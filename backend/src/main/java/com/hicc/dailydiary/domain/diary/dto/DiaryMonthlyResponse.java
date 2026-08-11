package com.hicc.dailydiary.domain.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

// 4. 월별 일기장 조회
@Getter
@AllArgsConstructor
@Schema(description = "월별 일기 요약 정보 DTO")
public class DiaryMonthlyResponse {

    @Schema(description = "일기 고유 ID", example = "1")
    private Long diaryId; //[cite: 5]

    @Schema(description = "작성 날짜", example = "2026-07-22")
    private String diaryDate; //[cite: 5]

    @Schema(description = "일일 감정 가중 평균 점수 (-10.0 ~ 10.0)", example = "6.3")
    private Double weightedAvg; //[cite: 5]
}