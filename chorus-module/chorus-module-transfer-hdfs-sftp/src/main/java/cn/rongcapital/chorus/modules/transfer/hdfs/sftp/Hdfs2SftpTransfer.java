package cn.rongcapital.chorus.modules.transfer.hdfs.sftp;

import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.provider.sftp.SftpFileObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by hhlfl on 2017-8-14.
 */
@Slf4j
public class Hdfs2SftpTransfer {
   private FileSystem fileSystem;
   private static final long maxSize = 400*1024*1024l;
   private static final String tmplocalDir = "/tmp/hdfs2sftp";
   private SftpFileSystem sftpFileSystem;

   public Hdfs2SftpTransfer(SftpFileSystem sftpFileSystem){
       this.sftpFileSystem = sftpFileSystem;
       init();
   }

   private void init(){
       try {
           Configuration configuration = new Configuration();
           fileSystem = FileSystem.get(configuration);
       } catch (IOException e) {
           log.error(e.getMessage(), e);
           throw new IllegalArgumentException("fileSystem init Exception.");
       }
   }

   public void copy(String hdfsPathStr, String sftpPathStr) throws Exception{
        Path hdfsPath = new Path(hdfsPathStr);
        Path sftpPath= new Path(sftpPathStr);
        upload(hdfsPath,sftpPath);
   }

    private void upload(Path hdfsPath, Path sftpPath)throws Exception{
       log.info("upload files from hdfs path to sftp path .");
       log.info("hdfs path:{},sftp path:{}",hdfsPath.toUri().toString(),sftpPath.toUri().toString());
        FileStatus status = fileSystem.getFileStatus(hdfsPath);
        if(status.isDirectory()){
            long size = fileSystem.getContentSummary(hdfsPath).getLength();
            if(size == 0) return;

            if(size<=maxSize){//整个目录直接打成压缩包上传。
                compressDirAndUpload(sftpPath,status);
                return;
            }

            FileStatus[] fileStatuses = fileSystem.listStatus(hdfsPath);
            List<FileStatus> dirList = Arrays.stream(fileStatuses).filter(fileStatus -> fileStatus.isDirectory()).collect(toList());
            List<FileStatus> fileList = Arrays.stream(fileStatuses).filter(fileStatus -> !fileStatus.isDirectory()).collect(toList());
            Iterator<FileStatus> iterator = fileList.iterator();
            size = 0;
            List<Path> paths = new ArrayList<>();
            int index=0;
            while (iterator.hasNext()){
                FileStatus fileStatus = iterator.next();
                size += fileSystem.getContentSummary(fileStatus.getPath()).getLength();
                paths.add(fileStatus.getPath());
                iterator.remove();
                if(size>= maxSize || fileList.isEmpty()){
                    index++;
                    Path outputPath = new Path(sftpPath,String.format("%s_%d.zip",hdfsPath.getName(),index));
                    compressFilesAndUpload(outputPath,hdfsPath.getName(),paths.toArray(new Path[paths.size()]));
                    paths.clear();
                    size=0;
                }
            }

            //深度遍历
            for(FileStatus fileStatus : dirList){
                Path outputPath = new Path(sftpPath, fileStatus.getPath().getName());
                upload(fileStatus.getPath(), outputPath);
            }

        }else{
            Path output = sftpPath;
            if(!FilenameUtils.isExtension(sftpPath.getName(),"zip")) {
                output = new Path(sftpPath, String.format("%s.zip", Files.getNameWithoutExtension(hdfsPath.getName())));
            }

            compressFilesAndUpload(output,hdfsPath.getName(), hdfsPath);
        }
    }

    private void compressDirAndUpload(Path sftpOutput, FileStatus dir) throws Exception {
        File tmpDir = new File(tmplocalDir);
        if(!tmpDir.exists()) tmpDir.mkdir();
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile(dir.getPath().getName(), ".zip", tmpDir);
            Path sftpOutputPath = new Path(sftpOutput, String.format("%s.zip", dir.getPath().getName()));
            //压缩
            ZipFileUtils.compressHdfsDirToZip(tmpFile, dir.getPath(), fileSystem);
            //传输
            transfer(tmpFile, sftpOutputPath);
            log.info("upload directory:{} to stfp:{} success.", dir.getPath().toUri().toString(),sftpOutput.toUri().toString());
        }finally {
            //删除
            if(tmpFile != null && tmpFile.exists())
                tmpFile.delete();
        }

    }

    private void compressFilesAndUpload(Path sftpOutput, String parentName, Path ... files) throws Exception {
        File tmpDir = new File(tmplocalDir);
        if(!tmpDir.exists()) tmpDir.mkdir();

        File tmpFile = null;
        try {
            tmpFile = File.createTempFile(parentName, ".zip", tmpDir);
            //压缩
            ZipFileUtils.compressHdfsFilesToZip(tmpFile, fileSystem, files);
            //传输
            transfer(tmpFile, sftpOutput);
        }finally {
            //删除
            if(tmpFile != null && tmpFile.exists())
                tmpFile.delete();
        }
    }

    private void transfer(File zipFile, Path sftpOutput) throws Exception{
        InputStream in = null;
        OutputStream out = null;
        try {
            SftpFileObject sftpFileObject = sftpFileSystem.resolveFileObject(sftpOutput.toUri());
            in = new FileInputStream(zipFile);
            out = sftpFileObject.getOutputStream();
            IOUtils.copy(in, out);
            log.info("upload {} to {} success.", zipFile.getName(),sftpOutput.toUri().toString());
        }finally {
            try{
                if(in != null)in.close();
            }catch (IOException ex){}

            try{
                if(out != null) out.close();
            }catch (IOException ex){}
        }
    }
}
