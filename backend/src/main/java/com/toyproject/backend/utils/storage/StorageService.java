package com.toyproject.backend.utils.storage;

import com.toyproject.backend.utils.FileUploadResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;


public interface StorageService {

    public FileUploadResult uploadFile(MultipartFile file) throws IOException;
}
