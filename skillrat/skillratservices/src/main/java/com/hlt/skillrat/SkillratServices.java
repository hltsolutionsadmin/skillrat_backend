package com.hlt.skillrat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories
@EnableScheduling
@EnableFeignClients
@ComponentScan(basePackages = {"com.hlt.skillrat", "com.hlt.auth"})
public class SkillratServices {

    public static void main(String[] args) {
        SpringApplication.run(SkillratServices.class, args);
    }

}
