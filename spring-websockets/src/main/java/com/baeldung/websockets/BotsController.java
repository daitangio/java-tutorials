package com.baeldung.websockets;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * This controller gets mesage from users and send it to the common chat
 */
@Controller
@Slf4j
public class BotsController {

    @MessageMapping("/chatwithbots")
    @SendTo("/topic/pushmessages")
    public OutputMessage send(final Message message) throws Exception {
        log.info("Sending message..."+message.getText());
        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(message.getFrom(), message.getText(), time);
    }

}
