package com.hicc.dailydiary.domain.diary.service;

import com.hicc.dailydiary.domain.diary.dto.*;
import com.hicc.dailydiary.domain.diary.entity.Diary;
import com.hicc.dailydiary.domain.diary.repository.DiaryRepository;
import com.hicc.dailydiary.global.exception.CustomException;
import com.hicc.dailydiary.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private final DiaryRepository diaryRepository;

    @Transactional
    public Long createDiary(DiaryCreateRequest request) {
        if (diaryRepository.existsByDiaryDateAndIsDeletedFalse(request.getDiaryDate())) {
            throw new CustomException(ErrorCode.DIARY_ALREADY_EXISTS);
        }

        Diary diary = Diary.builder()
                .diaryDate(request.getDiaryDate())
                .keywordId(request.getKeywordId())
                .weightId(request.getWeightId())
                .score1(request.getScore1())
                .score2(request.getScore2())
                .score3(request.getScore3())
                .score4(request.getScore4())
                .score5(request.getScore5())
                .weather(request.getWeather())
                .memo(request.getMemo())
                .build();

        return diaryRepository.save(diary).getId();
    }

    @Transactional
    public Long updateDiary(Long diaryId, DiaryUpdateRequest request) {
        Diary diary = diaryRepository.findByIdAndIsDeletedFalse(diaryId)
                .orElseThrow(() -> new CustomException(ErrorCode.DIARY_NOT_FOUND));

        diary.update(
                request.getScore1(),
                request.getScore2(),
                request.getScore3(),
                request.getScore4(),
                request.getScore5(),
                request.getMemo()
        );

        return diary.getId();
    }

    @Transactional
    public void deleteDiary(Long diaryId) {
        Diary diary = diaryRepository.findByIdAndIsDeletedFalse(diaryId)
                .orElseThrow(() -> new CustomException(ErrorCode.DIARY_NOT_FOUND));

        diary.delete();
    }

    public List<DiaryMonthlyResponse> getMonthlyDiaries(Integer year, Integer month) {
        String yearMonth = String.format("%04d-%02d", year, month);
        List<Diary> diaries = diaryRepository.findByDiaryDateStartingWithAndIsDeletedFalse(yearMonth);

        return diaries.stream().map(diary -> {
            int totalScore = diary.getScore1() + diary.getScore2() + diary.getScore3() + diary.getScore4() + diary.getScore5();
            int avgScore = totalScore / 5;

            return new DiaryMonthlyResponse(
                    diary.getId(),
                    diary.getDiaryDate(),
                    avgScore
            );
        }).toList();
    }

    public DiaryDetailResponse getDiaryDetail(Long diaryId) {
        Diary diary = diaryRepository.findByIdAndIsDeletedFalse(diaryId)
                .orElseThrow(() -> new CustomException(ErrorCode.DIARY_NOT_FOUND));

        List<String> keywordNames = List.of("수면", "식사", "학업", "관계", "운동");
        List<Integer> scores = List.of(diary.getScore1(), diary.getScore2(), diary.getScore3(), diary.getScore4(), diary.getScore5());

        return new DiaryDetailResponse(
                diary.getId(),
                diary.getDiaryDate(),
                diary.getWeather(),
                keywordNames,
                scores,
                diary.getMemo(),
                diary.getAiReply()
        );
    }

    public List<DiarySearchResponse> searchDiaries(String startDate, String endDate, String keyword) {
        List<Diary> diaries = diaryRepository.searchDiaries(startDate, endDate, keyword);

        return diaries.stream().map(diary -> {
            String memoPreview = diary.getMemo() != null && diary.getMemo().length() > 20
                    ? diary.getMemo().substring(0, 20) + "..."
                    : diary.getMemo();

            return new DiarySearchResponse(
                    diary.getId(),
                    diary.getDiaryDate(),
                    memoPreview
            );
        }).toList();
    }
}