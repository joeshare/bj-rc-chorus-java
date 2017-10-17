package cn.rongcapital.chorus.common.hadoop;

import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.permission.FsPermission;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipOutputStream;

/**
 * @author lizhiwei
 */
public interface HadoopClient {

    void readFileAndWrite(String file, OutputStream output, boolean autoCloseStream) throws IOException;

    void readFileAndWrite(RemoteIterator<LocatedFileStatus> iterator, OutputStream output, boolean autoCloseStream)
            throws IOException;

    void readFileAndWrite(Path filepath, OutputStream output, boolean autoCloseStream) throws IOException;

    void readFileAndWriteWithZip(ZipOutputStream zipOutput, List<String> filesUri, boolean autoCloseStream)
            throws IOException;

    RemoteIterator<LocatedFileStatus> listFiles(String dir, boolean recursive) throws IOException;

    RemoteIterator<LocatedFileStatus> listFiles(String dir) throws IOException;

    /**
     * @param dir
     * @return
     * @author yunzhong
     * @time 2017年6月12日下午1:53:57
     */
    boolean mkdir(String dir, String user);

    /**
     * @param path
     * @param fsPermission
     * @return
     * @author yunzhong
     * @time 2017年6月12日下午1:54:02
     */
    boolean mkdir(Path path, FsPermission fsPermission, String user);

    /**
     * @param dir
     * @param permission
     * @param user
     * @return
     * @author yunzhong
     * @time 2017年6月22日下午2:35:23
     */
    boolean mkdir(String dir, short permission, String user);

    /**
     * 
     * 为路径设置容量
     * 
     * @param dir
     * @param spaceQuota
     * @param unit
     * @return
     * @author yunzhong
     * @time 2017年6月14日上午11:27:56
     */
    boolean setSpaceQuota(String dir, long spaceQuota, String unit);

    /**
     * 查询hdfs数据总量
     * @return
     */
    Long getTotalDataNum(Set<String> projectCodes);
    /**
     * 开启hdfs snapshot 功能
     * @param dir 目录
     * @return
     */
    boolean allowSnapshot(String dir);
}
