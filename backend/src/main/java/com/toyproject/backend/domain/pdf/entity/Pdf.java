package com.toyproject.backend.domain.pdf.entity;


import com.toyproject.backend.domain.pdf.Enum.PdfStatus;
import com.toyproject.backend.domain.pdf.Enum.PdfTypeEnum;
import com.toyproject.backend.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name="PDF")
public class Pdf extends BaseEntity {
    @Id
    @SequenceGenerator(
            name = "PDF_SEQ",
            sequenceName = "PDF_SEQ",
            initialValue = 1,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy= GenerationType.SEQUENCE,
            generator = "PDF_SEQ")
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String taskId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PdfTypeEnum pdfType;

    @Column(nullable = false)
    private String originalFilename;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PdfStatus status;

    private LocalDateTime completedAt;

    public static Pdf createPdf(String taskId, PdfTypeEnum pdfType, String originalFilename) {
        Pdf pdf = new Pdf();
        pdf.taskId = taskId;
        pdf.pdfType = pdfType;
        pdf.originalFilename = originalFilename;
        pdf.status = PdfStatus.PROCESSING;
        return pdf;
    }

    public void complete() {
        this.status = PdfStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void fail() {
        this.status = PdfStatus.FAILED;
        this.completedAt = LocalDateTime.now();
    }

}
