package com.uc3m.delphi.database.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chat_messages", schema = "public")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    private User sentBy;

    @OneToOne
    private User sentTo;

    private String message;

    private boolean read;

    private LocalDateTime sentDate;

}
