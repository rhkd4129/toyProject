package com.toyproject.backend.redis.consumer;

import com.toyproject.backend.Emitter.SseEmitterService;
import com.toyproject.backend.domain.pdf.service.PdfService;
import com.toyproject.backend.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;


import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class PdfResultConsumer implements StreamListener<String, MapRecord<String, String, String>> {

        private final StorageService storageService;
        private final PdfService pdfService;
        private final SseEmitterService sseEmitterService;


        @Override
        public void onMessage(MapRecord<String, String, String> message) {
            log.info("--------- onMessage  ------");
            log.info("redis results 스트림 도착 ");
            String taskId = message.getValue().get("taskId");
            String status    = message.getValue().get("status");
            String filePath = message.getValue().get("filePath");

            log.info("{}",taskId);
            log.info("{}",status);
            log.info("{}",filePath);

            if ("COMPLETED".equals(status)) {
                //완료된 파일의 경로 + 이름
                String resultPath    = message.getValue().get("resultPath");
                pdfService.completePdf(taskId);
                sseEmitterService.sendEvent(taskId, resultPath,status);

                // 원본파일 삭제
                storageService.deleteFile(filePath);



            } else if("FAILED".equals(status)) {
                String errorMessage    = message.getValue().get("errorMessage");
                log.info("{}",errorMessage);
                pdfService.failPdf(taskId);
                sseEmitterService.sendErrorMessage(taskId,status,errorMessage);
                storageService.deleteFile(filePath);
                // 재시도?
                // Vue에 실패 이벤트 전송
            }
        }
}
