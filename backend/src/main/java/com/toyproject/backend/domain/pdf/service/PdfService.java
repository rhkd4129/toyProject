package com.toyproject.backend.domain.pdf.service;

import com.toyproject.backend.utils.FileUtils;
import com.toyproject.backend.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class PdfService {

    @Value("${spring.servlet.multipart.location}")
    private String uploadPathPattern;

    private FastApiClient fastApiClient;

    public PdfService(FastApiClient fastApiClient) {
        this.fastApiClient = fastApiClient;
    }

    public Result createPdf(MultipartFile file) throws IOException {
        Result result  = new Result();
        String fileName = file.getOriginalFilename();
        FileUtils.uploadFile(fileName, file.getBytes(), uploadPathPattern);
//        String fullPath = filePath + File.separator + fileName;
        String filePath = Paths.get(uploadPathPattern,fileName).toString();
        String jobId = UUID.randomUUID().toString();
        String a = fastApiClient.sendPdfPath(filePath,jobId);
        result.setMessage(a);
        return result;




    }

}
