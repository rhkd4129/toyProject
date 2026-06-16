package com.toyproject.backend.redis.consumer.handler;

import com.toyproject.backend.emitter.SseEmitterService;
import com.toyproject.backend.domain.pdf.service.PdfService;
import com.toyproject.backend.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.stereotype.Component;

@Component("COMPLETED")
@Slf4j
@RequiredArgsConstructor
public class CompletedMessageHandler implements MessageHandler {

    private final StorageService storageService;
    private final PdfService pdfService;
    private final SseEmitterService sseEmitterService;

    @Override
    public void onMessageProcess(MapRecord<String, String, String> message) {
        String status = message.getValue().get("status");
        String outputPath = message.getValue().get("outputPath");
        String taskId = message.getValue().get("taskId");
        String inputPath = message.getValue().get("inputPath");

        String resultFileName = storageService.uploadResultFile(outputPath);
        pdfService.completePdf(taskId);
        sseEmitterService.sendEvent(taskId, resultFileName, status);
        storageService.deleteFile(inputPath);
    }
}
