package com.uc3m.delphi.rest.v1.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

public class FullWsTest  implements ApplicationListener<SessionSubscribeEvent> {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public FullWsTest(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        messagingTemplate.convertAndSendToUser(event.getUser().getName(), "/topic/mydest", "Last known error count");
    }
}
