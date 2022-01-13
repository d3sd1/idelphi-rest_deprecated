package com.uc3m.delphi.rest.Dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QuestionReorderDto {
    private Long fromId;
    private int fromPosition;
    private Long toId;
    private int toPosition;
}
