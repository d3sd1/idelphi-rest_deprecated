package com.uc3m.delphi.database.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "password_recover", schema = "public")
public class PasswordRecover {

    @Id
    @GeneratedValue
            (strategy = GenerationType.AUTO)
    private Long id;

    private Long recoverCode;

    private LocalDateTime expires;

    @OneToOne
    private User user;

}