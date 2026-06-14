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

import java.io.IOException;
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


    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename(); // "abc.pdf"
//        String fileName = FileUtils.buildFileName(taskId,originalFilename);
        String filePath = uploadPathPattern+originalFilename;
//        String key = uploadPathPattern + FileUtils.generateKey(originalFilename);
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

    }

    @Override
    public PdfResponseDTO downloadFile(String fileName) {
        return new PdfResponseDTO(fileName);
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




//// 2026-06-13 결과 XLSX 저장 경로 prefix
//@Value("${cloud.aws.s3.result-key-prefix}")
//private String resultKeyPrefix;
//
//
//@Override
//public String uploadFile(MultipartFile file) throws IOException {
//    String originalFilename = file.getOriginalFilename(); // "abc.pdf"
////        String fileName = FileUtils.buildFileName(taskId,originalFilename);
//    String filePath = uploadPathPattern+originalFilename;
////        String key = uploadPathPattern + FileUtils.generateKey(originalFilename);
//    s3Client.putObject(
//            PutObjectRequest.builder()
//                    .bucket(bucket)          // 어느 S3 버킷에 저장할지 (버킷 = 최상위 폴더 개념)
//                    .key(filePath)                // 저장될 파일 경로+이름 (예: "pdf/uuid_파일명.pdf")
//                    .contentType(file.getContentType())  // 파일 형식 명시 (예: "application/pdf", "image/png")
//                    .build(),                // PutObjectRequest 객체 완성
//            RequestBody.fromInputStream(file.getInputStream(), file.getSize())
//            // 업로드할 실제 파일 데이터 (바이트 스트림) , 파일 크기 (AWS가 얼마나 읽을지 알아야 함)
//    );
//
//    // 2026-06-13 presigned URL 대신 S3 key 반환 (deleteFile에서 key로 삭제하기 위함)
//    // String presignedUrl = generatePresignedUrl(filePath);
//    // return presignedUrl;
//    log.info("파일업로드 => {}",filePath);
//    return filePath;
////        return new PdfRedisRequest(taskId,presignedUrl,pdfType); // presignedUrl이 path 역할
//}
//
//// 2026-06-13 S3 원본 파일 삭제 구현 (filePath = S3 key)
//@Override
//public void deleteFile(String filePath) {
//    s3Client.deleteObject(DeleteObjectRequest.builder()
//            .bucket(bucket)
//            .key(filePath)
//            .build());
//    log.info("S3 파일 삭제 => {}", filePath);
//}
//
//// 2026-06-13 S3에서 실제 파일 바이트 조회 구현
//// return new PdfResponseDTO(fileName);
//@Override
//public PdfResponseDTO downloadFile(String fileName) {
//    ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(
//            GetObjectRequest.builder().bucket(bucket).key(fileName).build());
//    String encodedFilename = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
//    return PdfResponseDTO.of(fileName, fileName, new ByteArrayResource(objectBytes.asByteArray()), encodedFilename);
//}
//
//private String generatePresignedUrl(String filePath) {
//    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//            .bucket(bucket)
//            .key(filePath)
//            .build();
//    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
//            .signatureDuration(Duration.ofMinutes(15)) // ⏱️ 10분 유효
//            .getObjectRequest(getObjectRequest)
//            .build();
//    PresignedGetObjectRequest presignedRequest =
//            s3Presigner.presignGetObject(presignRequest);
//    return presignedRequest.url().toString();
//}
//
//// 2026-06-13 StorageService 인터페이스 기본 메서드 오버라이드
//@Override
//public String generatePresignedDownloadUrl(String key) {
//    return generatePresignedUrl(key);
//}
//
//// 2026-06-13 결과 XLSX용 presigned PUT/GET URL 쌍 반환 (FastAPI가 업로드, Vue가 다운로드)
//@Override
//public String[] generateResultPresignedUrls(String taskId) {
//    String resultKey = resultKeyPrefix + taskId + "_result.xlsx";
//    String uploadUrl = generatePresignedPutUrl(resultKey);
//    String downloadUrl = generatePresignedUrl(resultKey);
//    return new String[]{uploadUrl, downloadUrl};
//}
//
//private String generatePresignedPutUrl(String key) {
//    PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(
//            PutObjectPresignRequest.builder()
//                    .signatureDuration(Duration.ofMinutes(15))
//                    .putObjectRequest(PutObjectRequest.builder().bucket(bucket).key(key).build())
//                    .build());
//    return presignedRequest.url().toString();
//}
//}
