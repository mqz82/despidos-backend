package com.despidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DespidosApplication {

    public static void main(String[] args) {
        SpringApplication.run(DespidosApplication.class, args);
    }
}
