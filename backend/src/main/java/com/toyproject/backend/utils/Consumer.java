package com.toyproject.backend.utils;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class Consumer implements StreamListener<String, MapRecord<String, String, String>> {


        @Override
        public void onMessage(MapRecord<String, String, String> message) {
            String taskId = message.getValue().get("task_id");
            String status = message.getValue().get("status");
            String log    = message.getValue().get("log");

            System.out.println("📩 LOG 수신 - taskId: " + taskId
                    + " | status: " + status
                    + " | log: " + log);

            // 여기서 SSE로 Vue에 전송하는 로직 추가하면 됨
        }
//    public StreamMessageListenerContainer createStreamMessageListenerContainer(){
//        return StreamMessageListenerContainer.create(c)
//    }


}
