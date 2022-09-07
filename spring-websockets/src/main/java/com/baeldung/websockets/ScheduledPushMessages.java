package com.baeldung.websockets;


import com.github.javafaker.Faker;

import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import static java.util.concurrent.TimeUnit.*;

@Service
@Slf4j
public class ScheduledPushMessages {

    private final SimpMessagingTemplate simpMessagingTemplate;
    
    private final Faker faker;
    
    public ScheduledPushMessages(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        faker = new Faker();
        this.log.info("Chuck Norris Ready");
    }

    @Scheduled(fixedRate = 60, timeUnit = SECONDS)
    public void sendMessage() {
        final String  fact=faker.chuckNorris().fact();
        this.log.info("Sending push message {}", fact);
        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        simpMessagingTemplate.convertAndSend("/topic/pushmessages", 
            new OutputMessage("Chuck Norris", fact, time));
    }
    
    /** Rate is in millisecond */
    @Scheduled(fixedRate = 10, timeUnit = SECONDS)
    public void sendFriendMessage(){
        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        simpMessagingTemplate.convertAndSend("/topic/pushmessages", 
            new OutputMessage(faker.friends().character(), faker.friends().quote(), time));
    }




}
