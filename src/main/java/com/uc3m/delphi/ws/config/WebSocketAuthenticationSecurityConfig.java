package com.uc3m.delphi.ws.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uc3m.delphi.database.model.User;
import com.uc3m.delphi.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.util.Collections;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 98)
public class WebSocketAuthenticationSecurityConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry registry) {
        // ...
    }
    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        // Handle exceptions in interceptors and Spring library itself.
        // Will terminate a connection and send ERROR frame to the client.

    }

    @Override
    public void configureClientInboundChannel(final ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            private JwtUtil jwtUtil;

            private ChannelInterceptor init(JwtUtil jwtUtil) {
                this.jwtUtil = jwtUtil;
                return this;
            }

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT == accessor.getCommand()) {
                    try {
                        final ObjectMapper mapper = new ObjectMapper();
                        Claims claims = this.jwtUtil.validate(accessor.getFirstNativeHeader("jwt"));
                        User user = mapper.convertValue(claims.get("user"), User.class);
                        accessor.setUser(new UsernamePasswordAuthenticationToken(
                                user.getId(),
                                null,
                                Collections.singleton((GrantedAuthority) () -> "ROLE_USER")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return message;
            }

        }.init(this.jwtUtil));
    }

}
