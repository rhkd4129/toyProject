package com.toyproject.backend.domain.pdf.controller;


import com.toyproject.backend.Emitter.SseEmitterService;
import com.toyproject.backend.domain.pdf.dto.PdfRedisRequest;
import com.toyproject.backend.domain.pdf.service.PdfService;
import com.toyproject.backend.utils.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
    @PostMapping("/create")
    public ResponseEntity<?> createPdf(@RequestParam("newPdf") MultipartFile newPdf) {
        log.info(" === createPdf 진입 === ");
        Result result = new Result();
        PdfRedisRequest pdfRedisRequest  = pdfService.createPdf(newPdf);
        result.setData(pdfRedisRequest);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @GetMapping(path = "/subscribe/{taskId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable String taskId) {
        //TODO 여기서 생성 EMITER를
        return sseEmitterService.getEmitter(taskId);
    }




}
//    @GetMapping("/pdf/download")
//    public ResponseEntity<?> downloadPdf(@RequestParam("taskId")String taskId){
//        Result result = new Result();
//        pdfService.getPdf(taskId);
//        return new ResponseEntity<>(result,HttpStatus.OK);
//}
