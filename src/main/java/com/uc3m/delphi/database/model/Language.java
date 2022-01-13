package com.uc3m.delphi.database.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "languages", schema = "public")
public class Language {

    @Id
    @GeneratedValue
            (strategy = GenerationType.AUTO)
    private Long id;

    private String keyName;

    private boolean available;

}