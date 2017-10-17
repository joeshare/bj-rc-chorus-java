package cn.rongcapital.chorus.modules.test.table.stats.util;

import java.io.IOException;

import org.apache.atlas.AtlasClientV2;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.context.annotation.Bean;

import cn.rongcapital.chorus.modules.table.stats.DataSource;
import cn.rongcapital.chorus.modules.table.stats.HiveTableStats;

@org.springframework.context.annotation.Configuration
public class TestConfig {

    @Bean(name = "hiveDataSource")
    public DataSource hiveDatasource() {
        DataSource dataSource = new DataSource();
        String hiveURL =  "jdbc:mysql://10.200.48.150:3306/hive";
        dataSource.setUrl(hiveURL);
        String hiveUser = "hive";
        dataSource.setUserName(hiveUser);
        String hivePassword ="hive";
        dataSource.setPassword(hivePassword);
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        return dataSource;
    }

    @Bean(name = "chorusDataSource")
    public DataSource chorusDatasource() {
        DataSource dataSource = new DataSource();
        String chorusURL = "jdbc:mysql://10.200.48.79:3306/chorus";
        dataSource.setUrl(chorusURL );
        String chorusUser= "dps";
        dataSource.setUserName(chorusUser);
        String chorusPassword= "Dps@10.200.48.MySQL";
        dataSource.setPassword(chorusPassword);
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        return dataSource;
    }

    @Bean
    public AtlasClientV2 atlasClient() {
        String atlasURL= "http://10.200.48.155:21000";
        String[] baseUrl = atlasURL.split(",");
        String atlasUser= "admin";
        String atlasPassword= "admin";
        String[] basicAuthUserNamePassword = new String[] { atlasUser, atlasPassword };
        AtlasClientV2 client = new AtlasClientV2(baseUrl, basicAuthUserNamePassword);
        return client;
    }

    @Bean
    public FileSystem fileSystem() throws IOException {
        Configuration conf = new Configuration();
        FileSystem fileSystem = FileSystem.get(conf);
        return fileSystem;
    }
    
    @Bean
    public HiveTableStats hiveTableStats(){
        try {
            return new HiveTableStats();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
