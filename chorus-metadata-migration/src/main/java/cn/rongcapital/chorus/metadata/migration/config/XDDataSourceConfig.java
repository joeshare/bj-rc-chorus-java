package cn.rongcapital.chorus.metadata.migration.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * @author Li.ZhiWei
 */
@Configuration
@MapperScan(basePackages = "cn.rongcapital.chorus.das.xd.dao",sqlSessionFactoryRef = XDDataSourceConfig.SQL_SESSION_FACTORY_NAME)
public class XDDataSourceConfig {
    public static final String SQL_SESSION_FACTORY_NAME = "sessionFactoryXd";
    public static final String TX_MANAGER = "txManagerXd";

    @Bean(name = "datasourceXd")
    @ConfigurationProperties(prefix = "datasource.xd")
    public DataSource dataSourceXD() {
        DruidDataSource dataSource = new DruidDataSource();
      return dataSource;
    }

    @Bean(name = TX_MANAGER)
    public PlatformTransactionManager txManagerXD() {
        return new DataSourceTransactionManager(dataSourceXD());
    }

    @Bean(name = XDDataSourceConfig.SQL_SESSION_FACTORY_NAME)
    public SqlSessionFactory sqlSessionFactoryBean() throws Exception {

        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSourceXD());
        return sqlSessionFactoryBean.getObject();
    }
}
