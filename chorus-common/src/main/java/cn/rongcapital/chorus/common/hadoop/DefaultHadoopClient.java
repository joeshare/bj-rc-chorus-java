package cn.rongcapital.chorus.common.hadoop;

import cn.rongcapital.chorus.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.HdfsConstants;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Li.ZhiWei
 */
@Component
@Slf4j
public class DefaultHadoopClient implements HadoopClient, InitializingBean {

    public static final String FILESYSTEM_DEFAULT_GROUP = "hadoop";
    public static final short FILESYSTEM_PROJECT_DEFAULT_PERMISSION = 488;
    public static final short FILESYSTEM_PROJECT_PERMISSION_777 = 511;
    public static final short FILESYSTEM_PROJECT_PERMISSION_770 = 504;
    public static final String FILESYSTEM_CHORUS_PATH = "/chorus/project/%s/hive";

    @Autowired
    private FileSystem fileSystem;

    public void readFileAndWrite(String file, OutputStream output, boolean autoCloseStream) throws IOException {
        Path path = new Path(file);
        readFileAndWrite(path, output, autoCloseStream);

        // if (autoCloseStream) {
        // closeStream(output);
        // }
    }

    public void readFileAndWrite(RemoteIterator<LocatedFileStatus> iterator, OutputStream output,
            boolean autoCloseStream) throws IOException {
        while (iterator.hasNext()) {
            readFileAndWrite(iterator.next().getPath(), output, autoCloseStream);
        }

        // if (autoCloseStream) {
        // closeStream(output);
        // }
    }

    public void readFileAndWrite(Path filepath, OutputStream output, boolean autoCloseStream) throws IOException {
        if (!fileSystem.exists(filepath)) {
            FileNotFoundException fileNotFoundexception = new FileNotFoundException();
            log.error(filepath + " does not exists", fileNotFoundexception);
            throw fileNotFoundexception;
        }

        FSDataInputStream fsinput = fileSystem.open(filepath);

        byte[] b = new byte[2048];
        int numBytes = 0;
        while ((numBytes = fsinput.read(b)) > 0) {
            output.write(b, 0, numBytes);
        }

        fsinput.close();
    }

    public void readFileAndWriteWithZip(ZipOutputStream zipOutput, List<String> filesUri, boolean autoCloseStream)
            throws IOException {

        for (String u : filesUri) {
            RemoteIterator<LocatedFileStatus> iterator = listFiles(u, false);

            zipOutput.putNextEntry(new ZipEntry(u + ".txt"));
            readFileAndWrite(iterator, zipOutput, false);
            zipOutput.closeEntry();
        }

        // RemoteIterator<LocatedFileStatus> iteratorDir = listFiles(date,
        // false);
        //
        // while(iteratorDir.hasNext())
        // {
        // LocatedFileStatus iteratorFile = iteratorDir.next();
        //
        // if(iteratorFile.isFile()){
        // Path filepath = iteratorFile.getPath();
        //
        // if(filesUri.contains(filepath.toString())){
        // zipOutput.putNextEntry(new ZipEntry(filepath.getName()+".txt"));
        // RemoteIterator<LocatedFileStatus> filesItertor =
        // listFiles(filepath.toString(),false);
        // readFileAndWrite(filesItertor, zipOutput);
        // zipOutput.closeEntry();
        // }
        // }
        // }
        //
        // zipOutput.close();
    }

    public RemoteIterator<LocatedFileStatus> listFiles(String dir, boolean recursive) throws IOException {
        Path path = isExistedPath(dir);
        RemoteIterator<LocatedFileStatus> ritr = fileSystem.listFiles(path, recursive);
        return ritr;
    }

