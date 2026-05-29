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

    public PdfRedisRequest uploadFile(String taskId, MultipartFile file,String pdfType) throws IOException;

    public PdfResponseDTO downloadFile(String fileName)throws MalformedURLException;
}
