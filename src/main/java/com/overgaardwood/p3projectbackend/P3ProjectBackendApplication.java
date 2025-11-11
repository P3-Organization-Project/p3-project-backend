package com.overgaardwood.p3projectbackend;

import com.overgaardwood.p3projectbackend.config.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtConfig.class)
public class P3ProjectBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(P3ProjectBackendApplication.class, args);
    }

}
