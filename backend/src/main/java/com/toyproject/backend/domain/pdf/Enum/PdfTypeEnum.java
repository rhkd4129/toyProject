package com.toyproject.backend.domain.pdf.Enum;

import lombok.Getter;

@Getter
public enum PdfTypeEnum {
    XLSX("XLSX생성기"),
    DIVIDE("초본분리기");

    private final String description;

    PdfTypeEnum(String description) {
        this.description = description;
    }

}
