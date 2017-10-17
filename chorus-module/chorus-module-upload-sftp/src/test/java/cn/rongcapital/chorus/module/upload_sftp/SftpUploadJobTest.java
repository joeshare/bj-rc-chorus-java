package cn.rongcapital.chorus.module.upload_sftp;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IOUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;

import static cn.rongcapital.chorus.module.upload_sftp.SftpMeta.AUTH_TYPE_PASSWORD;

/**
 * Created by alan on 06/02/2017.
 */
public class SftpUploadJobTest {

    @Test
    public void step1WithPassword() throws Exception {
        String hdfsFileFolderPathStr = "/chorus/project/alan1/";

//        String sftpUriStr = String.format("sftp://%s:%s@%s:%s%s", "liyaguang",
//                "Rong.0714", "121.52.221.199", "1196", "/rc/local/liyaguang/test.htm");
        String sftpUriStr = "sftp://liyaguang:Rong.0714@121.52.221.199:1196/rc/local/liyaguang/test.htm";

        Path sftpFilePath = new Path(sftpUriStr);
        URI sftpFileUri = sftpFilePath.toUri();

        Configuration conf = new Configuration();

        String fileName = sftpFilePath.getName();
        SftpFileSystem sftpFs = new SftpFileSystem();
        sftpFs.initialize(sftpFileUri, conf);
        FSDataInputStream fsDIn = sftpFs.open(sftpFilePath, 1000);

        IOUtils.copyBytes(fsDIn, System.out, conf, true);
    }

    @Test
    public void step1WithKey() throws Exception {
        File keyFile = new File("/Users/alan/.ssh/liyaguang.pem");
        BufferedReader fbr = new BufferedReader(new FileReader(keyFile));
        String key = fbr.lines().reduce((pre, s) -> pre + "\n" + s).get();
        System.out.println(key);
        Path filePath = new Path(
                "sftp://sftp@121.52.221.199:1196/rc/local/sftp/chorus-module-upload-sftp-0.1.jar");
        String hdfsFileFolderPathStr = "";

        SftpUploadJob job = new SftpUploadJob();
        URI uri = filePath.toUri();
        String[] userInfo = uri.getUserInfo().split(":");
        job.step1(uri.getHost(), uri.getPort() + "",
                userInfo[0], SftpMeta.AUTH_TYPE_KEY, null, key,
                uri.getPath(), hdfsFileFolderPathStr, 0);
    }

}