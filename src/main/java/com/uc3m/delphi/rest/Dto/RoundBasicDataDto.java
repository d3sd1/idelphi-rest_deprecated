package com.uc3m.delphi.rest.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@Setter
public class RoundBasicDataDto {
    private String name;
    private ZonedDateTime endTime;
}
