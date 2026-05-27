package com.toyproject.backend.domain.pdf.repository;

import com.toyproject.backend.domain.pdf.entity.Pdf;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PdfRepository {
    private final EntityManager em;


    public void createPdf(Pdf pdf){em.persist(pdf);}
}
