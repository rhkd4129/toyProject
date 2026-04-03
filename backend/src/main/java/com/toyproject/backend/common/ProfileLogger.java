package com.toyproject.backend.common;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;

import java.util.logging.Logger;

@Component
@Slf4j
public class ProfileLogger implements CommandLineRunner {


    private final Environment environment;

    public ProfileLogger(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(String... args) {
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length == 0) {
            log.info("현재 활성화된 프로필: default");
        } else {
            log.info("현재 활성화된 프로필: {}", String.join(", ", activeProfiles));
        }
    }
}
