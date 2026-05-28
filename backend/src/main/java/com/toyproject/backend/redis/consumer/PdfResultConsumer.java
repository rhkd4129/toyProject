package com.toyproject.backend.redis.consumer;

import com.toyproject.backend.Emitter.SseEmitterService;
import com.toyproject.backend.domain.pdf.entity.Pdf;
import com.toyproject.backend.domain.pdf.repository.PdfRepository;
import com.toyproject.backend.domain.pdf.service.PdfService;
import com.toyproject.backend.domain.post.entity.Post;
import com.toyproject.backend.error.CommonException;
import com.toyproject.backend.error.ErrorCode;
import com.toyproject.backend.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
            String resultPath    = message.getValue().get("filePath");
//            String status    = message.getValue().get("status");

            log.info("{}",taskId);
            log.info("{}",resultPath);

            sseEmitterService.sendEvent(taskId,resultPath);
            pdfService.completePdf(taskId);


//            if ("SUCCESS".equals(status)) {
//                pdfService.completePdf(taskId);
//                sseEmitterService.sendEvent(taskId, resultPath);
//            } else {
//                pdfService.failPdf(taskId);
                    // 재시도?
//                // Vue에 실패 이벤트 전송
//            }
        }
}
