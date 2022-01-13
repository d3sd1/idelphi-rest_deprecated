package com.uc3m.delphi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class DelphiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DelphiApplication.class, args);
    }

}
