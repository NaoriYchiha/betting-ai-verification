package org.lytvynovych.bettingaiverification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BettingAiVerificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(BettingAiVerificationApplication.class, args);
    }

}
