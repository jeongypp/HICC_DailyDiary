package com.hicc.dailydiary.domain.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "날씨 조회 요청 DTO")
public class WeatherRequest {
    
    @Schema(description = "X 좌표", example = "58")
    private Integer nx;

    @Schema(description = "Y 좌표", example = "127")
    private Integer ny;
}
