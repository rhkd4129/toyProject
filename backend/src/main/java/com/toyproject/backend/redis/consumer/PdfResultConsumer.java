package com.toyproject.backend.redis.consumer;

import com.toyproject.backend.Emitter.SseEmitterService;
import com.toyproject.backend.domain.pdf.service.PdfService;
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
        private final PdfService pdfService;
        private final SseEmitterService sseEmitterService;


        @Override
        public void onMessage(MapRecord<String, String, String> message) {
            String taskId = message.getValue().get("taskId");
            String status    = message.getValue().get("status");
            String inputPath = message.getValue().get("inputPath"); //원본파일경로

            log.info("{}",taskId);
            log.info("{}",status);
            log.info("{}",inputPath);

            if ("COMPLETED".equals(status)) {
                String outputPath = message.getValue().get("outputPath");
                //outputPath(S3 key, ex: xlsx/{taskId}.xlsx)에서 파일명만 추출
                //outputPath(로컬 경로)에서 파일명만 추출
                String resultFileName = storageService.uploadResultFile(outputPath);
                pdfService.completePdf(taskId);
                sseEmitterService.sendEvent(taskId, resultFileName, status); // 파일이름 전송
                storageService.deleteFile(inputPath);  //원본 pdf파일 삭제

            } else if("FAILED".equals(status)) {
                String errorMessage    = message.getValue().get("errorMessage");
                log.info("{}",errorMessage);
                pdfService.failPdf(taskId);
                sseEmitterService.sendErrorMessage(taskId,status,errorMessage);
                storageService.deleteFile(inputPath);  //원본 pdf파일 삭제

                // 재시도?
                // Vue에 실패 이벤트 전송
            }
        }
}
