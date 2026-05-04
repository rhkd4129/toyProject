package com.toyproject.backend.storage;
import com.toyproject.backend.domain.pdf.dto.PdfRedisRequest;
import com.toyproject.backend.utils.FileUploadResult;
import com.toyproject.backend.utils.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Paths;


@Service
@Profile("local")
public class LocalStorageService implements StorageService{

    @Value("${spring.servlet.multipart.location}")
    private  String uploadPathPattern;


    @Override
    public PdfRedisRequest uploadFile(String taskId, MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename(); // "abc.pdf"
        String fileName= FileUtils.buildFileName(taskId,originalFilename);
        FileUtils.uploadFile(fileName, file.getBytes(), uploadPathPattern);
        String filePath = Paths.get(uploadPathPattern, fileName).toString();
        return new PdfRedisRequest(taskId, filePath,originalFilename);
    }

    @Override
    public void downloadFile(String downloadPath){
        return;
//        Path path = Paths.get(downloadPath);
//        Resource resource= UrlResource(path.toUri());

    }


}
//PhotoResultResponseDTO photoResultResponseDTO = photoService.selectPhoto(currentMember, num,kind);
//String contentDisposition = "attachment; filename=\"" + photoResultResponseDTO.getEncodedFilename() + "\"";
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(photoResultResponseDTO.getResource());
