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
@Table(name = "delphi_process_round_questions", schema = "public")
public class DelphiProcessRoundQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private QuestionType type;
    private int minVal;
    private int maxVal;
    private int maxSelectable;
    private int orderPosition;

    @OneToMany
    private Set<DelphiProcessRoundQuestionCategory> categories;


}
