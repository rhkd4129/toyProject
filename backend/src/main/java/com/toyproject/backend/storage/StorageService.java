package com.toyproject.backend.storage;

import com.toyproject.backend.domain.pdf.dto.PdfRedisRequest;
import com.toyproject.backend.domain.pdf.dto.PdfResponseDTO;
import com.toyproject.backend.utils.FileUploadResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;


public interface StorageService {

    public String uploadFile(MultipartFile file) throws IOException;

    public String uploadResultFile(String localFilePath);

    public PdfResponseDTO downloadFile(String fileName) throws MalformedURLException;

    public void deleteFile(String filePath);

    // 2026-06-13 S3 presigned URL 생성 메서드 (local에서는 빈 문자열 반환)
//    default String generatePresignedDownloadUrl(String key) { return ""; }
//    default String[] generateResultPresignedUrls(String taskId) { return new String[]{"", ""}; }
}
