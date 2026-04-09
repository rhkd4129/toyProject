package com.toyproject.backend.error;


import lombok.extern.slf4j.Slf4j;
import org.hibernate.JDBCException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Business 예외 처리
     */
    @ExceptionHandler(CommonException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(CommonException e) {
        log.error("BusinessException occurred: {}", e.getMessage());
        ErrorResponse errorResult = ErrorResponse.builder()
                .code(e.getErrorCode().getCode())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(errorResult, e.getErrorCode().getStatus());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validException(MethodArgumentNotValidException e){
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        ErrorResponse errorResult = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .errors(errors)
                .build();
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler({SignatureException.class, IllegalArgumentException.class})
    protected ResponseEntity<ErrorResponse> handleJwtException(Exception e) {
        log.error("JWT error occurred: {}", e.getMessage());
        ErrorCode errorCode;

    if (e instanceof IllegalArgumentException) {
            errorCode = ErrorCode.EMPTY_JWT_CLAIMS;
        } else {
            errorCode = ErrorCode.NOT_JWT_TOKEN;
        }

        ErrorResponse errorResult = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        return new ResponseEntity<>(errorResult, errorCode.getStatus());
    }



    @ExceptionHandler(JDBCException.class)
    protected ResponseEntity<ErrorResponse> handleJDBCException(Exception e) {
        log.error("JDBCException: {}", e.getMessage());
        ErrorCode errorCode;
        errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse ,errorCode.getStatus() );
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unhandled exception occurred: {}", e.getMessage(), e);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }
}
//    @ExceptionHandler(MissingRequestHeaderException.class)
//    public ResponseEntity<?> handleMissingHeader(MissingRequestHeaderException e) {
//        log.error("BusinessException occurred: {}", e.getMessage());
//        ErrorResponse errorResult = ErrorResponse.builder()
//                .code(e.getErrorCode().getCode())
//                .message(e.getMessage())
//                .build();
//        return new ResponseEntity<>(errorResult, e.getErrorCode().getStatus());
//    }

//    @ExceptionHandler(MissingRequestCookieException.class)
//    public ResponseEntity<?> handleMissingCookie(MissingCookieException ex) {
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                .body(new ErrorResponse(ErrorCode.INVALID_TOKEN));
//    }

// 유효성 검사 예외 처리
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
//            MethodArgumentNotValidException ex) {
//        Map<String, String> errors = new HashMap<>();
//        ex.getBindingResult().getAllErrors().forEach((error) -> {
//            String fieldName = ((FieldError) error).getField();
//            String errorMessage = error.getDefaultMessage();
//            errors.put(fieldName, errorMessage);
//        });
//
//        ApiResponse<Map<String, String>> response = ApiResponse.error(400, "Validation Failed");
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }

//public class JwtExceptionFilter extends OncePerRequestFilter {
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        try {
//            filterChain.doFilter(request, response);
//        } catch (ExpiredJwtException e) {
//            sendErrorResponse(response, ErrorCode.EXPIRED_JWT_TOKEN);
//        } catch (MalformedJwtException | SignatureException e) {
//            sendErrorResponse(response, ErrorCode.INVALID_JWT_SIGNATURE);
//        } catch (UnsupportedJwtException e) {
//            sendErrorResponse(response, ErrorCode.UNSUPPORTED_JWT_TOKEN);
//        }
//    }
//}
