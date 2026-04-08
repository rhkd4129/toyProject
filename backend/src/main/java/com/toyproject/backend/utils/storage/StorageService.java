package com.toyproject.backend.utils.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;


public interface StorageService {

    public String uploadFile(MultipartFile file) throws IOException;
}
