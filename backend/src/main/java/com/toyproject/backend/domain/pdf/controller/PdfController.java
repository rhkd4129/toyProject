package com.toyproject.backend.domain.pdf.controller;


import com.toyproject.backend.domain.pdf.service.PdfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oracle.ucp.proxy.annotation.Post;
import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/pdf")
public class PdfController {

    private final PdfService pdfService;

    @PostMapping("/create")
    public ResponseEntity<?> createPdf(){
        log.info(" === createPdf 진입 === ");
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
