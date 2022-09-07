package com.baeldung;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.*;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class SpringBootApp extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootApp.class, args);
    }

    @PostConstruct
    public void initApplication() {
        this.log.info("%%% Demo Ready %%%");
    }
}