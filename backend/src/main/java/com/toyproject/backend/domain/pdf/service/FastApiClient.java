package com.toyproject.backend.domain.pdf.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

@Component
public class FastApiClient {
    private final WebClient webClient;

    public FastApiClient(@Value("${fastapi.url}") String fastApiUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(fastApiUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }


    public String sendPdfPath(String filePath) {
        return webClient.post()
                .uri("/process-pdf")
                .bodyValue(Map.of(
                        "filePath", filePath
                ))
                .retrieve() // 요청을 실제로 실행 및 응답을 가져오겠다는 의미
                .bodyToMono(String.class)
                .timeout(Duration.ofMinutes(5))  // 타임아웃 5분
                .block();  // 동기 방식이면 block()
    }


}