    @Override
    public RemoteIterator<LocatedFileStatus> listFiles(String dir) throws IOException{
        Path path = isExistedPath(dir);
        RemoteIterator<LocatedFileStatus> listLocatedStatus = fileSystem.listLocatedStatus(path);
        return listLocatedStatus;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    /**
     * 关闭资源流
     * 
     * @param output 可变参数(关闭顺序按照传入的顺序)
     * @throws IOException
     */
    private void closeStream(OutputStream... output) throws IOException {
        if (output != null && output.length > 0) {
            try {
                for (OutputStream outputStream : output) {
                    outputStream.flush();
                }
            } finally {
                for (OutputStream outputStream : output) {
                    outputStream.close();
                }
            }
        }
    }

    @Override
    public boolean mkdir(String dir, String user) {
        return mkdir(dir, FILESYSTEM_PROJECT_DEFAULT_PERMISSION, user);
    }

    @Override
    public boolean mkdir(String dir, short permission, String user) {
        if (StringUtils.isEmpty(dir)) {
            return false;
        }
        FsPermission fsPermission = new FsPermission(permission);
        return mkdir(new Path(dir), fsPermission, user);
    }

    @Override
    public boolean mkdir(Path path, FsPermission fsPermission, String user) {
        try {
            if (fileSystem.exists(path)) {
                if (fileSystem.isDirectory(path)) {
                    log.info("path [" + path + "] exists with no change.");
                    return true;
                } else {
                    log.error("{} is not path.", path.toString());
                    return false;
                }
            }
            fileSystem.mkdirs(path, fsPermission);
            fileSystem.setOwner(path, user, FILESYSTEM_DEFAULT_GROUP);
            // mkdir传的权限被hadoop client umask限制。单独指定一次。
            fileSystem.setPermission(path, fsPermission);
        } catch (IOException e) {
            log.error("Failed to mkdir [" + path + "]", e);
            return false;
        }
        return true;
    }

    public boolean setSpaceQuota(String dir, long spaceQuota, String unit) {
        if (fileSystem instanceof DistributedFileSystem) {
            DistributedFileSystem dFileSystem = (DistributedFileSystem) fileSystem;
            try {
                Path path = new Path(dir);
                if (StringUtils.isEmpty(unit)) {
                    dFileSystem.setQuota(path, HdfsConstants.QUOTA_DONT_SET, spaceQuota);
                } else {
                    long value = QuotaEnum.getValue(unit);
                    if (value <= 0) {
                        log.error("Failed to get unit [" + unit + "] info.");
                        return false;
                    } else {
                        dFileSystem.setQuota(path, HdfsConstants.QUOTA_DONT_SET, spaceQuota * value);
                    }
                }
            } catch (IOException e) {
                log.error("Failed to set quota [" + spaceQuota + unit + "] of path [" + dir + "]", e);
                return false;
            }
            return true;
        } else {
            log.error("Can not get distributedfileSystem .");
            return false;
        }
    }

    @Override
    public Long getTotalDataNum(Set<String> projectCodes) {
        Long num = 0L;
        try {
            for(String projectCode:projectCodes){
                Path path = new Path(String.format(FILESYSTEM_CHORUS_PATH, projectCode));
                if(fileSystem.exists(path)){
                    Long result = fileSystem.getContentSummary(path).getLength();
                    num += result;
                }
            }
        } catch (IOException e) {
            log.error("Failed to get total used data num.");
        }
        return num;
    }

    @Override
    public boolean allowSnapshot(String dir) {
        try {
            if (fileSystem instanceof DistributedFileSystem) {
                DistributedFileSystem dFileSystem = (DistributedFileSystem) fileSystem;
                dFileSystem.allowSnapshot(new Path(dir));
            } else {
                log.error("Can not get distributedfileSystem .");
                return false;
            }
            return true;
        } catch (IllegalArgumentException e) {
            log.error("Can not allow directory " + dir + "start snapshot", e);
        } catch (IOException e) {
            log.error("Can not allow directory " + dir + "start snapshot", e);
        }
        return false;
    }

    public boolean createSnapshot(String dir, String name) {
        try {
            fileSystem.createSnapshot(new Path(dir));

            return true;
        } catch (IllegalArgumentException e) {
            log.error("Can not create snapshot for directory " + dir, e);
        } catch (IOException e) {
            log.error("Can not create snapshot for directory " + dir, e);
        }
        return false;
    }

    private Path isExistedPath(String dir) throws IOException, FileNotFoundException {
        Path path = new Path(dir);

        if (!fileSystem.exists(path)) {
            FileNotFoundException fileNotFoundexception = new FileNotFoundException();
            log.error(dir + " does not exists", fileNotFoundexception);
            throw fileNotFoundexception;
        }
        return path;
    }

}
