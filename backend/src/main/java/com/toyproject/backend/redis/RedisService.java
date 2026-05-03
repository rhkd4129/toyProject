package com.toyproject.backend.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toyproject.backend.domain.pdf.dto.PdfRedisRequest;
import com.toyproject.backend.utils.StreamKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;



    public void addStream(PdfRedisRequest pdfRedisRequest) {
        boolean streamExists = checkStreamExists(StreamKey.PDF_EVENT.getKey());

        if (!streamExists) {
            System.out.println("스트림 생성: " + StreamKey.PDF_EVENT.getKey());
        }



//        // 1. 객체 → Map 변환 (ObjectMapper)
        Map<String, Object> fields = objectMapper.convertValue(
                pdfRedisRequest, new TypeReference<Map<String, Object>>() {}
        );
    // 1. Map<String, String>으로 직접 변환
//            Map<String, String> fields = Map.of(
//                    "taskId",   pdfRedisRequest.getTaskId(),
//                    "filePath", pdfRedisRequest.getFilePath(),
//                    "fileName", pdfRedisRequest.getFileName()
//            );
        // 2. Map → MapRecord 포장 (스트림이름 + 데이터 묶음)
        // 2. MapRecord 타입도 String, String으로 변경
        MapRecord<String, String,Object> record =
                MapRecord.create(StreamKey.PDF_EVENT.getKey(), fields);

        // 3. Stream에 추가 (스트림 없으면 자동 생성)
        RecordId recordId = redisTemplate.opsForStream().add(record);
        System.out.println("Message added with ID: " + recordId.getValue());

    }

    private boolean checkStreamExists(String streamKey) {
        try {
            redisTemplate.opsForStream().info(streamKey);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
