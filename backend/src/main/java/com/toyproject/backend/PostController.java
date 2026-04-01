package com.toyproject.backend;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor   /// final | @NonNull 필드를 파라미터로 받는 생성자 자동으로 만듬
public class PostController  {

    private final RestTemplate restTemplate;

    @GetMapping("/")
    public ResponseEntity<?> listPost(){
        log.info("getData");
        String result = restTemplate.getForObject("http://localhost:8084/", String.class);
        log.info("FastAPI response: {}", result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
//@Service
//public class AIService {
//
//    private final WebClient webClient = WebClient.builder()
//            .baseUrl("http://localhost:8000")
//            .build();
//
//    public String processImage(MultipartFile image) {
//
//        return webClient.post()
//                .uri("/process-image")
//                .contentType(MediaType.MULTIPART_FORM_DATA)
//                .bodyValue(image.getResource())
//                .retrieve()
//                .bodyToMono(String.class)
//                .block(); // 동기처럼 기다림
//    }
//}
