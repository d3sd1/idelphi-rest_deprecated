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
@Table(name = "delphi_resp_json_tmp", schema = "public")
public class DelphiRespTemp {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String jsonObjTmp;


}
