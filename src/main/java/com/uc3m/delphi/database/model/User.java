package com.uc3m.delphi.database.model;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;

@Getter(onMethod_ = @JsonIgnore)
@Setter(onMethod_ = @JsonIgnore)
@ToString
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", schema = "public")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String email;
    @JsonIgnore
    private String password;
    private String name;
    private String surnames;

    private String photo;
    private ChatStatus chatStatus = ChatStatus.OFFLINE;
    private boolean enabled;
    private boolean blocked;
    private boolean needsOnboard = true;
    private boolean notificationStatus = true;

    @OneToOne
    private Language language;

}
