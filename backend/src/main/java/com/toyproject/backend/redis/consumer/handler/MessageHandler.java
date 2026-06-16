package com.toyproject.backend.redis.consumer.handler;

import org.springframework.data.redis.connection.stream.MapRecord;

public interface MessageHandler {
    void onMessageProcess(MapRecord<String, String, String> message);
}
