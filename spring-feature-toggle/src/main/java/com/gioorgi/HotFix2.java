package com.gioorgi;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@ConditionalOnProperty(value="application.hotfix", havingValue = "2")
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
