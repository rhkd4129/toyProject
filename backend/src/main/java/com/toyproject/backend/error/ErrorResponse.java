package com.toyproject.backend.error;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
@Builder
@Getter
public class ErrorResponse {
    private String code;
    private String message;
    private Map<String, String> errors; //form filed에러
    // errors 없는 에러 응답 생성
    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .errors(new HashMap<>())
                .build();
    }

    // errors 포함한 에러 응답 생성
    public static ErrorResponse of(String code, String message, Map<String, String> errors) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .errors(errors)
                .build();
    }

}
