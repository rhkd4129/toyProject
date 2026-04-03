package com.toyproject.backend.domain.post.service;

import com.toyproject.backend.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;

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

}
