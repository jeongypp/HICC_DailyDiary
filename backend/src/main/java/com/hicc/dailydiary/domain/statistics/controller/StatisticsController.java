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

    @GetMapping
    public ResponseEntity<StatisticsResponseDto> getStatistics() {
        StatisticsResponseDto response = statisticsService.getStatistics();
        return ResponseEntity.ok(response);
    }
}