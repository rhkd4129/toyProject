package com.toyproject.backend.utils.storage;

import com.toyproject.backend.utils.FileUtils;
import lombok.RequiredArgsConstructor;
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

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@Profile("prod")
@RequiredArgsConstructor
public class S3StorageService implements StorageService{

    private final S3Client s3Client;

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private  String bucket;

    @Value("${cloud.aws.s3.prefix}")
    private  String uploadPathPattern;
    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename(); // "abc.pdf"
        String key = uploadPathPattern + FileUtils.generateKey(originalFilename);
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)          // 어느 S3 버킷에 저장할지 (버킷 = 최상위 폴더 개념)
                        .key(key)                // 저장될 파일 경로+이름 (예: "pdf/uuid_파일명.pdf")
                        .contentType(file.getContentType())  // 파일 형식 명시 (예: "application/pdf", "image/png")
                        .build(),                // PutObjectRequest 객체 완성
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
                // 업로드할 실제 파일 데이터 (바이트 스트림) , 파일 크기 (AWS가 얼마나 읽을지 알아야 함)
        );
        return generatePresignedUrl(key);
    }

    private String generatePresignedUrl(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
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
