package com.hicc.dailydiary.domain.weather.controller;

import com.hicc.dailydiary.domain.weather.WeatherService;
import com.hicc.dailydiary.domain.weather.dto.WeatherRequest;
import com.hicc.dailydiary.domain.weather.dto.WeatherResponse;
import com.hicc.dailydiary.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Weather API", description = "날씨 정보 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    @Operation(summary = "날씨 텍스트 조회", description = "주어진 nx, ny 좌표의 단기예보를 조회하여 날씨 텍스트를 반환합니다.")
    @GetMapping
    public ApiResponse<WeatherResponse> getWeather(@ModelAttribute WeatherRequest request) {
        String weatherText = weatherService.getWeatherCondition(request.getNx(), request.getNy());
        return ApiResponse.success(
                200,
                "WEATHER_FETCH_SUCCESS",
                "날씨 조회 성공",
                new WeatherResponse(weatherText)
        );
    }
}
