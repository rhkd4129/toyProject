package com.toyproject.backend.storage;
import com.toyproject.backend.utils.FileUploadResult;
import com.toyproject.backend.utils.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


@Service
@Profile("local")
public class LocalStorageService implements StorageService{

    @Value("${spring.servlet.multipart.location}")
    private  String uploadPathPattern;



    @Override
    public FileUploadResult  uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename(); // "abc.pdf"
        String key = FileUtils.generateKey(originalFilename);
        FileUtils.uploadFile(key, file.getBytes(), uploadPathPattern);
        return new FileUploadResult(uploadPathPattern+key , key);
    }

    @Override
    public void downloadFile() {

    }


}
