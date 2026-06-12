package com.toyproject.backend.domain.pdf.controller;


import com.toyproject.backend.Emitter.SseEmitterService;
import com.toyproject.backend.domain.pdf.dto.PdfRedisRequest;
import com.toyproject.backend.domain.pdf.dto.PdfResponseDTO;
import com.toyproject.backend.domain.pdf.service.PdfService;
import com.toyproject.backend.utils.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.net.MalformedURLException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/pdf")
public class PdfController {

    private final PdfService pdfService;
    private final SseEmitterService sseEmitterService;

//    @RequestParam
//    단순한 값만 받을 때 — 문자열, 숫자, 파일
//    multipart/form-data에서 파일 하나만 받을 때 충분

    //    @RequestPart
//    파일 + JSON 데이터를 같이 받을 때
//    각 파트마다 Content-Type이 다를 수 있어서 이걸 구분해서 처리해줌
    @GetMapping("/create/id")
    public ResponseEntity<?> createPdfTaskId(){
        log.info(" ============= createPdfTaskId 진입 ================");
        Result result   = new Result();
        String pdfTaskId = pdfService.createPdfTaskId();
        result.setData(pdfTaskId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @PostMapping("/create")
    public ResponseEntity<?> createPdf(@RequestPart("newPdf") MultipartFile newPdf ,
                                       @RequestPart("pdfTaskId") String pdfTaskId,
                                       @RequestPart("pdfType") String pdfType
                                       ) {
        log.info(" ============= createPdf 진입 ================");
        Result result = new Result();
        PdfRedisRequest pdfRedisRequest = pdfService.createPdf(newPdf,pdfTaskId,pdfType);
        result.setData(pdfRedisRequest);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/update/metadata")
    public ResponseEntity<?> updateMetadata(@RequestParam("metadata")MultipartFile metadata){
        Result result = new Result();
        result.setMessage("업로드완료");
//        회사 메시지 큐?
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @GetMapping("/download/{fileName}")
    public ResponseEntity<?> downloadPdf(@PathVariable String fileName) throws MalformedURLException {
        log.info(" ============= downloadPdf 진입 ================");
        PdfResponseDTO pdfResponseDTO = pdfService.getPdf(fileName);
        String contentDisposition = "attachment; filename=\"" + pdfResponseDTO.getEncodedFilename() + "\"";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(pdfResponseDTO.getResource());
    }


    @GetMapping(path = "/subscribe/{taskId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable String taskId) {
        log.info(" ============= subscribe 진입 ================");
        return sseEmitterService.subscribePdf(taskId);

//        return sseEmitterService.getEmitter(taskId);
//        sseEmitterService.createEmitter(taskId);
    }


}
//1. Vue → POST /task/new
//        ← taskId 반환
//
//2. Vue → GET /subscribe/{taskId}        (SSE 연결)
//        ← "connected" 더미 이벤트 반환  (연결 확인용)
//         (연결 유지 중...)
//
//        3. Vue → POST /upload/{taskId}          (실제 파일 전송)
//Spring → /shared/uploads/ 저장
//                → Redis Stream(pdf:events) XADD {taskId, 파일경로}
//         ← 200 OK
