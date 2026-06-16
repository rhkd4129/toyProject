package com.toyproject.backend.redis.consumer;

import com.toyproject.backend.config.properties.RedisStreamProperties;
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
    private final RedisStreamProperties redisStreamProperties;

    @Override
        public void run(ApplicationArguments args) {
            container.receive(StreamOffset.create(redisStreamProperties.getPdfResults(), ReadOffset.latest()), pdfResultConsumer);
            container.start();
            System.out.println("구독 시작");
    }
}
