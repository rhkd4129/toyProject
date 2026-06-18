package com.toyproject.backend.domain.pdf.enums;

import lombok.Getter;

@Getter
public enum PdfStatus {
    PROCESSING,
    COMPLETED,
    FAILED,     //python에서 에러가 발생한경우
    FAILED_002  // redis steam에 값 설정시 에러
}
