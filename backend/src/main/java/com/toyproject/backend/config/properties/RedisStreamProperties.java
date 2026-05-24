package com.toyproject.backend.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "redis.stream")
public class RedisStreamProperties {
    private String pdfEvents;
    private String pdfResults;
}
