package com.uc3m.delphi.rest.request;

import com.uc3m.delphi.database.model.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Setter
@Getter
@ToString
public class RequestUser {
    private User user;

}
