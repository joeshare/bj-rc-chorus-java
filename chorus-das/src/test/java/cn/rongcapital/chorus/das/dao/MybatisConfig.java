package cn.rongcapital.chorus.das.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Mybatis相关配置
 *
 */
@Configuration
@MapperScan(basePackages={"cn.rongcapital.chorus.core.*.repository","cn.rongcapital.chorus.das.dao"},sqlSessionFactoryRef=MybatisConfig.SQL_SESSION_FACTORY_NAME)
public class MybatisConfig {
    public static final String SQL_SESSION_FACTORY_NAME = "sessionFactoryChorus";
    public static final String TX_MANAGER = "txManagerChorus";

    private Interceptor pageHelper;

    @Bean(name = "datasourceChorus")
    @Primary
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                        .setType(EmbeddedDatabaseType.H2)
                        .setName("jdbc:h2:mem:chorus;MODE=MySQL")
                        .addScript("classpath:db/schema.sql")
                        .addScript("classpath:db/init_data.sql")
                        .build();
    }

    @Bean(name = SQL_SESSION_FACTORY_NAME)
    @Primary
    @DependsOn(value = "pageHelper")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource());
        sessionFactoryBean.setPlugins(new Interceptor[]{pageHelper});
        return sessionFactoryBean.getObject();
    }

    @Bean(name = TX_MANAGER)
    @Primary
    public DataSourceTransactionManager transactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }

    /**
     * MyBatis分页插件
     */
    @Bean(name = "pageHelper")
    public Interceptor pageHelper() {
        pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("dialect", "H2");
        pageHelper.setProperties(properties);
        return pageHelper;
    }
}
