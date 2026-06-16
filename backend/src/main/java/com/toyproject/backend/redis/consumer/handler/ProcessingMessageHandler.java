package com.toyproject.backend.redis.consumer.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.stereotype.Component;

@Component("PROCESSING")
@Slf4j
public class ProcessingMessageHandler implements MessageHandler {

    @Override
    public void onMessageProcess(MapRecord<String, String, String> message) {
        String totalPages = message.getValue().get("totalPages");
        String currentPage = message.getValue().get("currentPage");

        log.info("총페이지    : {}", totalPages);
        log.info("현재 페이지  : {}", currentPage);

        //TODO 사용자한테 알리는 로직...
    }
}
