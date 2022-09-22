package com.gioorgi.quarkus;

// import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
// @ConditionalOnProperty(value="application.hotfix", havingValue = "0")
@Slf4j
public class DefaultBrokenImplementation  implements FeatureX{

    public String getSomething(int i){
        return "not fixed";
    }
    
}
