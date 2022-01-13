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
@Table(name = "delphi_process_round_question_answers", schema = "public")
public class DelphiProcessRoundQuestionAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String response;
    @OneToOne
    private DelphiProcessRoundQuestion question;
    @OneToOne
    private User user;

    private LocalDateTime answerDate;


}
