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

    // 🌟 플로우 다이어그램 반영 (누락됐던 월간 종합 통계 추가)
    private double monthlyAverageScore; // 이번 달 평균 총점
    private double monthlyAvgDomain1;   // 이번 달 레이더 차트 영역 1 평균
    private double monthlyAvgDomain2;
    private double monthlyAvgDomain3;
    private double monthlyAvgDomain4;
    private double monthlyAvgDomain5;

    private List<DailyScoreDto> dailyScores;

    @Builder
    public StatisticsResponseDto(String startDate, String endDate, Long totalCount,
                                 double monthlyAverageScore, double monthlyAvgDomain1,
                                 double monthlyAvgDomain2, double monthlyAvgDomain3,
                                 double monthlyAvgDomain4, double monthlyAvgDomain5,
                                 List<DailyScoreDto> dailyScores) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalCount = totalCount;
        this.monthlyAverageScore = monthlyAverageScore;
        this.monthlyAvgDomain1 = monthlyAvgDomain1;
        this.monthlyAvgDomain2 = monthlyAvgDomain2;
        this.monthlyAvgDomain3 = monthlyAvgDomain3;
        this.monthlyAvgDomain4 = monthlyAvgDomain4;
        this.monthlyAvgDomain5 = monthlyAvgDomain5;
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