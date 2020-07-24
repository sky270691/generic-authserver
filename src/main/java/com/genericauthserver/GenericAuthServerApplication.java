package com.genericauthserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GenericAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GenericAuthServerApplication.class, args);
    }

}
