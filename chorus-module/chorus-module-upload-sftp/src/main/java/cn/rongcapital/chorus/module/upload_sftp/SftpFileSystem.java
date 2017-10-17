package cn.rongcapital.chorus.module.upload_sftp;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.SftpATTRS;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
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

/**
 * Sftp file system using commons-vfs2 from ASF extends {@code org.apache.hadoop.fs.FileSystem }.
 * Created by alan on 20/01/2017.
 */
@Slf4j
@InterfaceAudience.Public
@InterfaceStability.Stable
public class SftpFileSystem extends FileSystem {

    public static final int DEFAULT_BUFFER_SIZE = 1024 * 1024;
    public static final int DEFAULT_BLOCK_SIZE = 4 * 1024;

    private static final String SCHEME = "sftp";
    private static final int DEFAULT_PORT = 22;

    public static final String FS_SFTP_USER_PREFIX = "fs.sftp.user.";
    public static final String FS_SFTP_HOST = "fs.sftp.host";
    public static final String FS_SFTP_HOST_PORT = "fs.sftp.host.port";
    public static final String FS_SFTP_PASSWORD_PREFIX = "fs.sftp.password.";
    public static final String FS_SFTP_KEY_PREFIX = "fs.sftp.key.";

    public static final String VFS_SFTP_SSHDIR = "/tmp/rc/chorus/.sftp/";
    //    private static final String VFS_SFTP_SSHDIR = "/Users/alan/.sftp/";
    private static final String VFS_SFTP_KNOWN_HOST_FILE_PATH = "/tmp/rc/chorus/.sftp/known_hosts";
//    private static final String VFS_SFTP_KNOWN_HOST_FILE_PATH = "/Users/alan/.sftp/known_hosts";


    private URI uri;

    private FileSystemOptions fsOptions = new FileSystemOptions();

    @Override
    public String getScheme() {
        return SCHEME;
    }

    @Override
    protected int getDefaultPort() {
        return DEFAULT_PORT;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public void initialize(URI uri, Configuration conf) throws IOException { // get
        super.initialize(uri, conf);
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
            setConf(conf);
        } else {
            Preconditions.checkState(userPasswdInfo.length > 1,
                    "Invalid username / password");
            conf.set(FS_SFTP_USER_PREFIX + host, userPasswdInfo[0]);
            conf.set(FS_SFTP_PASSWORD_PREFIX + host, userPasswdInfo[1]);
            setConf(conf);
        }
        this.uri = uri;
        log.info("before preparing fs option, config: {}", getConf());
        prepareFsOption();
    }

    private void prepareFsOption() throws IOException {
        Configuration conf = getConf();
        String host = conf.get(FS_SFTP_HOST);
//        int port = conf.getInt(FS_SFTP_HOST_PORT, DEFAULT_PORT);
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
//        builder.setPreferredAuthentications(fsOptions, "privateKey");
        System.setProperty("vfs.sftp.sshdir", VFS_SFTP_SSHDIR);
        if (StringUtils.isNotBlank(keyStr)) {
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

    @Override
    public FSDataInputStream open(Path f, int bufferSize) throws IOException {
        FileObject fileObject = VFS.getManager().resolveFile(f.toUri().toString(), fsOptions);
        return new FSDataInputStream(new SftpInputStream((SftpFileObject) fileObject));
    }

    @Override
    public FileStatus[] listStatus(Path f) throws FileNotFoundException, IOException {
        List<FileStatus> collect = Arrays.stream(
                VFS.getManager().resolveFile(f.toUri().toString(), fsOptions).getChildren())
                .map(SftpFileSystem::getFileStatus).collect(Collectors.toList());
        return collect.toArray(new FileStatus[collect.size()]);
    }

    @Override
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

    @Override
    public void setWorkingDirectory(Path new_dir) {
        throw new RuntimeException("SFTP don't have working dictionary");
    }

    @Override
    public Path getWorkingDirectory() {
        throw new RuntimeException("SFTP don't have working dictionary");
    }

    @Override
    public boolean mkdirs(Path f, FsPermission permission) throws IOException {
        throw new IOException("Doesn't support mkdirs for now.");
    }

    @Override
    public FSDataOutputStream create(Path f, FsPermission permission, boolean overwrite,
                                     int bufferSize, short replication, long blockSize, Progressable progress)
            throws IOException {
        throw new IOException("Doesn't support create for now.");
    }

    @Override
    public FSDataOutputStream append(Path f, int bufferSize, Progressable progress) throws IOException {
        throw new IOException("Doesn't support append for now.");
    }

    @Override
    public boolean rename(Path src, Path dst) throws IOException {
        throw new IOException("Doesn't support rename for now.");
    }

    @Override
    public boolean delete(Path f, boolean recursive) throws IOException {
        throw new IOException("Doesn't support delete for now.");
    }

}
