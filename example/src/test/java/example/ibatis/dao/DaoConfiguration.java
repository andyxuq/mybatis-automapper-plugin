package example.ibatis.dao;

import andy.ibatis.plugin.ResultSetHandlerInteceptor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * User: andyxu
 * Date: 2018/11/28
 * Time: 11:02
 */
@Configuration
@ComponentScan(basePackages = {"example.ibatis.dao.**"})
@PropertySource("classpath:mysql.properties")
public class DaoConfiguration {

    @Bean(name = "dataSource")
    public HikariDataSource dataSource(
            @Value("${mysql.url}") String connUrl,
            @Value("${mysql.username}") String dbUserName,
            @Value("${mysql.password}") String dbPassword,
            @Value("${jdbc.mysql.driver}") String driverClassName,
            @Value("${mysql.initialSize}") int maxPoolSize) {

        HikariConfig conf = new HikariConfig();
        conf.setPoolName("springHikariCP");
        conf.setJdbcUrl(connUrl);
        conf.setDriverClassName(driverClassName);
        conf.setUsername(dbUserName);
        conf.setPassword(dbPassword);
        conf.setMaximumPoolSize(maxPoolSize);
        conf.setIdleTimeout(600000);
        conf.setConnectionInitSql("SELECT 1");
        return new HikariDataSource(conf);
    }

    @Bean(name = "sessionFactoryBean")
    public SqlSessionFactoryBean sessionFactoryBean(@Qualifier("dataSource") DataSource dataSource) throws IOException {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/example/ibatis/dao/mysql/**/*.xml"));
        bean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:mybatis-config.xml"));

        //add auto mapper plugin
        Interceptor[] plugins = new Interceptor[]{new ResultSetHandlerInteceptor()};
        bean.setPlugins(plugins);
        return bean;
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer conf = new MapperScannerConfigurer();
        conf.setSqlSessionFactoryBeanName("sessionFactoryBean");
        conf.setBasePackage("example.ibatis.dao.mysql");
        return conf;
    }

    @Bean
    public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }

}
