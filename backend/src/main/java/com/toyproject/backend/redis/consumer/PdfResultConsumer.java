package com.toyproject.backend.redis.consumer;

import com.toyproject.backend.Emitter.SseEmitterService;
import com.toyproject.backend.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@Slf4j
@RequiredArgsConstructor
public class PdfResultConsumer implements StreamListener<String, MapRecord<String, String, String>> {

        private final StorageService storageService;
        private final SseEmitterService sseEmitterService;

        @Override
        public void onMessage(MapRecord<String, String, String> message) {
            log.info("--------- onMessage  ------");
            log.info("redis results 스트림 도착 ");
            String taskId = message.getValue().get("taskId");
            String resultPath    = message.getValue().get("filePath");


            log.info("{}",taskId);
            log.info("{}",resultPath);
//            sseEmitterService.sendEvent(taskId);
            sseEmitterService.sendEvent(taskId,resultPath);

            
//            storageService.downloadFile();

//            SseEmitter emitter = new SseEmitter();
//            emitter.put(taskId, emitter);
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
