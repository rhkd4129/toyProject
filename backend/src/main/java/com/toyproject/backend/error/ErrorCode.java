package com.toyproject.backend.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통에러
    REDIS_CONNECT_ERROR(HttpStatus.UNAUTHORIZED, "REDIS-001", "REDIS 에러"),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-001", "서버 오류가 발생했습니다"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON-002", "잘못된 입력값입니다"),

    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH-001", "ID나 비밀번호를 확인해주세요"),
    // 재발급
    EXPIRED_JWT_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "JWT-001", "인증시간이 초과되었습니다"),
    // 로그아웃 후 재로그인
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "JWT-002", "잘못된 JWT 서명입니다"),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "JWT-003", "토큰의 형식이 올바르지 않습니다"),
    NOT_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "JWT-004", "토큰정보가 없습니다(로그인이 필요한 정보입니다.)"),
    EMPTY_JWT_CLAIMS(HttpStatus.UNAUTHORIZED, "JWT-005", "토큰 페이로드가 비어있습니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "JWT-006", "블랙리스트  토큰"),
    EXPIRED_JWT_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "JWT-007", "인증시간이 초과되었습니다"),



    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-001", "회원을 찾을 수 없습니다"),
    DUPLICATE_MEMBER_ID(HttpStatus.CONFLICT, "MEMBER-002", "이미 존재하는 아이디입니다"),
    UNAUTHORIZED_MEMBER(HttpStatus.FORBIDDEN, "MEMBER-003", "권한이 없는 회원입니다"),


    //Post
    POST_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "POST-TYPE-001", "게시물 구분을 찾을 수 없습니다."),
    BOARD_N0_SUCH(HttpStatus.NOT_FOUND, "BOARD-001", "해당 게시글을 찾을 수 없습니다."),

    //Pdf
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PDF-001", "파일 업로드에 실패했습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;

}
