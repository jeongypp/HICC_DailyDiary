package com.hicc.dailydiary.domain.statistics.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class StatisticsResponseDto {

    private String startDate;
    private String endDate;
    private Long totalCount;
    private List<DailyScoreDto> dailyScores;

    @Builder
    public StatisticsResponseDto(String startDate, String endDate, Long totalCount, List<DailyScoreDto> dailyScores) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalCount = totalCount;
        this.dailyScores = dailyScores;
    }

    @Getter
    @NoArgsConstructor
    public static class DailyScoreDto {
        private String date;
        private double totalScore;
        private String memo;
        private String aiReply;
        private boolean isBestDay;

        private double domainScore1;
        private double domainScore2;
        private double domainScore3;
        private double domainScore4;
        private double domainScore5;

        @Builder
        public DailyScoreDto(String date, double totalScore, String memo, String aiReply, boolean isBestDay,
                             double domainScore1, double domainScore2, double domainScore3, double domainScore4, double domainScore5) {
            this.date = date;
            this.totalScore = totalScore;
            this.memo = memo;
            this.aiReply = aiReply;
            this.isBestDay = isBestDay;
            this.domainScore1 = domainScore1;
            this.domainScore2 = domainScore2;
            this.domainScore3 = domainScore3;
            this.domainScore4 = domainScore4;
            this.domainScore5 = domainScore5;
        }

        public void setBestDay() {
            this.isBestDay = true;
        }
    }
}