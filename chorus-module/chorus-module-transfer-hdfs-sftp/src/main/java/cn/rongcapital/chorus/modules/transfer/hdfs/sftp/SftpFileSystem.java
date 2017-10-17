package cn.rongcapital.chorus.modules.transfer.hdfs.sftp;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.SftpATTRS;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.IdentityInfo;
import org.apache.commons.vfs2.provider.sftp.SftpFileObject;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.util.Progressable;
import org.kitesdk.shaded.com.google.common.base.Preconditions;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class SftpFileSystem{
    public static final int DEFAULT_BLOCK_SIZE = 4 * 1024;

    private static final String SCHEME = "sftp";
    private static final int DEFAULT_PORT = 22;

    public static final String FS_SFTP_USER_PREFIX = "fs.sftp.user.";
    public static final String FS_SFTP_HOST = "fs.sftp.host";
    public static final String FS_SFTP_HOST_PORT = "fs.sftp.host.port";
    public static final String FS_SFTP_PASSWORD_PREFIX = "fs.sftp.password.";
    public static final String FS_SFTP_KEY_PREFIX = "fs.sftp.key.";

    public static final String VFS_SFTP_SSHDIR = "/tmp/rc/chorus/.sftp/";
    private static final String VFS_SFTP_KNOWN_HOST_FILE_PATH = "/tmp/rc/chorus/.sftp/known_hosts";


    private FileSystemOptions fsOptions = new FileSystemOptions();


    public String getScheme() {
        return SCHEME;
    }


    protected int getDefaultPort() {
        return DEFAULT_PORT;
    }


    public void initialize(URI uri, Configuration conf) throws IOException { // get
        // get host information from uri (overrides info in conf)
        String host = uri.getHost();
        host = (host == null) ? conf.get(FS_SFTP_HOST, null) : host;
        if (host == null) {
            throw new IOException("Invalid host specified");
        }
        conf.set(FS_SFTP_HOST, host);

        // get port information from uri, (overrides info in conf)
        int port = uri.getPort();
        port = (port == -1) ? DEFAULT_PORT : port;
        conf.setInt(FS_SFTP_HOST_PORT, port);

        // get user/password information from URI (overrides info in conf)
        String userAndPassword = uri.getUserInfo();
        if (userAndPassword == null) {
            userAndPassword = (conf.get(FS_SFTP_USER_PREFIX + host, null) + ":" +
                    conf.get(FS_SFTP_PASSWORD_PREFIX + host, null));
        }
        String[] userPasswdInfo = userAndPassword.split(":");
        if (StringUtils.isNotBlank(conf.get(FS_SFTP_KEY_PREFIX + host))) {
            conf.set(FS_SFTP_USER_PREFIX + host, userPasswdInfo[0]);
        } else {
            Preconditions.checkState(userPasswdInfo.length > 1,
                    "Invalid username / password");
            conf.set(FS_SFTP_USER_PREFIX + host, userPasswdInfo[0]);
            conf.set(FS_SFTP_PASSWORD_PREFIX + host, userPasswdInfo[1]);
        }
        log.info("before preparing fs option, config: {}", conf);
        prepareFsOption(conf);
    }

    private void prepareFsOption(Configuration conf) throws IOException {
        String host = conf.get(FS_SFTP_HOST);
        String user = conf.get(FS_SFTP_USER_PREFIX + host);
        String password = conf.get(FS_SFTP_PASSWORD_PREFIX + host);
        String keyStr = conf.get(FS_SFTP_KEY_PREFIX + host);

        SftpFileSystemConfigBuilder builder = SftpFileSystemConfigBuilder.getInstance();
        builder.setPreferredAuthentications(fsOptions, "publickey,password");
        builder.setStrictHostKeyChecking(fsOptions, "no");
        builder.setUserDirIsRoot(fsOptions, false);
        builder.setCompression(fsOptions, "zlib,none");
        builder.setFileNameEncoding(fsOptions, "UTF-8");
        builder.setTimeout(fsOptions, 0);
        builder.setKnownHosts(fsOptions, new File(VFS_SFTP_KNOWN_HOST_FILE_PATH));

        System.setProperty("vfs.sftp.sshdir", VFS_SFTP_SSHDIR);
        if (StringUtils.isNotBlank(keyStr)) {
//            log.info("host:{}, private key:{}", host, keyStr);
            File privateKeyTempFile = createKeyTempFile(keyStr);
            IdentityInfo key = new IdentityInfo(privateKeyTempFile);
            builder.setIdentityRepositoryFactory(fsOptions, JSch::getIdentityRepository);
            builder.setIdentityInfo(fsOptions, key);
        } else {
            DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(fsOptions,
                    new StaticUserAuthenticator(host, user, password));
        }
    }

    private static File createKeyTempFile(String keyStr) throws IOException {
        File privateKeyTempFile;
        int retry = 0;
        do {
            privateKeyTempFile = new File(VFS_SFTP_SSHDIR +
                    System.currentTimeMillis());
            if (privateKeyTempFile.createNewFile())
                break;
            if (retry >= 3)
                throw new RuntimeException("Create temp private key file failed!");
            retry++;
        } while (true);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(privateKeyTempFile));
        bos.write(keyStr.getBytes());
        bos.flush();
        bos.close();
        return privateKeyTempFile;
    }

    public void copyFrom(Path dest, FileObject src)throws  Exception{
        FileObject destObject = VFS.getManager().resolveFile(dest.toUri().toString(), fsOptions);
        destObject.copyFrom(src, Selectors.SELECT_SELF);
    }

    public SftpFileObject resolveFileObject(URI uri)throws  Exception{
        if(uri == null)
            return null;

        return (SftpFileObject) VFS.getManager().resolveFile(uri.toString(), fsOptions);
    }



    public FileStatus[] listStatus(Path f) throws FileNotFoundException, IOException {
        List<FileStatus> collect = Arrays.stream(
                VFS.getManager().resolveFile(f.toUri().toString(), fsOptions).getChildren())
                .map(SftpFileSystem::getFileStatus).collect(Collectors.toList());
        return collect.toArray(new FileStatus[collect.size()]);
    }


    public FileStatus getFileStatus(Path f) throws IOException {
        FileObject fo = VFS.getManager().resolveFile(f.toUri().toString(), fsOptions);
        return getFileStatus(fo);
    }

    private static FileStatus getFileStatus(FileObject fileObject) {
        try {
            SftpATTRS attrs = getSftpATTRS(fileObject);
            return new FileStatus(attrs.getSize(),
                    attrs.isDir(),
                    1, DEFAULT_BLOCK_SIZE,
                    attrs.getMTime(),
                    attrs.getATime(),
                    FsPermission.createImmutable((short) attrs.getPermissions()),
                    attrs.getUId() + "",
                    attrs.getGId() + "",
                    null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            log.error("Caught error while reading file status.", e);
            return null;
        }
    }

    private static SftpATTRS getSftpATTRS(FileObject fileObject)
            throws NoSuchFieldException, IllegalAccessException {
        Field attrsField = SftpFileObject.class.getDeclaredField("attrs");
        attrsField.setAccessible(true);
        return (SftpATTRS) attrsField.get(fileObject);
    }
}
