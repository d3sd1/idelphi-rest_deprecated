package com.uc3m.delphi.database.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "filter_jwt_configs", schema = "public")
public class FilterJwtConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String urlPattern;

    private boolean excludes;

}
