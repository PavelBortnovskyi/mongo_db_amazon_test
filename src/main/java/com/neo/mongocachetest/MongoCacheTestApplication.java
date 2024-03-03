package com.neo.mongocachetest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MongoCacheTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MongoCacheTestApplication.class, args);
    }

}
