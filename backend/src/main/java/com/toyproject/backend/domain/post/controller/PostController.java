package com.toyproject.backend.domain.post.controller;

import com.toyproject.backend.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
@RequestMapping("/api//post")
@RequiredArgsConstructor   /// final | @NonNull 필드를 파라미터로 받는 생성자 자동으로 만듬
public class PostController {

    private final PostService postService;
    private final RestTemplate restTemplate;

//    @GetMapping("/")
//    public ResponseEntity<?> listPost(){
//        log.info("getData");
//        String result = restTemplate.getForObject("http://localhost:8084/", String.class);
//        log.info("FastAPI response: {}", result);
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }
    @GetMapping("/list")
    public ResponseEntity<?> posts(){
        return new ResponseEntity<>("s", HttpStatus.OK);
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<?> post(@PathVariable(name = "id") Long id){
        return new ResponseEntity<>("s", HttpStatus.OK);
    }
}
