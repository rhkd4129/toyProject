package com.toyproject.backend.redis.consumer;

import com.toyproject.backend.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PdfResultConsumer implements StreamListener<String, MapRecord<String, String, String>> {

        private final StorageService storageService;

        @Override
        public void onMessage(MapRecord<String, String, String> message) {
            String taskId = message.getValue().get("taskId");
            String status = message.getValue().get("status");
            String resultPath    = message.getValue().get("resultPath");
            log.info("📩 LOG 수신 - taskId: {} | status: {} | log: {}", taskId, status, resultPath);


            storageService.downloadFile();


//            // ✅ 1. DB에 작업 결과 저장
//            pdfTaskService.updateTaskResult(taskId, status, resultPath);
//            // ✅ 2. 상태에 따른 분기 처리
//            if ("SUCCESS".equals(status)) {
//                // 성공 → 다운로드 가능 상태로 변경
//                pdfTaskService.markAsCompleted(taskId, resultPath);
//
//                // ✅ 3. 클라이언트에알림
//
//            } else if ("FAIL".equals(status)) {
//                // 실패 → 재시도 or 에러 처리
//
//            }
        }
}
