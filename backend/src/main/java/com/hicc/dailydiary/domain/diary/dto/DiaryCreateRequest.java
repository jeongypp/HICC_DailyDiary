package com.hicc.dailydiary.domain.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Setter;

// 1. 일기 작성(생성)
@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "일기 작성 요청 DTO")
public class DiaryCreateRequest {

    @Schema(description = "일기 작성 날짜", example = "2026-07-22")
    private String diaryDate; //[cite: 3]

    @Schema(description = "적용된 영역 이름 버전", example = "1")
    private Integer domainId;

    @Schema(description = "적용된 가중치 버전", example = "1")
    private Integer weightId; //[cite: 3]

    @Schema(description = "영역별 점수 1 (-10~10)", example = "8")
    private Integer score1; //[cite: 3]
    @Schema(description = "영역별 점수 2 (-10~10)", example = "-5")
    private Integer score2; //[cite: 3]
    @Schema(description = "영역별 점수 3 (-10~10)", example = "0")
    private Integer score3; //[cite: 3]
    @Schema(description = "영역별 점수 4 (-10~10)", example = "7")
    private Integer score4; //[cite: 3]
    @Schema(description = "영역별 점수 5 (-10~10)", example = "9")
    private Integer score5; //[cite: 3]

    @Schema(description = "날씨 (프론트엔드에서 조회 후 전달)", example = "맑음")
    private String weather;

    @Schema(description = "추가 메모", example = "오늘은 비가 와서 기분이 처진다.", nullable = true)
    private String memo;
}