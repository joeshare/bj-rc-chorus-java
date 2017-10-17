package cn.rongcapital.chorus.hadoop;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * @author yimin
 */
@Slf4j
public class HadoopFSOperationsImpl implements HadoopFSOperations {
    private final FileSystem fileSystem;

    public HadoopFSOperationsImpl(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public @Nullable
    ContentSummary contentSummary(@Nonnull String pathString) throws IOException {
        if (!StringUtils.isBlank(pathString)) {
            Path path = new Path(pathString);
            if (fileSystem.exists(path)) {
                return fileSystem.getContentSummary(path);
            } else {
                log.warn("hdfs file path {} not found.", pathString);
            }
        }
        return null;
    }
}
