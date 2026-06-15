package com.toyproject.backend.domain.pdf.event;

import com.toyproject.backend.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PdfCreatedEventListener {
//    실제 Redis 전송은 @TransactionalEventListener 안으로 옮기는 거야. 그게 이 패턴의 핵심이야.

    private final RedisService redisService;

    @TransactionalEventListener
    public void handle(PdfCreatedEvent event) {
        redisService.addStream(event.pdfRedisRequest());
    }
}


// PdfService                이벤트 발행
// ApplicationEventPublisher 전달
// PdfCreatedEventListener   실행
// RedisService

// PdfCreatedEvent = "PDF 작업 생성됨"이라는 사실과 데이터를 담은 객체
// PdfCreatedEventListener = 그 이벤트를 받아서 Redis Stream에 작업을 넣는 실행자


//publisher.publishEvent(new PdfCreatedEvent(pdfRedisRequest));
//=>
//1. 이벤트 생성
//2. 스프링 이벤트 버스 전달
//3. 해당 이벤트 Listener 찾기
//4. Commit 대기
//5. Listener 실행
