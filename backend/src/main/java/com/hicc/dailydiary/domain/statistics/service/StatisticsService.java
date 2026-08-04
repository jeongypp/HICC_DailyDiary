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

    // 이름 변경: getWeeklyStatistics -> getMonthlyStatistics
    public StatisticsResponseDto getMonthlyStatistics(String startDate, String endDate) {

        Weight latestWeight = weightRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new IllegalArgumentException("가중치 설정 데이터가 없습니다."));

        // 프론트에서 1달 치 범위(예: 2026-08-01 ~ 2026-08-31)를 넘기면 1달 데이터를 다 가져옴
        List<Diary> diaries = diaryRepository.searchDiaries(startDate, endDate, null);

        long totalCount = diaries.size();
        double maxScore = -1.0;
        DailyScoreDto bestDayDto = null;
        List<DailyScoreDto> dailyScores = new ArrayList<>();
        double MAX_DOMAIN_SCORE = 5.0;

        // 평균 계산용 합계 변수
        double sumTotalScore = 0.0;
        double sumDomain1 = 0.0, sumDomain2 = 0.0, sumDomain3 = 0.0, sumDomain4 = 0.0, sumDomain5 = 0.0;

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

            // 합계 누적
            sumTotalScore += convertedScore100;
            sumDomain1 += score1; sumDomain2 += score2; sumDomain3 += score3; sumDomain4 += score4; sumDomain5 += score5;

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

            // 1달 내 최고 점수(Best Day) 찾기
            if (convertedScore100 > maxScore) {
                maxScore = convertedScore100;
                bestDayDto = dto;
            }
        }

        if (bestDayDto != null) {
            bestDayDto.setBestDay(); // 1달 중 최고점인 날에 true 체크
        }

        // 평균 계산 (일기가 있을 때만)
        double avgTotal = totalCount > 0 ? Math.round((sumTotalScore / totalCount) * 10.0) / 10.0 : 0.0;
        double avgD1 = totalCount > 0 ? Math.round((sumDomain1 / totalCount) * 10.0) / 10.0 : 0.0;
        double avgD2 = totalCount > 0 ? Math.round((sumDomain2 / totalCount) * 10.0) / 10.0 : 0.0;
        double avgD3 = totalCount > 0 ? Math.round((sumDomain3 / totalCount) * 10.0) / 10.0 : 0.0;
        double avgD4 = totalCount > 0 ? Math.round((sumDomain4 / totalCount) * 10.0) / 10.0 : 0.0;
        double avgD5 = totalCount > 0 ? Math.round((sumDomain5 / totalCount) * 10.0) / 10.0 : 0.0;

        return StatisticsResponseDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalCount(totalCount)
                .monthlyAverageScore(avgTotal)
                .monthlyAvgDomain1(avgD1)
                .monthlyAvgDomain2(avgD2)
                .monthlyAvgDomain3(avgD3)
                .monthlyAvgDomain4(avgD4)
                .monthlyAvgDomain5(avgD5)
                .dailyScores(dailyScores)
                .build();
    }
}