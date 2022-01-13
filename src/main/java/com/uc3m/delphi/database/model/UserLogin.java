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
@Table(name = "user_logins", schema = "public")
public class UserLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToOne
    private User user;
    private LocalDateTime creationDate;
    private String jwt;
    private boolean enabled;

}
