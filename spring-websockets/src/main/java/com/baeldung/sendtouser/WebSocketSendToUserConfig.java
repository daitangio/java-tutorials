package com.baeldung.sendtouser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.*;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

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
    


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic/", "/queue/");
        config.setApplicationDestinationPrefixes("/app");

    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("Registering greeting end-to-end");
        registry.addEndpoint("/greeting").setHandshakeHandler(new DefaultHandshakeHandler() {

            @Override
            protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
                    Map<String, Object> attributes) {
                    /* 
                log.info("Attributes {}", attributes);                
                for(Entry<String, List<String>> ks : request.getHeaders().entrySet()){
                    log.info("{}\t{}", ks.getKey(), ks.getValue());
                };*/
                Principal newPrincipal=new StompPrincipal(UUID.randomUUID().toString());        
                log.info("Username:{}",newPrincipal);
                connectedUserManager.registerNewUser(newPrincipal);
                return newPrincipal;
            }


            }).addInterceptors(new HandshakeInterceptor(){
                public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,WebSocketHandler wsHandler, Map<String, Object> attributes) {
                    log.info("Handshake!!");
                    for(Entry<String, List<String>> ks : request.getHeaders().entrySet()){
                        log.info("{}\t{}", ks.getKey(), ks.getValue());
                    };
                    return true;
                }
                

                @Override
                public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                        WebSocketHandler wsHandler, Exception exception) {
                    log.info("AFTER Handshake!");
                    for(Entry<String, List<String>> ks : request.getHeaders().entrySet()){
                        log.info("{}\t{}", ks.getKey(), ks.getValue());
                    };
                }
            }).withSockJS();
    }


    // @Override
    // public void configureClientInboundChannel(ChannelRegistration registration) {
    //     registration.interceptors(new ChannelInterceptor() {
    //         @Override
    //         public Message<?> preSend(Message<?> message, MessageChannel channel) {
    //             log.info("preSend "+message);
    //             StompHeaderAccessor accessor =
    //                     MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    //             if (StompCommand.CONNECT.equals(accessor.getCommand())) {

    //                 log.info("Login header:{}",accessor.getHeader("login"));
    //                 log.info("Login {} Passcode {}", accessor.getNativeHeader("login"), accessor.getNativeHeader("passcode"));

    //                 // accessor.setUser
    //                 /*
    //                 Authentication user = ... ; // access authentication header(s)
    //                 accessor.setUser(user);
    //                 */
    //             }
    //             return message;
    //         }
    //     });
    // }
}
