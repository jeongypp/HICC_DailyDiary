package com.hicc.dailydiary.domain.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "월별 일기 요약 정보 DTO")
public class DiaryMonthlyResponse {

    @Schema(description = "일기 식별번호", example = "1")
    private Long diaryId; //[cite: 5]

    @Schema(description = "작성 날짜", example = "2026-07-22")
    private String diaryDate; //[cite: 5]

    @Schema(description = "가중치 반영 평균 점수", example = "65")
    private Integer weightedAvg; //[cite: 5]
}