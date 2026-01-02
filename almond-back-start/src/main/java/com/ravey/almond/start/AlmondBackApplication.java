package com.ravey.almond.start;

import com.ravey.common.service.web.annotation.EnableGlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Ravey
 * @since 1.0.0
 */
@Slf4j
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.ravey.almond"})
@MapperScan("com.ravey.almond.service.dao.mapper")
@EnableGlobalExceptionHandler
public class AlmondBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlmondBackApplication.class, args);
    }
}
