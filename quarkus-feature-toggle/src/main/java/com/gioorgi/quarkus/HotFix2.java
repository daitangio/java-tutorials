package com.gioorgi.quarkus;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

// @Service
@Slf4j
public class HotFix2  implements FeatureX{

    public String getSomething(int i){
        return "FixedAgain. Now returns good output:"+(i+1);
    }
    @PostConstruct
    public void init(){
        log.info("HotFix2 is here");
    }
    
}
