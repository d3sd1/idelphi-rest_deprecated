package com.uc3m.delphi.ws.channel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Objects;

@Controller
public class ChatBroadcast implements ApplicationListener<SessionSubscribeEvent> {

    @Autowired
    SimpMessagingTemplate template;

    private final String CHANNEL = "/private/ws/subscribe/process/list";

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        Message<byte[]> message = event.getMessage();
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        assert command != null;
        if (command.equals(StompCommand.SUBSCRIBE)) {
            String sessionId = accessor.getSessionId();
            String userId = Objects.requireNonNull(accessor.getUser()).getName();
            String destination = accessor.getDestination();
            System.out.println(sessionId);
            System.out.println(userId);
            System.out.println(destination);
            //this.template.convertAndSendToUser();
        }
    }
    @SubscribeMapping(value=CHANNEL)
    public String chatInit() {
        System.out.println("chat init!");
        return "this is working";
    }


}
