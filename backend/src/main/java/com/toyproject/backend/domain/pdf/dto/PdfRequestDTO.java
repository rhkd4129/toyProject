package com.toyproject.backend.domain.pdf.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PdfRequestDTO {
    private String fileName;
    private String filePath;
}
