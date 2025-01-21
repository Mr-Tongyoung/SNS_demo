package com.example.junho.sns_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EnableElasticsearchRepositories(basePackages = "com.example.junho.sns_demo.domain.elasticSearch")
//@EnableJpaRepositories(basePackages = "com.example.junho.sns_demo.domain")

public class SnsDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(SnsDemoApplication.class, args);
  }

}
