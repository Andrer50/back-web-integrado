package com.utp.backwebintegrado;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackWebIntegradoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackWebIntegradoApplication.class, args);
    }

}

