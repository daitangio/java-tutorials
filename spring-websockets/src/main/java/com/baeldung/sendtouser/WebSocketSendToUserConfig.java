package com.baeldung.sendtouser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.*;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import lombok.var;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpSession;

import java.security.Principal;
import java.util.*;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketSendToUserConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    ConnectedUserManager connectedUserManager;
    
    private TaskScheduler messageBrokerTaskScheduler;

    @Autowired
    public void setMessageBrokerTaskScheduler(@Lazy TaskScheduler taskScheduler) {
        this.messageBrokerTaskScheduler = taskScheduler;        
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic/", "/queue/")
            /** server,client  heart-beat in milliseconds 
             * 
             */
            .setHeartbeatValue(new long[] {10000, 10000})
            .setTaskScheduler(this.messageBrokerTaskScheduler);
        config.setApplicationDestinationPrefixes("/app");
        config.setPreservePublishOrder(true);
        
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("Registering greeting end-to-end");
        registry.addEndpoint("/greeting")
        /* Right way
        .setHandshakeHandler(new DefaultHandshakeHandler() {

            @Override
            protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
                    Map<String, Object> attributes) {

                Principal newPrincipal=new StompPrincipal(UUID.randomUUID().toString());        
                log.info("Username:{}",newPrincipal);
                connectedUserManager.registerNewUser(newPrincipal);
                return newPrincipal;
            }
        })
         */
        .withSockJS();
    }


    /***
     * In a *true* implementation please relay on Spring Security OR on  the determinateUser inside Hanshake handler.
     * For this PoC we tryst the login header name in the CONNECT header below
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                // log.info("preSend "+message);
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {

                    var login=accessor.getNativeHeader("login");                    
                    log.info("Login {} Passcode {}", accessor.getNativeHeader("login"), accessor.getNativeHeader("passcode"));
                    // log.info("Message broker task scheduler: {}", messageBrokerTaskScheduler);

                    Principal newPrincipal=new StompPrincipal(login+"$"+UUID.randomUUID().toString());
                    accessor.setUser(newPrincipal);
                    connectedUserManager.registerNewUser(newPrincipal);
                    
                    // accessor.setUser
                    /*
                    Authentication user = ... ; // access authentication header(s)
                    accessor.setUser(user);
                    */
                }else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                    log.info("Disconnecting... {}", message);
                    // .get("simpHeartbeat")
                    log.info("Session data {}",message.getHeaders().get("simpUser"));
                    StompPrincipal user= (StompPrincipal) message.getHeaders().get("simpUser");
                    connectedUserManager.deregisterUser(user);
                    
                }
                return message;
            }
        });
    }
}
