package com.toyproject.backend.storage;
import com.toyproject.backend.domain.pdf.dto.PdfRedisRequest;
import com.toyproject.backend.domain.pdf.dto.PdfResponseDTO;
import com.toyproject.backend.utils.FileUploadResult;
import com.toyproject.backend.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
@Profile("local")
@Slf4j
public class LocalStorageService implements StorageService{

    @Value("${spring.servlet.multipart.location}")
    private  String uploadPathPattern;


    @Override
    public PdfRedisRequest uploadFile(String taskId, MultipartFile file,String pdfType) throws IOException {
        String originalFilename = file.getOriginalFilename(); // "abc.pdf"
        String fileName= FileUtils.buildFileName(taskId,originalFilename);
        String filePath = Paths.get(uploadPathPattern, fileName).toString();
        log.info("파일업로드 => {}",filePath);
        FileUtils.uploadFile(fileName, file.getBytes(), uploadPathPattern);
        return new PdfRedisRequest(taskId, filePath,originalFilename,pdfType);
    }

    @Override
    public PdfResponseDTO downloadFile(String fileName) throws MalformedURLException {
        String encodedFilename = null;
        Resource resource = null;
        String filePath = Paths.get(uploadPathPattern, fileName).toString();
        Path path = Paths.get(filePath);
        resource= new UrlResource(path.toUri());
        encodedFilename = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        return PdfResponseDTO.of(fileName,filePath,resource,encodedFilename);

    }


}
//PhotoResultResponseDTO photoResultResponseDTO = photoService.selectPhoto(currentMember, num,kind);
//String contentDisposition = "attachment; filename=\"" + photoResultResponseDTO.getEncodedFilename() + "\"";
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(photoResultResponseDTO.getResource());
