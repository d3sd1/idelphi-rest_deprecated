package com.uc3m.delphi.rest.v1.profile.chat;

import com.uc3m.delphi.database.model.ChatMessage;
import com.uc3m.delphi.database.model.User;
import com.uc3m.delphi.database.model.UserChat;
import com.uc3m.delphi.database.repository.ChatMessageRepository;
import com.uc3m.delphi.database.repository.DelphiProcessRepository;
import com.uc3m.delphi.database.repository.UserChatRepository;
import com.uc3m.delphi.database.repository.UserRepository;
import com.uc3m.delphi.rest.exception.ChatException;
import com.uc3m.delphi.rest.exception.UserException;
import com.uc3m.delphi.rest.request.RequestUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/${v1API}/chat")
public class ChatController {

    private final DelphiProcessRepository delphiProcessRepository;
    private final UserChatRepository userChatRepository;
    private final ChatMessageRepository chatMessageRepository;
    @Autowired
    SimpMessagingTemplate template;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private RequestUser requestUser;

    public ChatController(DelphiProcessRepository delphiProcessRepository,
                          UserChatRepository userChatRepository,
                          ChatMessageRepository chatMessageRepository) {
        this.delphiProcessRepository = delphiProcessRepository;
        this.userChatRepository = userChatRepository;
        this.chatMessageRepository = chatMessageRepository;
    }
/*
    @RequestMapping(method = RequestMethod.GET, path = "/list")
    public List<UserChat> listUserChats() {
        return this.userChatRepository.findAllByUser1OrUser2Is(this.requestUser.getUser(), this.requestUser.getUser());
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/open")
    public Long openChat(@RequestParam Long userId) throws UserException, ChatException {
    }
    @RequestMapping(method = RequestMethod.POST, path = "/read/{chatId}")
    public void postChatAsRead(@PathVariable long chatId) {
        this.userChatRepository.findById(chatId).ifPresent((userChat) -> {
            userChat.getChatMessages().forEach(userMessage -> {
                if (userMessage.getSentBy().getId() != this.requestUser.getUser().getId()) {
                    userMessage.setRead(true);
                    this.chatMessageRepository.save(userMessage);
                }
            });
        });
    }

    @RequestMapping(method = RequestMethod.GET, path = "/get/{toUserId}")
    public UserChat getChatByUserId(@PathVariable long toUserId) throws ChatException {
        User toUser = this.userRepository.findById(toUserId).orElseThrow(() -> new ChatException("Could not find toUser."));
        User fromUser = this.requestUser.getUser();
        if (toUser.getId() == fromUser.getId()) {
            throw new ChatException("User can't open chat to himself");
        }
        UserChat uc = null;
        Optional<UserChat> userChatComb1 = this.userChatRepository.findByUser1AndUser2(toUser, fromUser);
        if(userChatComb1.isPresent()) {
            uc = userChatComb1.get();
        }
        Optional<UserChat> userChatComb2 = this.userChatRepository.findByUser1AndUser2(fromUser, toUser);
        if(userChatComb2.isPresent()) {
            uc = userChatComb2.get();
        }
        if(uc == null) {
            UserChat userChat = UserChat.builder()
                    .user1(fromUser)
                    .user2(toUser)
                    .build();
            uc = this.userChatRepository.save(userChat);
        }
        return uc;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/write/{chatId}")
    public void writeChatMessage(@PathVariable long chatId, @RequestBody ChatMessage chatMessage) {
        this.userChatRepository.findById(chatId).ifPresent((userChat) -> {
            chatMessage.setId(0);
            chatMessage.setSentBy(this.requestUser.getUser());
            chatMessage.setRead(false);
            chatMessage.setSentDate(LocalDateTime.now());
            ChatMessage dbChatMessage = this.chatMessageRepository.save(chatMessage);
            userChat.getChatMessages().add(dbChatMessage);
            this.userChatRepository.save(userChat);
            template.convertAndSendToUser(String.valueOf(chatMessage.getSentTo().getId()), "/ws/subscribe/chat/messages", dbChatMessage);
        });

    }
*/
}
