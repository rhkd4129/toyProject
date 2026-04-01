package com.toyproject.backend;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.boot.logging.LoggerGroup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor   /// final | @NonNull 필드를 파라미터로 받는 생성자 자동으로 만듬
public class PostController  {
    @GetMapping("/")
    public ResponseEntity<?> listPost(){
        log.info("getData");
        return new ResponseEntity<>("hello ! world", HttpStatus.OK);}
}
