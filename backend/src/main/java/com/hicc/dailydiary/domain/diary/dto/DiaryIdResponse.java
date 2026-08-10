package com.hicc.dailydiary.domain.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

// 일기 고유 ID 기능
@Getter
@AllArgsConstructor
@Schema(description = "일기 생성/수정 성공 응답 DTO")
public class DiaryIdResponse {

    @Schema(description = "생성/수정된 일기 고유 ID", example = "1")
    private Long diaryId;
}