package com.fintrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.fintrack")
@EnableJpaRepositories(basePackages = "com.fintrack.adapter.out.persistence.repository")
@EntityScan(basePackages = "com.fintrack.adapter.out.persistence.entity")
public class FintrackApplication {
    public static void main(String[] args) {
        SpringApplication.run(FintrackApplication.class, args);
    }
}