package com.hicc.dailydiary.domain.statistics.service;

import com.hicc.dailydiary.domain.diary.entity.Diary;
import com.hicc.dailydiary.domain.diary.repository.DiaryRepository;
import com.hicc.dailydiary.domain.setting.entity.Weight;
import com.hicc.dailydiary.domain.setting.repository.WeightRepository;
import com.hicc.dailydiary.domain.statistics.dto.StatisticsResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
     * 주간 평균 감정 점수 조회 (-10점 ~ +10점 기준)
     */
    public StatisticsResponseDto.WeeklyAverageResponse getWeeklyAverageScore() {
        // ★ 버그 5번 해결: 클라우드 환경에서도 항상 한국 시간(KST)을 기준으로 날짜 계산
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate startDate = today.minusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String startDateStr = startDate.format(formatter);
        String endDateStr = today.format(formatter);

        // 1. 최근 7일치 일기 한 번에 조회
        List<Diary> weeklyDiaries = diaryRepository.searchDiaries(startDateStr, endDateStr, null);

        // ★ 성능 문제(N+1 쿼리) 해결: 조회된 일기들의 weightId를 모아서 한 번의 쿼리로 가중치 맵 구성
        List<Long> weightIds = weeklyDiaries.stream()
                .map(diary -> Long.valueOf(diary.getWeightId()))
                .distinct()
                .collect(Collectors.toList());
        Map<Long, Weight> weightMap = weightRepository.findAllById(weightIds).stream()
                .collect(Collectors.toMap(Weight::getId, w -> w));

        double totalScoreSum = 0.0;
        int validDaysCount = 0;

        // 2. 조회된 일기들의 점수를 계산
        for (Diary diary : weeklyDiaries) {
            Weight weight = weightMap.get(Long.valueOf(diary.getWeightId()));
            if (weight == null) continue;

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
     * 최근 7일간의 일일 평균 감정 점수 추세 조회 (-10점 ~ +10점 기준)
     */
    public StatisticsResponseDto.WeeklyTrendResponse getWeeklyTrend() {
        // ★ 버그 5번 해결: 클라우드 환경 타임존 방어
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate startDate = today.minusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String startDateStr = startDate.format(formatter);
        String endDateStr = today.format(formatter);

        // 1. 7일치 일기 한 번에 조회
        List<Diary> weeklyDiaries = diaryRepository.searchDiaries(startDateStr, endDateStr, null);

        // 2. 날짜를 Key로, Diary를 Value로 매핑
        Map<String, Diary> diaryMap = weeklyDiaries.stream()
                .collect(Collectors.toMap(Diary::getDiaryDate, d -> d));

        // ★ 성능 문제(N+1 쿼리) 해결: 가중치 맵 구성
        List<Long> weightIds = weeklyDiaries.stream()
                .map(diary -> Long.valueOf(diary.getWeightId()))
                .distinct()
                .collect(Collectors.toList());
        Map<Long, Weight> weightMap = weightRepository.findAllById(weightIds).stream()
                .collect(Collectors.toMap(Weight::getId, w -> w));

        List<String> dates = new ArrayList<>();
        List<Double> weightedScores = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            String dateString = today.minusDays(i).format(formatter);
            dates.add(dateString);

            Diary diary = diaryMap.get(dateString);

            if (diary != null) {
                Weight weight = weightMap.get(Long.valueOf(diary.getWeightId()));
                if (weight != null) {
                    double dailyScore = calculateDailyScore(diary, weight);
                    weightedScores.add(Math.round(dailyScore * 10.0) / 10.0);
                } else {
                    weightedScores.add(null);
                }
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
     * [내부 로직] 일일 점수 계산기 (가중 평균)
     * ★ 버그 4번 해결: null(미입력)인 점수는 강제로 0점 처리하지 않고, 평균 계산에서 완전히 제외시킵니다.
     */
    private double calculateDailyScore(Diary diary, Weight weight) {
        double weightedScoreSum = 0.0;
        double validTotalWeight = 0.0;

        // 점수가 null이 아닐 때만 분자(점수*가중치)와 분모(가중치)에 누적 합산
        if (diary.getScore1() != null) {
            weightedScoreSum += (diary.getScore1() * weight.getWeight1Value());
            validTotalWeight += weight.getWeight1Value();
        }
        if (diary.getScore2() != null) {
            weightedScoreSum += (diary.getScore2() * weight.getWeight2Value());
            validTotalWeight += weight.getWeight2Value();
        }
        if (diary.getScore3() != null) {
            weightedScoreSum += (diary.getScore3() * weight.getWeight3Value());
            validTotalWeight += weight.getWeight3Value();
        }
        if (diary.getScore4() != null) {
            weightedScoreSum += (diary.getScore4() * weight.getWeight4Value());
            validTotalWeight += weight.getWeight4Value();
        }
        if (diary.getScore5() != null) {
            weightedScoreSum += (diary.getScore5() * weight.getWeight5Value());
            validTotalWeight += weight.getWeight5Value();
        }

        // 모든 점수를 입력하지 않아 가중치 합이 0인 경우 방어 로직
        if (validTotalWeight == 0) return 0.0;

        return weightedScoreSum / validTotalWeight;
    }
}