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

    @Operation(summary = "주간 통계 조회", description = "특정 1주일(시작일~종료일)의 통계와 일기 데이터를 조회합니다. 날짜 형식은 YYYY-MM-DD 입니다.")
    @GetMapping("/weekly")
    public ApiResponse<StatisticsResponseDto> getWeeklyStatistics(
            @RequestParam String startDate,
            @RequestParam String endDate) {

        // Service 호출로 통계 데이터 가져오기
        StatisticsResponseDto response = statisticsService.getWeeklyStatistics(startDate, endDate);

        // DiaryController와 동일한 ApiResponse.success 규격 적용
        return ApiResponse.success(
                200,
                "STATISTICS_WEEKLY_SUCCESS",
                "주간 통계 조회 성공",
                response
        );
    }
}