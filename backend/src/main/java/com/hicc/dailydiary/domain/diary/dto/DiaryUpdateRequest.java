package com.hicc.dailydiary.domain.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Setter;

// 2. 일기 내용 수정 기능
@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "일기 내용 수정 요청 DTO")
public class DiaryUpdateRequest {

    @Schema(description = "수정할 원본 점수 1", example = "9")
    private Integer score1; //[cite: 2]
    @Schema(description = "수정할 원본 점수 2", example = "6")
    private Integer score2; //[cite: 2]
    @Schema(description = "수정할 원본 점수 3", example = "0")
    private Integer score3; //[cite: 2]
    @Schema(description = "수정할 원본 점수 4", example = "8")
    private Integer score4; //[cite: 2]
    @Schema(description = "수정할 원본 점수 5", example = "9")
    private Integer score5; //[cite: 2]

    @Schema(description = "수정할 메모", example = "비가 그쳤다!", nullable = true)
    private String memo; //[cite: 2]
}