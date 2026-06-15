package com.toyproject.backend.domain.pdf.service;
import com.toyproject.backend.domain.pdf.Enum.PdfTypeEnum;
import com.toyproject.backend.domain.pdf.dto.PdfRedisRequest;
import com.toyproject.backend.domain.pdf.dto.PdfResponseDTO;
import com.toyproject.backend.domain.pdf.entity.Pdf;
import com.toyproject.backend.domain.pdf.event.PdfCreatedEvent;
import com.toyproject.backend.domain.pdf.repository.PdfRepository;
import com.toyproject.backend.error.CommonException;
import com.toyproject.backend.error.ErrorCode;
import com.toyproject.backend.redis.RedisService;
import com.toyproject.backend.storage.StorageService;
import org.springframework.context.ApplicationEventPublisher;
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
public class PdfService{

//    private final FastApiClient fastApiClient;
    private final StorageService storageService;
    private final PdfRepository pdfRepository;
//    private final RedisService redisService;
    private final ApplicationEventPublisher publisher;  //중간 전달자



    public String createPdfTaskId(){
        return  UUID.randomUUID().toString();
    }

    @Transactional
    public PdfRedisRequest createPdf(MultipartFile file,String pdfTaskId,String pdfType) {
        try {
//            phase = AFTER_COMMIT)
            //TODO 리팩토링? storageService.uploadFile
            //TODO 트랜잭션 리팩토링
            String inputPath = storageService.uploadFile(file);
            PdfRedisRequest pdfRedisRequest = PdfRedisRequest.to(pdfTaskId,inputPath,pdfType);
            Pdf pdf = Pdf.createPdf(pdfTaskId, PdfTypeEnum.valueOf(pdfType),file.getOriginalFilename());
            pdfRepository.createPdf(pdf);
            publisher.publishEvent(new PdfCreatedEvent(pdfRedisRequest));

            return pdfRedisRequest;
        } catch (IOException e) {
            // 여기서 파일 삭제하는 로직?
            //redis가 실패해도 사용자한테 어떤처리는 가야함.

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



    public PdfResponseDTO getPdf(String fileName) throws MalformedURLException {
        return storageService.downloadFile(fileName);
    }

}
//@Transactional
//public PdfRedisRequest createPdf(MultipartFile file,String pdfTaskId,String pdfType) {
//    try {
//        //TODO 리팩토링? storageService.uploadFile
//
//        // 2026-06-13 S3 presigned URL 포함하도록 수정
//        // PdfRedisRequest pdfRedisRequest = PdfRedisRequest.to(pdfTaskId,filePath,pdfType);
//        String filePath = storageService.uploadFile(file);
//        String presignedDownloadUrl = storageService.generatePresignedDownloadUrl(filePath);
//        String[] resultUrls = storageService.generateResultPresignedUrls(pdfTaskId);
//        PdfRedisRequest pdfRedisRequest = PdfRedisRequest.to(pdfTaskId, filePath, pdfType,
//                presignedDownloadUrl, resultUrls[0], resultUrls[1]);
//        Pdf pdf = Pdf.createPdf(pdfTaskId, PdfTypeEnum.valueOf(pdfType),file.getOriginalFilename());
//        pdfRepository.createPdf(pdf);
//        redisService.addStream(pdfRedisRequest);
//        return pdfRedisRequest;
//    } catch (IOException e) {
//        log.error("PDF 파일 처리 중 오류 발생: {}", e.getMessage());
//        throw new CommonException(ErrorCode.FILE_UPLOAD_FAILED);
//    }
//}
