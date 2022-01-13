package com.uc3m.delphi.database.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "delphi_processes", schema = "public")
public class DelphiProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String description;
    private String pictureUrl;
    private String objectives;

    @ManyToMany
    private List<User> experts;

    @ManyToMany
    private List<User> coordinators;

    @OneToMany
    private List<DelphiProcessRound> rounds;

    @OneToOne
    private DelphiProcessRound currentRound;

    private boolean processFinished;

    private String finalComment;

    private LocalDateTime creationDate;
    private LocalDateTime modifiedDate;


}
