package com.toyproject.backend.domain.pdf.controller;


import com.toyproject.backend.domain.pdf.service.PdfService;
import com.toyproject.backend.utils.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/pdf")
public class PdfController {

    private final PdfService pdfService;
//    @RequestParam
//    단순한 값만 받을 때 — 문자열, 숫자, 파일
//    multipart/form-data에서 파일 하나만 받을 때 충분

//    @RequestPart
//    파일 + JSON 데이터를 같이 받을 때
//    각 파트마다 Content-Type이 다를 수 있어서 이걸 구분해서 처리해줌
    @PostMapping("/create")
    public ResponseEntity<?> createPdf(@RequestParam("newPdf") MultipartFile newPdf) {
        Result result = pdfService.createPdf(newPdf);
        log.info(" === createPdf 진입 === ");
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
//    Spring이 FastAPI를 호출하고 응답이 올 때까지 그냥 기다림.
//    FastAPI가 PDF 처리를 완료하면 결과를 바로 Spring으로 반환하고, Spring은 그걸 사용자에게 전달.
//    구현이 단순한 대신 타임아웃을 충분히 길게 설정해야 하고, 처리 중에는 HTTP 연결이 계속 유지


//    Spring이 FastAPI에 요청만 던지고 바로 jobId를 받아서 사용자에게 반환.
//    FastAPI는 백그라운드에서 PDF를 처리하고, 완료되면 결과를 Redis에 저장.
//    Spring은 Redis를 구독하고 있다가 결과가 들어오면 캐치.
//    사용자는 완료 알림을 받거나 프론트에서 주기적으로 완료 여부를 확인.
//    구현은 복잡하지만 타임아웃 걱정이 없고 서버 부하가 적습니다.

}
