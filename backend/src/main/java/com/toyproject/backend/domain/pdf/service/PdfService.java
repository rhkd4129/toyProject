package com.toyproject.backend.domain.pdf.service;


import com.toyproject.backend.error.CommonException;
import com.toyproject.backend.error.ErrorCode;
import com.toyproject.backend.utils.Result;
import com.toyproject.backend.utils.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


@Service
@Slf4j
@RequiredArgsConstructor
public class PdfService {

    private final FastApiClient fastApiClient;
    private final StorageService storageService;
    private final RedisTemplate<String, Object> redisTemplate;


    public Result createPdf(MultipartFile file) {
        try {
            Result result = new Result();
            String filePath = storageService.uploadFile(file);
            redisTemplate.opsForList().rightPush("producer:group", "value1");
            String a = fastApiClient.sendPdfPath(filePath);
            result.setMessage(a);
            return result;
        } catch (IOException e) {
            log.error("PDF 파일 처리 중 오류 발생: {}", e.getMessage());
            throw new CommonException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

}
