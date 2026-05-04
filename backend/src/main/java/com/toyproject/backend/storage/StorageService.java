package com.toyproject.backend.storage;

import com.toyproject.backend.domain.pdf.dto.PdfRedisRequest;
import com.toyproject.backend.utils.FileUploadResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;


public interface StorageService {

    public PdfRedisRequest uploadFile(String taskId, MultipartFile file) throws IOException;

    public void downloadFile(String downloadPath);
}
