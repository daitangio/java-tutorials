package com.baeldung.sendtouser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketSendToUserConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    ConnectedUserManager connectedUserManager;
    
    private TaskScheduler messageBrokerTaskScheduler;

    @Value("${application.hub.max_users}")
    private int max_users;
    @Value("${application.hub.max_inbound_threads}")
    private int max_inbound_threads;
    @Value("${application.hub.max_outbound_threads}")
    private int max_outbound_threads;

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

    
    /**
     * GG: Limiting the max connected users using .setHandshakeHandler(new DefaultHandshakeHandler() { ... isValidOrigin
     * DOES Not work in practice. We see connection happen anyway.
     * So we relay on Stomp CONNECT  (see below)
     * 
     * Also this implementation does not use .withSockJS() and create a pure websocket without fallbacks
     * Refer to https://stomp-js.github.io/guide/stompjs/rx-stomp/ng2-stompjs/using-stomp-with-sockjs.html
     * 
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("Registering greeting end-to-end");
        registry.addEndpoint("/hubc") ;
    }


    /***
     * On Stomp, the standard way to "authenticate" user seems to relay on login,pass parameters
     * 
     * Refer to
     * https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/web.html#websocket-stomp-authentication
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {

        registration
            .taskExecutor()
            .queueCapacity(max_users+2)  // At least two more for managing connect requests
            .corePoolSize(max_inbound_threads)
            .maxPoolSize(max_inbound_threads);

        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                // log.info("preSend "+message);
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {

                    // PRE CHECK 
                    if(connectedUserManager.size() >=max_users) {
                        log.warn("?Max user reached... {}  Limit: {}",connectedUserManager.size(), max_users);
                        //log.warn("Max user reached... refusing CONNECT "+ accessor.getLogin());
                        //return null;
                    }

                    if(!"trustn00ne".equals(accessor.getPasscode())){
                        log.error("Login failed for {}", accessor.getLogin());
                        return null;
                    }

                    var login=accessor.getLogin();
                    log.info("Login {} Passcode {}", login, accessor.getPasscode());
  

                    Principal newPrincipal=new StompPrincipal(login+"$"+UUID.randomUUID().toString());
                    accessor.setUser(newPrincipal);
                    connectedUserManager.registerNewUser(newPrincipal);
  
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

    
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration
            .taskExecutor()
            .queueCapacity(max_users+3)  // We have 3 scheduled tasks so we ask 
                                         //  a bit more queue. Also this value should be greater the inbound
            .corePoolSize(max_outbound_threads)            
            .maxPoolSize(max_outbound_threads);
        
    }
    

}
