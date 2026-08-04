package com.hicc.dailydiary.domain.statistics.controller;

import com.hicc.dailydiary.domain.statistics.dto.StatisticsResponseDto;
import com.hicc.dailydiary.domain.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 주간 평균 감정 점수 조회[cite: 1]
     */
    @GetMapping("/average")
    public ResponseEntity<StatisticsResponseDto.WeeklyAverageResponse> getWeeklyAverage() {
        StatisticsResponseDto.WeeklyAverageResponse response = statisticsService.getWeeklyAverageScore();
        // 실제 프로젝트의 공통 Response 포맷에 맞춰 수정 (예: return ApiResponse.success(response);)
        return ResponseEntity.ok(response);
    }

    /**
     * 주간 감정 점수 추세 조회 (최근 7일)[cite: 2]
     */
    @GetMapping("/weekly")
    public ResponseEntity<StatisticsResponseDto.WeeklyTrendResponse> getWeeklyTrend() {
        StatisticsResponseDto.WeeklyTrendResponse response = statisticsService.getWeeklyTrend();
        return ResponseEntity.ok(response);
    }
}