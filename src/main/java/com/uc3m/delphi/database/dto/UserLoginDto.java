package com.uc3m.delphi.database.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserLoginDto {
    private String email;
    private String password;
    private boolean rememberMe;
}
