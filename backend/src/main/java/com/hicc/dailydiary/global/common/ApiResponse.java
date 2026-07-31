package com.hicc.dailydiary.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private final int status;
    private final String code;
    private final String message;
    private final T result;

    // 정상 응답 생성 메서드
    public static <T> ApiResponse<T> success(int status, String code, String message, T result) {
        return new ApiResponse<>(status, code, message, result);
    }

    // 에러 응답 생성 메서드
    public static <T> ApiResponse<T> error(int status, String code, String message) {
        return new ApiResponse<>(status, code, message, null);
    }
}