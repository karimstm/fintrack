package com.fintrack.adapter.out.persistence;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.fintrack.adapter.out.persistence.repository")
@EntityScan(basePackages = "com.fintrack.adapter.out.persistence.entity")
public class PersistenceTestConfiguration {}