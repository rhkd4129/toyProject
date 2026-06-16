package com.toyproject.backend.storage;

import com.toyproject.backend.domain.pdf.dto.PdfRedisRequest;
import com.toyproject.backend.domain.pdf.dto.PdfResponseDTO;
import com.toyproject.backend.domain.pdf.service.PdfService;
import com.toyproject.backend.utils.FileUploadResult;
import com.toyproject.backend.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
@Profile({"prod", "local-s3"})
//@Profile("prod")
@Slf4j
@RequiredArgsConstructor
public class S3StorageService implements StorageService{

    private final S3Client s3Client;

    private final S3Presigner s3Presigner;
//    private final PdfService pdfService;

    @Value("${cloud.aws.s3.bucket}")
    private  String bucket;

    @Value("${cloud.aws.s3.key-prefix}")
    private  String uploadPathPattern;

    @Value("${cloud.aws.s3.result-key-prefix}")
    private String resultKeyPrefix;



    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename(); // "abc.pdf"
        String filePath = uploadPathPattern+originalFilename;
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)          // 어느 S3 버킷에 저장할지 (버킷 = 최상위 폴더 개념)
                        .key(filePath)                // 저장될 파일 경로+이름 (예: "pdf/uuid_파일명.pdf")
                        .contentType(file.getContentType())  // 파일 형식 명시 (예: "application/pdf", "image/png")
                        .build(),                // PutObjectRequest 객체 완성
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
                // 업로드할 실제 파일 데이터 (바이트 스트림) , 파일 크기 (AWS가 얼마나 읽을지 알아야 함)
        );

        String presignedUrl = generatePresignedUrl(filePath);
        log.info("파일업로드 => {}",filePath);
        return presignedUrl;
//        return new PdfRedisRequest(taskId,presignedUrl,pdfType); // presignedUrl이 path 역할
    }

    @Override
    public void deleteFile(String filePath) {
        String path = java.net.URI.create(filePath).getPath();
        String s3Key = path.startsWith("/" + bucket + "/")
                ? path.substring(bucket.length() + 2)
                : path.substring(1);
        s3Client.deleteObject(b -> b.bucket(bucket).key(s3Key).build());
        log.info("파일삭제 => {}", s3Key);
    }

    @Override
    public String uploadResultFile(String outputPath) {
        return outputPath.substring(outputPath.lastIndexOf("/") + 1);
    }

    @Override
    public PdfResponseDTO downloadFile(String fileName) {
        String s3Key = resultKeyPrefix + fileName;
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
                .build();
        Resource resource = new InputStreamResource(s3Client.getObject(getObjectRequest));
        String encodedFilename = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        log.info("파일다운로드 => {}", s3Key);
        return PdfResponseDTO.of(fileName, s3Key, resource, encodedFilename);
    }



    private String generatePresignedUrl(String filePath) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(filePath)
                .build();
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15)) // ⏱️ 10분 유효
                .getObjectRequest(getObjectRequest)
                .build();
        PresignedGetObjectRequest presignedRequest =
                s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }
}





//}
