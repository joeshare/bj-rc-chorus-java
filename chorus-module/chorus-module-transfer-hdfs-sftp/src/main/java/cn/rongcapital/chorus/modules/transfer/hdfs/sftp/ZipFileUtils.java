package cn.rongcapital.chorus.modules.transfer.hdfs.sftp;

import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Created by hhlfl on 2017-8-15.
 */
public class ZipFileUtils {

    /**
     * 把文件压缩成zip格式
     *
     * @param files       需要压缩的文件
     * @param zipFilePath 压缩后的zip文件路径 ;
     */
    public static void compressFiles2Zip(File[] files, String zipFilePath) {
        if (files != null && files.length > 0) {
            if (isEndsWithZip(zipFilePath)) {
                ZipArchiveOutputStream zaos = null;
                try {
                    File zipFile = new File(zipFilePath);
                    zaos = new ZipArchiveOutputStream(zipFile);
                    // Use Zip64 extensions for all entries where they are
                    // required
                    zaos.setUseZip64(Zip64Mode.AsNeeded);

                    // 将每个文件用ZipArchiveEntry封装
                    // 再用ZipArchiveOutputStream写到压缩文件中
                    for (File file : files) {
                        if (file != null) {
                            ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(file, file.getName());
                            zaos.putArchiveEntry(zipArchiveEntry);
                            InputStream is = null;
                            try {
                                is = new FileInputStream(file);
                                byte[] buffer = new byte[1024 * 5];
                                int len = -1;
                                while ((len = is.read(buffer)) != -1) {
                                    // 把缓冲区的字节写入到ZipArchiveEntry
                                    zaos.write(buffer, 0, len);
                                }
                                // Writes all necessary data for this entry.
                                zaos.closeArchiveEntry();
                                is.close();
                                is = null;
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            } finally {
                                if (is != null)
                                    is.close();
                            }

                        }
                    }
                    zaos.finish();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        if (zaos != null) {
                            zaos.close();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            }

        }

    }


    public static void compressHdfsDirToZip(File outputFile, Path dir, FileSystem fileSystem) throws  Exception{
        if(fileSystem == null)
            fileSystem = FileSystem.get(new Configuration());

        if(!fileSystem.exists(dir))
            throw new Exception(String.format("directory:%s, is not exists.",dir.toUri().toString()));


        if (!isEndsWithZip(outputFile.getName()))
            throw new Exception(String.format(" outputFile is not a .zip file. outputFile:%s", outputFile.getPath()));


        ZipArchiveOutputStream zaos = null;
        try {
            zaos = new ZipArchiveOutputStream(outputFile);
            compressDir(fileSystem, zaos, dir, "");
        }finally {
            if (zaos != null) zaos.close();
        }
    }

    private static void compressDir(FileSystem fileSystem, ZipArchiveOutputStream zaos, Path dir, String parentPath) throws Exception {
        FileStatus[] fileStatuses = fileSystem.listStatus(dir);
        List<Path> filePaths = Stream.of(fileStatuses).filter(fileStatus -> !fileStatus.isDirectory()).map(fileStatus -> fileStatus.getPath()).collect(toList());
        List<Path> children = Stream.of(fileStatuses).filter(fileStatus -> fileStatus.isDirectory()).map(fileStatus -> fileStatus.getPath()).collect(toList());
        if(!filePaths.isEmpty()) {
            compressFile2Zip(fileSystem, zaos, filePaths.toArray(new Path[filePaths.size()]),parentPath);
        }

        for(Path child : children){
            String parent = child.getName();
            if(!StringUtils.isEmpty(parentPath)) parent = String.format("%s/%s", parentPath, child.getName());
            compressDir(fileSystem, zaos, child, parent);
        }

    }


    public static void compressHdfsFilesToZip(File outputFile,  FileSystem fileSystem, Path...files) throws Exception {
        compressHdfsFiles2Zip(files, outputFile, fileSystem);
    }



    private static void compressHdfsFiles2Zip(Path[] files, File zipFile, FileSystem fileSystem) throws Exception {
        if(fileSystem == null)
            fileSystem = FileSystem.get(new Configuration());


        if (files != null && files.length > 0) {
            if (isEndsWithZip(zipFile.getName())) {
                ZipArchiveOutputStream zaos = null;
                try {
                    zaos = new ZipArchiveOutputStream(zipFile);
                    // Use Zip64 extensions for all entries where they are
                    // required
                    zaos.setUseZip64(Zip64Mode.AsNeeded);
                    // 将每个文件用ZipArchiveEntry封装
                    // 再用ZipArchiveOutputStream写到压缩文件中
                    compressFile2Zip(fileSystem, zaos, files,null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        if (zaos != null) {
                            zaos.close();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            }

        }

    }


    private static void  compressFile2Zip(FileSystem fileSystem, ZipArchiveOutputStream zaos, Path[] files,String parentPath)throws Exception{
        for (Path  file : files) {
            if (file != null) {
                String fileName = file.getName();
                if(!StringUtils.isEmpty(parentPath)) fileName = String.format("%s/%s",parentPath, file.getName());

                ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(fileName);
                zaos.putArchiveEntry(zipArchiveEntry);
                InputStream is = null;
                try {
                    is = fileSystem.open(file);
                    byte[] buffer = new byte[1024 * 5];
                    int len = -1;
                    while ((len = is.read(buffer)) != -1) {
                        // 把缓冲区的字节写入到ZipArchiveEntry
                        zaos.write(buffer, 0, len);
                    }
                    // Writes all necessary data for this entry.
                    zaos.closeArchiveEntry();
                    is.close();
                    is = null;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    if (is != null)
                        is.close();
                }

            }
        }
    }

    public static boolean isEndsWithZip(String fileName) {
        boolean flag = false;
        if (fileName != null && !"".equals(fileName.trim())) {
            if (fileName.endsWith(".ZIP") || fileName.endsWith(".zip")) {
                flag = true;
            }
        }
        return flag;
    }
}
