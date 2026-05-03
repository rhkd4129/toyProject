package com.toyproject.backend.redis.subscriber;

// 컨테이너에 리스너 등록 + 시작

import com.toyproject.backend.redis.consumer.PdfResultConsumer;
import com.toyproject.backend.utils.StreamKey;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PdfResultStreamRunner implements ApplicationRunner {

    private final StreamMessageListenerContainer<String, MapRecord<String, String, String>> container;
    private final PdfResultConsumer pdfResultConsumer;
//    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void run(ApplicationArguments args) {
        // Consumer Group 없이 심플하게 구독 (Spring 1개짜리라 그룹 불필요)
//        container.receive(StreamOffset.create(StreamKey.PDF_RESULT.getKey(), ReadOffset.lastConsumed()), consumer);
        // ✅ 수정
        container.receive(StreamOffset.create(StreamKey.PDF_RESULT.getKey(), ReadOffset.latest()), pdfResultConsumer);

        container.start(); // 🚀 폴링 시작
        System.out.println("🎧 구독 시작");
    }
}
