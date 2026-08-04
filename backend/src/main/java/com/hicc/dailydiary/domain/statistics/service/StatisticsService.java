package com.hicc.dailydiary.domain.statistics.service;

import com.hicc.dailydiary.domain.diary.entity.Diary;
import com.hicc.dailydiary.domain.diary.repository.DiaryRepository;
import com.hicc.dailydiary.domain.setting.entity.Weight;
import com.hicc.dailydiary.domain.setting.repository.WeightRepository;
import com.hicc.dailydiary.domain.statistics.dto.StatisticsResponseDto;
import com.hicc.dailydiary.domain.statistics.dto.StatisticsResponseDto.DailyScoreDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final DiaryRepository diaryRepository;
    private final WeightRepository weightRepository;

    public StatisticsResponseDto getWeeklyStatistics(String startDate, String endDate) {

        Weight latestWeight = weightRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new IllegalArgumentException("가중치 설정 데이터가 없습니다."));

        List<Diary> diaries = diaryRepository.searchDiaries(startDate, endDate, null);

        long totalCount = diaries.size();
        double maxScore = -1.0;
        DailyScoreDto bestDayDto = null;
        List<DailyScoreDto> dailyScores = new ArrayList<>();
        double MAX_DOMAIN_SCORE = 5.0;

        for (Diary diary : diaries) {
            double score1 = diary.getScore1();
            double score2 = diary.getScore2();
            double score3 = diary.getScore3();
            double score4 = diary.getScore4();
            double score5 = diary.getScore5();

            double maxPossibleTotal = (MAX_DOMAIN_SCORE * latestWeight.getWeight1Value())
                    + (MAX_DOMAIN_SCORE * latestWeight.getWeight2Value())
                    + (MAX_DOMAIN_SCORE * latestWeight.getWeight3Value())
                    + (MAX_DOMAIN_SCORE * latestWeight.getWeight4Value())
                    + (MAX_DOMAIN_SCORE * latestWeight.getWeight5Value());

            double dailyTotal = (score1 * latestWeight.getWeight1Value())
                    + (score2 * latestWeight.getWeight2Value())
                    + (score3 * latestWeight.getWeight3Value())
                    + (score4 * latestWeight.getWeight4Value())
                    + (score5 * latestWeight.getWeight5Value());

            double convertedScore100 = 0.0;
            if (maxPossibleTotal > 0) {
                convertedScore100 = Math.round((dailyTotal / maxPossibleTotal) * 10.0) / 10.0;
            }

            DailyScoreDto dto = DailyScoreDto.builder()
                    .date(diary.getDiaryDate())
                    .totalScore(convertedScore100)
                    .memo(diary.getMemo())
                    .aiReply(diary.getAiReply())
                    .isBestDay(false)
                    .domainScore1(score1)
                    .domainScore2(score2)
                    .domainScore3(score3)
                    .domainScore4(score4)
                    .domainScore5(score5)
                    .build();

            dailyScores.add(dto);

            if (convertedScore100 > maxScore) {
                maxScore = convertedScore100;
                bestDayDto = dto;
            }
        }

        if (bestDayDto != null) {
            bestDayDto.setBestDay();
        }

        return StatisticsResponseDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalCount(totalCount)
                .dailyScores(dailyScores)
                .build();
    }
}