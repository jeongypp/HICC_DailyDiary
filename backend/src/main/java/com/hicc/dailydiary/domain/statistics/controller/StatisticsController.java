package com.hicc.dailydiary.domain.statistics.controller;

import com.hicc.dailydiary.domain.statistics.dto.StatisticsResponseDto;
import com.hicc.dailydiary.domain.statistics.service.StatisticsService;
import com.hicc.dailydiary.global.common.ApiResponse; // 추가
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 주간 평균 감정 점수 조회
     */
    @GetMapping("/average")
    public ApiResponse<StatisticsResponseDto.WeeklyAverageResponse> getWeeklyAverage() {
        StatisticsResponseDto.WeeklyAverageResponse response = statisticsService.getWeeklyAverageScore();
        return ApiResponse.success(
                200,
                "STAT_AVG_SUCCESS",
                "주간 평균 점수 조회 성공",
                response
        );
    }

    /**
     * 주간 감정 점수 추세 조회 (최근 7일)
     */
    @GetMapping("/weekly")
    public ApiResponse<StatisticsResponseDto.WeeklyTrendResponse> getWeeklyTrend() {
        StatisticsResponseDto.WeeklyTrendResponse response = statisticsService.getWeeklyTrend();
        return ApiResponse.success(
                200,
                "STAT_WEEKLY_SUCCESS",
                "주간 추세 데이터 조회 성공",
                response
        );
    }
}