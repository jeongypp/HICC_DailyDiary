package com.hicc.dailydiary.domain.ai.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "AI 피드백 요청 DTO")
public class AiAnalyzeRequest {

    @Schema(description = "분석할 일기 고유 ID", example = "1")
    private Long diaryId;
}
