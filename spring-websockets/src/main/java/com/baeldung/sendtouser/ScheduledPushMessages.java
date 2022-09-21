package com.baeldung.sendtouser;


import com.baeldung.websockets.OutputMessage;
import com.github.javafaker.Faker;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import static java.util.concurrent.TimeUnit.*;

import java.security.Principal;
/** Example of Event from server to a specific user */
@Service
@Slf4j
public class ScheduledPushMessages {


    
    private final SimpMessagingTemplate simpMessagingTemplate;
    
    private final Faker faker;

    @Autowired
    ConnectedUserManager connectedUserManager;
    
    public ScheduledPushMessages(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        faker = new Faker();
        this.log.info("Chuck Norris Ready");
    }
    
    boolean limit_already_reached=false;
    @Scheduled(fixedRate = 2, timeUnit = SECONDS)
    public void limitEcho(){
        final String time = new SimpleDateFormat("HH:mm").format(new Date()); 
        if(connectedUserManager.isMaxReached() && !limit_already_reached){
            limit_already_reached=true;
            
            for(String username: connectedUserManager.getConnectedUsers()){
                simpMessagingTemplate.convertAndSendToUser(username, "/queue/reply", 
                new OutputMessage("System","Limit user Reached!",time));                
            }
        }else if (!connectedUserManager.isMaxReached() && limit_already_reached) {
            limit_already_reached=false;
            for(String username: connectedUserManager.getConnectedUsers()){
                simpMessagingTemplate.convertAndSendToUser(username, "/queue/reply", 
                new OutputMessage("System","There is more space for friends. Current user loggedin:"+connectedUserManager.size(),time));                
            }
        }



    }



    @Scheduled(fixedRate = 59, timeUnit = SECONDS)
    public void sendMessage() {

        //for (String username: connectedUserManager.getConnectedUsers()){
        String username= connectedUserManager.randomUser();
        if( username!=null ) {
            final String  fact=faker.chuckNorris().fact();
            this.log.info("Sending push message {}", fact);
            final String time = new SimpleDateFormat("HH:mm").format(new Date());            
            simpMessagingTemplate.convertAndSendToUser(username, "/queue/reply", 
                    new OutputMessage("Chuck Norris", fact, time));
            
        }    
        
    }


}
