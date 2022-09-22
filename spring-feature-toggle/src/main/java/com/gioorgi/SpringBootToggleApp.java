package com.gioorgi;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class SpringBootToggleApp {

    /**
     * Concrete implementaiton is selected by Spring and drived by an application configuration property
     * Optionally the property can be driven by a environemnt variable
     */
    @Autowired
    FeatureX feature;
    
    public static void main(String[] args) {
        SpringApplication.run(SpringBootToggleApp.class, args);
    }

    @PostConstruct
    public void initApplication() {
        log.info("%%% Demo Ready %%%");
        log.info("XX Feature:"+feature.getSomething(23));
    }
}
