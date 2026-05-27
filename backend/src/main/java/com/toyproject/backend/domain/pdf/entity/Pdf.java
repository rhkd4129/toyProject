package com.toyproject.backend.domain.pdf.entity;


import com.toyproject.backend.domain.pdf.Enum.PdfTypeEnum;
import com.toyproject.backend.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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


    //중복값을 허ㅇ용하지 않고 null를 허용하지 않는데 최초  insert이후 업데이트 불가하다.
    @Column(unique = true, nullable = false, updatable = false)
    private String uuid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PdfTypeEnum pdfType;


    private boolean completed = false;

    public static Pdf createPdf(PdfTypeEnum pdfType){
        Pdf pdf = new Pdf();
        pdf.uuid = UUID.randomUUID().toString();
        pdf.pdfType = pdfType;
        return pdf;
    }

}
