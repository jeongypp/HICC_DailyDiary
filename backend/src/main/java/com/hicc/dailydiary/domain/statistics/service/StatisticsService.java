package com.hicc.dailydiary.domain.statistics.service;

import com.hicc.dailydiary.domain.diary.entity.Diary;
import com.hicc.dailydiary.domain.diary.repository.DiaryRepository;
// TODO: Weight 패키지 경로는 실제 프로젝트에 맞게 수정해 주세요.
import com.hicc.dailydiary.domain.setting.entity.Weight;
import com.hicc.dailydiary.domain.setting.repository.WeightRepository;
import com.hicc.dailydiary.domain.statistics.dto.StatisticsResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private static final double MAX_SCALE_SCORE = 5.0; // 5점 척도 기준

    private final DiaryRepository diaryRepository;
    private final WeightRepository weightRepository;

    /**
     * 주간 평균 감정 점수 조회 (100점 만점 기준)
     */
    public StatisticsResponseDto.WeeklyAverageResponse getWeeklyAverageScore() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String startDateStr = startDate.format(formatter);
        String endDateStr = today.format(formatter);

        // 1. 기존에 있는 다중 검색 쿼리를 활용해 최근 7일치 일기 한 번에 조회!
        List<Diary> weeklyDiaries = diaryRepository.searchDiaries(startDateStr, endDateStr, null);

        double totalScoreSum = 0.0;
        int validDaysCount = 0;

        // 2. 조회된 일기들의 점수를 계산
        for (Diary diary : weeklyDiaries) {
            Weight weight = weightRepository.findById(Long.valueOf(diary.getWeightId()))
                    .orElseThrow(() -> new IllegalArgumentException("가중치 정보를 찾을 수 없습니다."));

            totalScoreSum += calculateDailyScore100(diary, weight);
            validDaysCount++;
        }

        // 일기가 하루도 없으면 0.0, 있으면 평균 계산 (소수점 첫째 자리까지)
        double average = (validDaysCount == 0) ? 0.0 : Math.round((totalScoreSum / validDaysCount) * 10.0) / 10.0;

        return StatisticsResponseDto.WeeklyAverageResponse.builder()
                .totalWeightedAverage(average)
                .build();
    }

    /**
     * 최근 7일간의 일일 평균 감정 점수 추세 조회 (100점 만점 기준)
     */
    public StatisticsResponseDto.WeeklyTrendResponse getWeeklyTrend() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String startDateStr = startDate.format(formatter);
        String endDateStr = today.format(formatter);

        // 1. 7일치 일기 한 번에 조회
        List<Diary> weeklyDiaries = diaryRepository.searchDiaries(startDateStr, endDateStr, null);

        // 2. 날짜를 Key로 사용하여 쉽게 찾을 수 있도록 Map으로 변환
        Map<String, Diary> diaryMap = weeklyDiaries.stream()
                .collect(Collectors.toMap(Diary::getDiaryDate, d -> d));

        List<String> dates = new ArrayList<>();
        List<Double> weightedScores = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            String dateString = today.minusDays(i).format(formatter);
            dates.add(dateString);

            // 3. Map에서 해당 날짜의 일기가 있는지 확인
            Diary diary = diaryMap.get(dateString);

            if (diary != null) {
                Weight weight = weightRepository.findById(Long.valueOf(diary.getWeightId()))
                        .orElseThrow(() -> new IllegalArgumentException("가중치 정보를 찾을 수 없습니다."));

                double dailyScore = calculateDailyScore100(diary, weight);
                weightedScores.add(Math.round(dailyScore * 10.0) / 10.0);
            } else {
                weightedScores.add(null); // 일기가 없으면 null
            }
        }

        return StatisticsResponseDto.WeeklyTrendResponse.builder()
                .dates(dates)
                .weightedScores(weightedScores)
                .build();
    }

    /**
     * [내부 로직] 일일 점수 100점 만점 환산 계산기
     */
    private double calculateDailyScore100(Diary diary, Weight weight) {
        // null 값 방어 (Integer 래퍼 클래스 처리)
        int score1 = diary.getScore1() != null ? diary.getScore1() : 0;
        int score2 = diary.getScore2() != null ? diary.getScore2() : 0;
        int score3 = diary.getScore3() != null ? diary.getScore3() : 0;
        int score4 = diary.getScore4() != null ? diary.getScore4() : 0;
        int score5 = diary.getScore5() != null ? diary.getScore5() : 0;

        double totalWeight = weight.getWeight1Value() + weight.getWeight2Value() +
                weight.getWeight3Value() + weight.getWeight4Value() +
                weight.getWeight5Value();

        if (totalWeight == 0) return 0.0; // 분모가 0이 되는 오류 방지

        double weightedScoreSum = (score1 * weight.getWeight1Value()) +
                (score2 * weight.getWeight2Value()) +
                (score3 * weight.getWeight3Value()) +
                (score4 * weight.getWeight4Value()) +
                (score5 * weight.getWeight5Value());

        return (weightedScoreSum / (MAX_SCALE_SCORE * totalWeight)) * 100.0;
    }
}