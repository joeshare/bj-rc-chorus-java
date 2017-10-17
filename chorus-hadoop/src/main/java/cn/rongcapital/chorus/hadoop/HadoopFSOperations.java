package cn.rongcapital.chorus.hadoop;

import org.apache.hadoop.fs.ContentSummary;

import java.io.IOException;

/**
 * @author yimin
 */
public interface HadoopFSOperations {
    ContentSummary contentSummary(String pathString) throws IOException;
}
