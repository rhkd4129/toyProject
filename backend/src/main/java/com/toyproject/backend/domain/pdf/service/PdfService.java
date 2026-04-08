package com.toyproject.backend.domain.pdf.service;


import com.toyproject.backend.utils.Result;
import com.toyproject.backend.utils.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


@Service
@Slf4j
@RequiredArgsConstructor
public class PdfService {

    private final FastApiClient fastApiClient;
    private final StorageService storageService;

    public Result createPdf(MultipartFile file) throws IOException {
        Result result  = new Result();
        String filePath = storageService.uploadFile(file);
        String a = fastApiClient.sendPdfPath(filePath);
        result.setMessage(a);
        return result;
    }

}
