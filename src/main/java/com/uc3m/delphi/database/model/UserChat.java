package com.uc3m.delphi.database.model;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_chats", schema = "public")
public class UserChat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    private User user1;

    @OneToOne
    private User user2;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_chat_messages",
            joinColumns = @JoinColumn(
                    name = "user_chat_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "chat_message_id", referencedColumnName = "id"))
    private Set<ChatMessage> chatMessages;

}
