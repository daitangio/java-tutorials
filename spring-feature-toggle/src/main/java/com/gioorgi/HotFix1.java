package com.gioorgi;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@ConditionalOnProperty(value="application.hotfix", havingValue = "1")
@Slf4j
public class HotFix1  implements FeatureX{

    public String getSomething(int i){
        return "Fixed. Now returns good output:"+i;
    }
    @PostConstruct
    public void init(){
        log.info("HotFix1 is here");
    }
    
}
