package com.toyproject.backend.domain.pdf.repository;

import com.toyproject.backend.domain.pdf.entity.Pdf;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PdfRepository {
    private final EntityManager em;


    public void createPdf(Pdf pdf){em.persist(pdf);}

    public Optional<Pdf> findByTaskId(String taskId) {
        try {
            Pdf pdf = em.createQuery("select p from Pdf p " +
                                    "where p.taskId =: taskId",
                            Pdf.class)
                    .setParameter("taskId", taskId).
                    getSingleResult();
            return Optional.of(pdf);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
