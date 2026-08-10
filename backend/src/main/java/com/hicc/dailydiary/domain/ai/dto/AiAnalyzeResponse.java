package com.hicc.dailydiary.domain.ai.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "AI 피드백 응답 DTO")
public class AiAnalyzeResponse {

    @Schema(description = "생성된 AI 피드백 텍스트", example = "오늘 하루도 수고하셨습니다!")
    private String aiReply;
}
