package com.uc3m.delphi.ws.model;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WsWrapper <T> {
    private WsUpdate mode;
    private T data;
}
