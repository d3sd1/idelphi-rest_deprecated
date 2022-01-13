package com.uc3m.delphi.database.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "delphi_process_round_question_categories", schema = "public")
public class DelphiProcessRoundQuestionCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String catName;


}
