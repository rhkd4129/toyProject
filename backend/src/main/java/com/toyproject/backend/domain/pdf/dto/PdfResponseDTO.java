package com.toyproject.backend.domain.pdf.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

@Getter
@NoArgsConstructor
public class PdfResponseDTO {
    private String fileName;
    private String filePath;
    private Resource resource;
    private String encodedFilename;

    public PdfResponseDTO(String fileName) {
        this.fileName = fileName;
    }

    public PdfResponseDTO(String fileName, String filePath, Resource resource, String encodedFilename) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.resource = resource;
        this.encodedFilename = encodedFilename;
    }

    public static PdfResponseDTO of(String fileName, String filePath, Resource resource, String encodedFilename) {
        return new PdfResponseDTO(fileName, filePath, resource, encodedFilename);
    }
//    핵심 이유 3가지
//      1. 변환 로직을 DTO 안에 캡슐화
            // 변환 책임이 DTO 자신에게 있음
            // Service/Controller가 PostType 내부 구조를 알 필요 없음
        //2. 생성자 직접 호출 방지
            // 생성자 직접 쓰면 이 코드가 여러 곳에 흩어짐
            // PostType 구조가 바뀌면 모든 곳을 다 수정해야 함
//    public of()
}
