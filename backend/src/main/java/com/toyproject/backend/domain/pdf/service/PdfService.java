package com.toyproject.backend.domain.pdf.service;


import com.toyproject.backend.Emitter.EmitterRepository;
import com.toyproject.backend.domain.pdf.dto.PdfRedisRequest;
import com.toyproject.backend.error.CommonException;
import com.toyproject.backend.error.ErrorCode;
import com.toyproject.backend.redis.RedisService;
import com.toyproject.backend.utils.Result;
import com.toyproject.backend.storage.StorageService;
import com.toyproject.backend.Emitter.SseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class PdfService {

    private final FastApiClient fastApiClient;
    private final StorageService storageService;
    private final RedisService redisService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SseEmitterService sseEmitterService;
    private final EmitterRepository emitterRepository;



    public PdfRedisRequest createPdf(MultipartFile file) {
        try {
            Result result = new Result();
            String taskId = UUID.randomUUID().toString();
            PdfRedisRequest pdfRedisRequest = storageService.uploadFile(taskId, file);
//            redisService.addStream(pdfRedisRequest);
            sseEmitterService.createEmitter(taskId);
            return pdfRedisRequest;
        } catch (IOException e) {
            log.error("PDF 파일 처리 중 오류 발생: {}", e.getMessage());
            throw new CommonException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }


    public void getPdf(String taskId){
//        PdfResponseDTO
//         SseEmitter emitter = emitters.get(taskId);





    }


}
