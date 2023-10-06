package com.github.system.base.config.datasource;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.github.system.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@MapperScan(basePackages = {"com.github.**.dao.**", "com.github.**.**.dao.**", "com.github.**.**.mapper.**"}, sqlSessionFactoryRef = "db1SqlSessionFactory")
public class DataSourceConfig {
    @Value("${system.datasource.type}")
    private String type;
    @Value("${system.datasource.url}")
    private String url;
    @Value("${system.datasource.username}")
    private String userName;
    @Value("${system.datasource.password}")
    private String passWord;

    @Bean
    @ConditionalOnProperty(name = "system.datasource.type", havingValue = "mysql")
    public DataSource mysqlDataSource() {
        log.info("数据库类型：mysql");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(passWord);
        return dataSource;
    }

    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource sqliteDataSource() throws FileNotFoundException {
        log.info("数据库类型：sqlite");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        // 检测项目部署路径下是否存在db文件
        Path dbPath = Paths.get(SpringUtil.getApplicationPath(), "db", "main.db");
        if (!FileUtil.exists(dbPath, true)) {
            log.info("未检测到db文件{}，将释放一个全新的db文件", dbPath);
            // 释放db文件
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("main.db");
            FileUtil.mkParentDirs(dbPath);
            FileOutputStream fos = new FileOutputStream(dbPath.toFile());
            IoUtil.copy(inputStream, fos);
            IoUtil.closeIfPosible(inputStream);
            IoUtil.closeIfPosible(fos);
        }
        dataSource.setUrl("jdbc:sqlite::" + dbPath);
        return dataSource;
    }

    @Bean(name = "db1SqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean sessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setMapperLocations(resolveMapperLocations());
        return sessionFactoryBean.getObject();
    }

    public Resource[] resolveMapperLocations() {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<String> mapperLocations = new ArrayList<>();
        mapperLocations.add("classpath:mapper/**.xml");
        mapperLocations.add("classpath:mapper/**/**.xml");
        List<Resource> resources = new ArrayList<>();
        for (String mapperLocation : mapperLocations) {
            try {
                Resource[] mappers = resourceResolver.getResources(mapperLocation);
                resources.addAll(Arrays.asList(mappers));
            } catch (IOException e) {
                // ignore
            }
        }
        return resources.toArray(new Resource[0]);
    }

    @Bean(name = "db1SqlSessionFactory")
    public SqlSessionTemplate sqlSessionFactoryTemplate(@Qualifier("db1SqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
