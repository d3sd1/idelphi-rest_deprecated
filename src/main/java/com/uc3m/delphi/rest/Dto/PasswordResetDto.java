package com.uc3m.delphi.rest.Dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetDto {
    private String email;
    private Long code;
}
