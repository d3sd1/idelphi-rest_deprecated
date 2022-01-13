package com.uc3m.delphi.rest.Dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResetPass {
    private String currentPass;
    private String newPass;
    private String newPassRep;
}
