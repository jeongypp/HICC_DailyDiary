package com.hicc.dailydiary.domain.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "날씨 조회 응답 DTO")
public class WeatherResponse {

    @Schema(description = "날씨 텍스트 (맑음, 구름많음, 흐림, 비, 눈 등)", example = "맑음")
    private String weather;
}
