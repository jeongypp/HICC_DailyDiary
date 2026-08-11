package com.hicc.dailydiary.domain.statistics.service;

import com.hicc.dailydiary.domain.diary.entity.Diary;
import com.hicc.dailydiary.domain.diary.repository.DiaryRepository;
import com.hicc.dailydiary.domain.setting.entity.Weight;
import com.hicc.dailydiary.domain.setting.repository.WeightRepository;
import com.hicc.dailydiary.domain.statistics.dto.StatisticsResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final DiaryRepository diaryRepository;
    private final WeightRepository weightRepository;

    /**
     * 주간 평균 감정 점수 조회 (해당 주간: 월~일 고정, -10점 ~ +10점 기준)
     */
    public StatisticsResponseDto.WeeklyAverageResponse getWeeklyAverageScore() {
        LocalDate today = LocalDate.now();
        // 무조건 이번 주 월요일을 시작일로!
        LocalDate startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        // 무조건 이번 주 일요일을 종료일로!
        LocalDate endDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String startDateStr = startDate.format(formatter);
        String endDateStr = endDate.format(formatter);

        // 월~일 7일치 일기 조회
        List<Diary> weeklyDiaries = diaryRepository.searchDiaries(startDateStr, endDateStr, null);

        double totalScoreSum = 0.0;
        int validDaysCount = 0;

        for (Diary diary : weeklyDiaries) {
            Weight weight = weightRepository.findById(Long.valueOf(diary.getWeightId()))
                    .orElseThrow(() -> new IllegalArgumentException("가중치 정보를 찾을 수 없습니다."));

            totalScoreSum += calculateDailyScore(diary, weight);
            validDaysCount++;
        }

        // 일기가 하루도 없으면 0.0, 있으면 평균 계산 (소수점 첫째 자리까지)
        double average = (validDaysCount == 0) ? 0.0 : Math.round((totalScoreSum / validDaysCount) * 10.0) / 10.0;

        return StatisticsResponseDto.WeeklyAverageResponse.builder()
                .totalWeightedAverage(average)
                .build();
    }

    /**
     * 해당 주간(월~일)의 일일 평균 감정 점수 추세 조회 (-10점 ~ +10점 기준)
     */
    public StatisticsResponseDto.WeeklyTrendResponse getWeeklyTrend() {
        LocalDate today = LocalDate.now();
        // 무조건 이번 주 월요일부터 일요일까지 픽스!
        LocalDate startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String startDateStr = startDate.format(formatter);
        String endDateStr = endDate.format(formatter);

        List<Diary> weeklyDiaries = diaryRepository.searchDiaries(startDateStr, endDateStr, null);

        Map<String, Diary> diaryMap = weeklyDiaries.stream()
                .collect(Collectors.toMap(Diary::getDiaryDate, d -> d));

        List<String> dates = new ArrayList<>();
        List<Double> weightedScores = new ArrayList<>();

        // 오늘 기준이 아니라, 무조건 '월요일(startDate)'부터 '일요일'까지 7번 반복
        for (int i = 0; i < 7; i++) {
            String dateString = startDate.plusDays(i).format(formatter);
            dates.add(dateString);

            Diary diary = diaryMap.get(dateString);

            if (diary != null) {
                Weight weight = weightRepository.findById(Long.valueOf(diary.getWeightId()))
                        .orElseThrow(() -> new IllegalArgumentException("가중치 정보를 찾을 수 없습니다."));

                double dailyScore = calculateDailyScore(diary, weight);
                weightedScores.add(Math.round(dailyScore * 10.0) / 10.0);
            } else {
                weightedScores.add(null);
            }
        }

        return StatisticsResponseDto.WeeklyTrendResponse.builder()
                .dates(dates)
                .weightedScores(weightedScores)
                .build();
    }

    /**
     * [내부 로직] 일일 점수 환산 계산기 (-10~10 가중 평균)
     */
    private double calculateDailyScore(Diary diary, Weight weight) {
        int score1 = diary.getScore1() != null ? diary.getScore1() : 0;
        int score2 = diary.getScore2() != null ? diary.getScore2() : 0;
        int score3 = diary.getScore3() != null ? diary.getScore3() : 0;
        int score4 = diary.getScore4() != null ? diary.getScore4() : 0;
        int score5 = diary.getScore5() != null ? diary.getScore5() : 0;

        double totalWeight = weight.getWeight1Value() + weight.getWeight2Value() +
                weight.getWeight3Value() + weight.getWeight4Value() +
                weight.getWeight5Value();

        if (totalWeight == 0) return 0.0;

        double weightedScoreSum = (score1 * weight.getWeight1Value()) +
                (score2 * weight.getWeight2Value()) +
                (score3 * weight.getWeight3Value()) +
                (score4 * weight.getWeight4Value()) +
                (score5 * weight.getWeight5Value());

        return weightedScoreSum / totalWeight;
    }
}