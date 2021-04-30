package com.xxl.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@Profile("!test")
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryOp",
        transactionManagerRef = "transactionManagerOp",
        basePackages = {"com.xxl.repository"})
public class DataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Autowired
    private DbConfig masterDbConfig;

//    @Autowired
//    private DbConfig slaveDbConfig;

    @Autowired
    private DataSource dataSource;

    @Configuration("masterDbConfig")
    @ConfigurationProperties(prefix = "datasource.master")
    public class MasterDbConfig extends DbConfig {
    }

//    @Configuration("slaveDbConfig")
//    @ConfigurationProperties(prefix = "datasource.slave")
//    public class SlaveDbConfig extends DbConfig {
//    }

    /**
     * get data source.
     * @return data source.
     */
    @Bean
    public DataSource getDataSource() {
        if (dataSource == null) {
            HikariDataSource ds = new HikariDataSource();
            ds.setMaximumPoolSize(30);
            ds.setDataSourceClassName(masterDbConfig.getDriverClassName());
            ds.addDataSourceProperty("serverName", masterDbConfig.getHost());
            ds.addDataSourceProperty("databaseName", masterDbConfig.getSchema());
            ds.addDataSourceProperty("user", masterDbConfig.getUsername());
            ds.addDataSourceProperty("password", masterDbConfig.getPassword());
            ds.addDataSourceProperty("port", masterDbConfig.getPort());
            ds.setConnectionTimeout(masterDbConfig.getConnectionTimeout());
            ds.setMaxLifetime(masterDbConfig.getMaxLifetime());
            ds.setIdleTimeout(masterDbConfig.getIdleTimeout());
            ds.setMinimumIdle(masterDbConfig.getMinIdle());
            ds.setMaximumPoolSize(masterDbConfig.getMaxPoolSize());
            dataSource = ds;
        }
        return dataSource;
    }

    @Primary
    @Bean(name = "entityManagerFactoryOp")
    public LocalContainerEntityManagerFactoryBean customerEntityManagerFactory(
            EntityManagerFactoryBuilder builder, DbConfig masterDbConfig) {
        LocalContainerEntityManagerFactoryBean bean = builder.dataSource(getDataSource()).build();
        bean.setPackagesToScan("com.xxl.entity.po");
        return bean;
    }

    @Primary
    @Bean(name = "transactionManagerOp")
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactoryOp") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
