package com.toyproject.backend.domain.pdf.entity;


import com.toyproject.backend.domain.post.Enum.PdfTypeEnum;
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
            name = "POST_SEQ",
            sequenceName = "POST_SEQ",
            initialValue = 1,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy= GenerationType.SEQUENCE,
            generator = "POST_SEQ")
    private Long id;


    //중복값을 허ㅇ용하지 않고 null를 허용하지 않는데 최초  insert이후 업데이트 불가하다.
    @Column(unique = true, nullable = false, updatable = false)
    private String uuid = UUID.randomUUID().toString();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PdfTypeEnum pdfType;


    private boolean completed = false;

}
