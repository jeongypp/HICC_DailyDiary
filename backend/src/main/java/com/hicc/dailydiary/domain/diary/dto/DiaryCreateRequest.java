package com.hicc.dailydiary.domain.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 1. 일기 작성(생성)
@Getter
@NoArgsConstructor
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

    @Schema(description = "날씨", example = "비", nullable = true)
    private String weather; //[cite: 3]

    @Schema(description = "추가 메모", example = "오늘은 비가 와서 기분이 처진다.", nullable = true)
    private String memo; //[cite: 3]
}