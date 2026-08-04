package com.hicc.dailydiary.domain.statistics.controller;

import com.hicc.dailydiary.domain.statistics.dto.StatisticsResponseDto;
import com.hicc.dailydiary.domain.statistics.service.StatisticsService;
import com.hicc.dailydiary.global.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Statistics API", description = "통계 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "월간(달력) 통계 조회", description = "특정 1달(시작일~종료일)의 통계와 일기 데이터를 조회합니다.")
    @GetMapping("/monthly")  // weekly에서 monthly로 변경
    public ApiResponse<StatisticsResponseDto> getMonthlyStatistics(
            @RequestParam String startDate,
            @RequestParam String endDate) {

        StatisticsResponseDto response = statisticsService.getMonthlyStatistics(startDate, endDate);

        return ApiResponse.success(
                200,
                "STATISTICS_MONTHLY_SUCCESS", // 커스텀 상태코드도 월간으로 변경
                "월간 달력 통계 조회 성공",
                response
        );
    }
}