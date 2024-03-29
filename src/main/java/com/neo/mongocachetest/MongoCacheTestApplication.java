package com.neo.mongocachetest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@EnableCaching
@EnableScheduling
@SpringBootApplication
public class MongoCacheTestApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(MongoCacheTestApplication.class, args);
    }

}
