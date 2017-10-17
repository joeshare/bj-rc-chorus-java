package cn.rongcapital.chorus.hadoop.autoconfigure;

import cn.rongcapital.chorus.hadoop.HadoopFSOperations;
import cn.rongcapital.chorus.hadoop.HadoopFSOperationsImpl;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yimin
 */
@Configuration
public class HadoopFSResourceAutoConfiguration {

    @Bean
    HadoopFSOperations hadoopFSResource(FileSystem fileSystem) {
        return new HadoopFSOperationsImpl(fileSystem);
    }
}
