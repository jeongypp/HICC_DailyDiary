package com.hicc.dailydiary.domain.statistics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class StatisticsResponseDto {

    // 주간 평균 감정 점수 조회 DTO[cite: 1]
    @Getter
    @Builder
    public static class WeeklyAverageResponse {
        @JsonProperty("total_weighted_average")
        private Double totalWeightedAverage; // 가중치 반영 전체 평균[cite: 1]
    }

    // 주간 감정 점수 추세 조회 DTO[cite: 2]
    @Getter
    @Builder
    public static class WeeklyTrendResponse {
        private List<String> dates; // 최근 7일 날짜 배열[cite: 2]

        @JsonProperty("weighted_scores")
        private List<Double> weightedScores; // 일별 가중치 평균 점수 배열 (일기가 없으면 null)[cite: 2]
    }
}