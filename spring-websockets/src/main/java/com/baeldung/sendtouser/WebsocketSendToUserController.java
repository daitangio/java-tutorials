package com.baeldung.sendtouser;

import com.baeldung.websockets.OutputMessage;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Controller
@Slf4j
public class WebsocketSendToUserController {

    private Gson gson = new Gson();

    /**
     * !! @SendToUser points to “queue” but the message will be sent to “/user/queue“.
     * @param message
     * @param principal
     * @return
     * @throws Exception
     */
    @MessageMapping("/message")
    @SendToUser(destinations="/queue/reply", broadcast=false)
    public OutputMessage processMessageFromClient(@Payload String message, Principal principal) throws Exception {
        try{
            log.info("User {} Sent {}",principal,message);
            String r= "you said:'"+gson.fromJson(message, Map.class).get("text").toString()+"'";
            final String time = new SimpleDateFormat("HH:mm").format(new Date());
            OutputMessage o= new OutputMessage("Spring", r, time);
            log.info("Sending reply:{}",r);        
            return o;
        }catch(Exception e){
            log.error("Failed:",e);
            throw e;
        }
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }
}
