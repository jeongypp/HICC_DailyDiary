package com.hicc.dailydiary.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400 Bad Request
    VALIDATION_ERROR(400, "VALIDATION_ERROR", "필수 입력값 누락 또는 형식 오류입니다."),

    // 404 Not Found
    DIARY_NOT_FOUND(404, "DIARY_NOT_FOUND", "해당하는 날짜의 일기 정보를 찾을 수 없습니다."),
    DIARY_NOT_EXIST(404, "DIARY_NOT_EXIST", "조회한 달에 일기가 없습니다."),

    // 409 Conflict
    DIARY_ALREADY_EXISTS(409, "DIARY_ALREADY_EXISTS", "해당 날짜에 이미 일기가 존재합니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."),
    AI_SERVER_ERROR(500, "AI_SERVER_ERROR", "AI API 서버 연동 실패"),

    // 502 Bad Gateway
    WEATHER_API_ERROR(502, "WEATHER_API_ERROR", "공공데이터포털 기상청 서버의 응답 지연 또는 장애 발생");

    private final int status;
    private final String code;
    private final String message;
}