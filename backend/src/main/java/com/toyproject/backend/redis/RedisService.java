package com.toyproject.backend.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toyproject.backend.config.properties.RedisStreamProperties;
import com.toyproject.backend.domain.pdf.dto.PdfRedisRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

//@RequiredArgsConstructor
@Service
@Slf4j
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisStreamProperties redisStreamProperties;
    private final ObjectMapper objectMapper;

    public RedisService(@Qualifier("streamRedisTemplate") RedisTemplate<String, String> redisTemplate,
                        RedisStreamProperties redisStreamProperties,
                        ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.redisStreamProperties = redisStreamProperties;
        this.objectMapper = objectMapper;
    }
    public void addStream(PdfRedisRequest pdfRedisRequest) {
        // 1. 객체 → Map<String, String> 변환
        Map<String, String> fields = objectMapper.convertValue(
                pdfRedisRequest, new TypeReference<Map<String, String>>() {}
        );

        // 2. Map → MapRecord 포장
        MapRecord<String, String, String> record =
                MapRecord.create(redisStreamProperties.getPdfEvents(), fields);

        // 3. Stream에 추가 (스트림 없으면 자동 생성)
        RecordId recordId = redisTemplate.opsForStream().add(record);

        if (recordId == null) {
            log.error("Redis Stream 추가 실패 - recordId null. stream: {}",
                    redisStreamProperties.getPdfEvents());
            throw new RuntimeException("Redis Stream 메시지 추가 실패");
        }
        log.info("redis message id: {}", recordId.getValue());
    }
}
