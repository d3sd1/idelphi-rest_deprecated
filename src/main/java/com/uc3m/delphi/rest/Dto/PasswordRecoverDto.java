package com.uc3m.delphi.rest.Dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordRecoverDto {
    private String email;
}
