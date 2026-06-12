package com.toyproject.backend.domain.pdf.service;
import com.toyproject.backend.domain.pdf.Enum.PdfTypeEnum;
import com.toyproject.backend.domain.pdf.dto.PdfRedisRequest;
import com.toyproject.backend.domain.pdf.dto.PdfResponseDTO;
import com.toyproject.backend.domain.pdf.entity.Pdf;
import com.toyproject.backend.domain.pdf.repository.PdfRepository;
import com.toyproject.backend.error.CommonException;
import com.toyproject.backend.error.ErrorCode;
import com.toyproject.backend.redis.RedisService;
import com.toyproject.backend.storage.StorageService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PdfService {

//    private final FastApiClient fastApiClient;
    private final StorageService storageService;
    private final PdfRepository pdfRepository;
    private final RedisService redisService;



    public String createPdfTaskId(){
        return  UUID.randomUUID().toString();
    }

    @Transactional
    public PdfRedisRequest createPdf(MultipartFile file,String pdfTaskId,String pdfType) {
        try {
            //TODO 리팩토링? storageService.uploadFile

            String filePath = storageService.uploadFile(file);
            PdfRedisRequest pdfRedisRequest = PdfRedisRequest.to(pdfTaskId,filePath,pdfType);
            Pdf pdf = Pdf.createPdf(pdfTaskId, PdfTypeEnum.valueOf(pdfType),file.getOriginalFilename());
            pdfRepository.createPdf(pdf);
            redisService.addStream(pdfRedisRequest);
            return pdfRedisRequest;
        } catch (IOException e) {
            log.error("PDF 파일 처리 중 오류 발생: {}", e.getMessage());
            throw new CommonException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    public void updateMetadata(MultipartFile file) throws IOException {
        String filePath  = storageService.uploadFile(file);
    }


    // PdfService.java
    @Transactional
    public void completePdf(String taskId) {
        Pdf pdf = pdfRepository.findByTaskId(taskId).orElseThrow(() -> new CommonException(ErrorCode.BOARD_N0_SUCH));
        pdf.complete();
    }
    @Transactional
    public void failPdf(String taskId) {
        Pdf pdf = pdfRepository.findByTaskId(taskId)
                .orElseThrow(() -> new CommonException(ErrorCode.BOARD_N0_SUCH));
        pdf.fail();
    }

    public PdfResponseDTO getPdf(String taskId) throws MalformedURLException {
        return storageService.downloadFile(taskId);
    }

}
