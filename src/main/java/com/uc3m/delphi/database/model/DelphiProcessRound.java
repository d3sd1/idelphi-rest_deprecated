package com.uc3m.delphi.database.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "delphi_process_rounds", schema = "public")
public class DelphiProcessRound {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private boolean finished;
    private boolean started;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime endTime;
    private int orderPosition;

    @ManyToMany
    private List<User> expertsVoted;

    @ManyToMany
    private List<User> expertsRemaining;

    @OneToMany
    private List<DelphiProcessRoundQuestionAnswer> answers;

    @OneToMany
    private Set<DelphiProcessRoundQuestion> questions;

}
