package com.hicc.dailydiary.domain.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "일기 작성 요청 DTO")
public class DiaryCreateRequest {

    // ★ 버그 수정: 반드시 YYYY-MM-DD 형식만 들어오도록 정규식 강제
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$", message = "날짜 형식은 YYYY-MM-DD 이어야 합니다.")
    @Schema(description = "일기 작성 날짜", example = "2026-07-22")
    private String diaryDate;

    @Schema(description = "적용된 영역 이름 버전", example = "1")
    private Integer domainId;

    @Schema(description = "적용된 가중치 버전", example = "1")
    private Integer weightId;

    @Schema(description = "영역별 점수 1 (-10~10)", example = "8")
    private Integer score1;
    @Schema(description = "영역별 점수 2 (-10~10)", example = "-5")
    private Integer score2;
    @Schema(description = "영역별 점수 3 (-10~10)", example = "0")
    private Integer score3;
    @Schema(description = "영역별 점수 4 (-10~10)", example = "7")
    private Integer score4;
    @Schema(description = "영역별 점수 5 (-10~10)", example = "9")
    private Integer score5;

    @Schema(description = "추가 메모", example = "오늘은 비가 와서 기분이 처진다.", nullable = true)
    private String memo;
}