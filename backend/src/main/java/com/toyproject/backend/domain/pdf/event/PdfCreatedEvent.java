package com.toyproject.backend.domain.pdf.event;

import com.toyproject.backend.domain.pdf.dto.PdfRedisRequest;
import lombok.AllArgsConstructor;

public record PdfCreatedEvent(PdfRedisRequest pdfRedisRequest) { }
