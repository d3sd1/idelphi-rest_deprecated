package com.uc3m.delphi.rest.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class VersionDto {
    private String current;
    private boolean hasNext;
    private String env;
}
