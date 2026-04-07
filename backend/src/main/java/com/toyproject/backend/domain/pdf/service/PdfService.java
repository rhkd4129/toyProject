package com.toyproject.backend.domain.pdf.service;

import com.toyproject.backend.utils.FileUtils;
import com.toyproject.backend.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfService {


    @Value("${spring.servlet.multipart.location}")
    private  String uploadPathPattern;

    @Value("${spring.profiles.active}")
    private  String profile;

    @Value("${cloud.aws.s3.bucket}")
    private  String bucket;

    private final FastApiClient fastApiClient;

    private final S3Client s3Client;



    public Result createPdf(MultipartFile file) throws IOException {
        Result result  = new Result();
        String fileName = file.getOriginalFilename();


//        String originalFilename = file.getOriginalFilename();
////        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
////        String key = "pdf/" + UUID.randomUUID() + ext;  // 동적으로 생성

        if(profile.equals("local")){
            FileUtils.uploadFile(fileName, file.getBytes(), uploadPathPattern);
            String filePath = Paths.get(uploadPathPattern,fileName).toString();
            String jobId = UUID.randomUUID().toString();
            String a = fastApiClient.sendPdfPath(filePath,jobId);
            result.setMessage(a);
        }else{
            log.info("s3업로드 시작 ");
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)          // 어느 S3 버킷에 저장할지 (버킷 = 최상위 폴더 개념)
                            .key("pdf/abc.image")                // 저장될 파일 경로+이름 (예: "pdf/uuid_파일명.pdf")
                            .contentType(file.getContentType())  // 파일 형식 명시 (예: "application/pdf", "image/png")
                            .build(),                // PutObjectRequest 객체 완성
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
                    // 업로드할 실제 파일 데이터 (바이트 스트림) , 파일 크기 (AWS가 얼마나 읽을지 알아야 함)
            );
            result.setMessage("sucesss!!!!!!!!");
        }

        return result;
    }

}
