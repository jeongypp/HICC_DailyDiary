package com.hicc.dailydiary.domain.diary.service;

import com.hicc.dailydiary.domain.diary.dto.*;
import com.hicc.dailydiary.domain.diary.entity.Diary;
import com.hicc.dailydiary.domain.diary.repository.DiaryRepository;
import com.hicc.dailydiary.domain.setting.entity.Domain;
import com.hicc.dailydiary.domain.setting.entity.Weight;
import com.hicc.dailydiary.domain.setting.service.SettingService;
import com.hicc.dailydiary.domain.ai.service.AiService;
import com.hicc.dailydiary.domain.weather.WeatherService;
import com.hicc.dailydiary.global.exception.CustomException;
import com.hicc.dailydiary.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final AiService aiService;
    private final WeatherService weatherService;
    private final SettingService settingService;

    @Transactional
    // 1. 일기 작성(생성)
    public Long createDiary(DiaryCreateRequest request) {
        if (diaryRepository.existsByDiaryDateAndIsDeletedFalse(request.getDiaryDate())) {
            throw new CustomException(ErrorCode.DIARY_ALREADY_EXISTS);
        }
        
        // 오늘 날짜인지 확인하여, 과거 밀린 일기면 날씨를 저장하지 않음(null)
        String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String weatherToSave = request.getDiaryDate().equals(todayDate) ? request.getWeather() : null;

        Diary diary = Diary.builder()
                .diaryDate(request.getDiaryDate())
                .domainId(request.getDomainId())
                .weightId(request.getWeightId())
                .score1(request.getScore1())
                .score2(request.getScore2())
                .score3(request.getScore3())
                .score4(request.getScore4())
                .score5(request.getScore5())
                .weather(weatherToSave)
                .memo(request.getMemo())
                .build();

        return diaryRepository.save(diary).getId();
    }

    @Transactional
    // 2. 일기 내용 수정
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
    // 3. 일기 삭제
    public void deleteDiary(Long diaryId) {
        Diary diary = diaryRepository.findByIdAndIsDeletedFalse(diaryId)
                .orElseThrow(() -> new CustomException(ErrorCode.DIARY_NOT_FOUND));

        diary.delete();
    }

    // 4. 월별 일기 조회(달력)
    public List<DiaryMonthlyResponse> getMonthlyDiaries(Integer year, Integer month) {
        String yearMonth = String.format("%04d-%02d", year, month);
        List<Diary> diaries = diaryRepository.findByDiaryDateStartingWithAndIsDeletedFalse(yearMonth);

        return diaries.stream().map(diary -> {
            Weight weightObj = settingService.getWeightById(diary.getWeightId());
            List<Integer> weights = List.of(
                    weightObj.getWeight1Value(), weightObj.getWeight2Value(),
                    weightObj.getWeight3Value(), weightObj.getWeight4Value(), weightObj.getWeight5Value()
            );

            // not null
            int s1 = diary.getScore1() != null ? diary.getScore1() : 0;
            int s2 = diary.getScore2() != null ? diary.getScore2() : 0;
            int s3 = diary.getScore3() != null ? diary.getScore3() : 0;
            int s4 = diary.getScore4() != null ? diary.getScore4() : 0;
            int s5 = diary.getScore5() != null ? diary.getScore5() : 0;

            // 각 영역 원점수에 가중치(1~10배)를 곱하여 합산
            double weightedSum = s1 * weights.get(0)
                    + s2 * weights.get(1)
                    + s3 * weights.get(2)
                    + s4 * weights.get(3)
                    + s5 * weights.get(4);
            
            // 총 가중치 합산
            double totalWeight = weights.stream().mapToInt(Integer::intValue).sum();
            
            // 일일 가중 평균 점수 계산 (-10.0 ~ 10.0)
            double avgScore = totalWeight > 0 ? Math.round(weightedSum / totalWeight * 10.0) / 10.0 : 0.0;

            return new DiaryMonthlyResponse(
                    diary.getId(),
                    diary.getDiaryDate(),
                    avgScore
            );
        }).toList();
    }

    // 5. 일기 상세 조회
    public DiaryDetailResponse getDiaryDetail(Long diaryId) {
        Diary diary = diaryRepository.findByIdAndIsDeletedFalse(diaryId)
                .orElseThrow(() -> new CustomException(ErrorCode.DIARY_NOT_FOUND));

        Domain domain = settingService.getDomainById(diary.getDomainId());
        List<String> domainNames = List.of(
                domain.getDomain1Name(), domain.getDomain2Name(),
                domain.getDomain3Name(), domain.getDomain4Name(), domain.getDomain5Name()
        );

        // 가중치 미반영 원점수(오각형 차트 UI)
        List<Integer> domainScores = List.of(
                diary.getScore1(),
                diary.getScore2(),
                diary.getScore3(),
                diary.getScore4(),
                diary.getScore5()
        );

        return new DiaryDetailResponse(
                diary.getId(),
                diary.getDiaryDate(),
                diary.getWeather(),
                domainNames,
                domainScores,
                diary.getMemo(),
                diary.getAiReply()
        );
    }

    // 6. 일기 검색
    public List<DiarySearchResponse> searchDiaries(String startDate, String endDate, String keyword) {
        List<Diary> diaries = diaryRepository.searchDiaries(startDate, endDate, keyword);

        return diaries.stream().map(diary -> {
            String memoPreview = generateMemoPreview(diary.getMemo(), keyword);

            return new DiarySearchResponse(
                    diary.getId(),
                    diary.getDiaryDate(),
                    memoPreview
            );
        }).toList();
    }

    // 일기 검색 시 메모 프리뷰 기능
    private String generateMemoPreview(String memo, String keyword) {
        if (memo == null || memo.isBlank()) return "";
        if (keyword == null || keyword.isBlank() || !memo.contains(keyword)) {
            return memo.length() > 25 ? memo.substring(0, 25) + "..." : memo;
        }

        int keywordIndex = memo.indexOf(keyword);
        int startIndex = Math.max(0, keywordIndex - 12);
        int endIndex = Math.min(memo.length(), keywordIndex + keyword.length() + 12);

        String prefix = (startIndex > 0) ? "..." : "";
        String suffix = (endIndex < memo.length()) ? "..." : "";

        String snippet = memo.substring(startIndex, endIndex);

        return prefix + snippet + suffix;
    }

    // 11. AI 피드백 생성용: 영역이름과 가중치 계산 후 추가메모와 함께 AiService에 전달
    @Transactional
    public String generateAndSaveAiFeedback(Long diaryId) {
        Diary diary = diaryRepository.findByIdAndIsDeletedFalse(diaryId)
                .orElseThrow(() -> new CustomException(ErrorCode.DIARY_NOT_FOUND));

        Domain domain = settingService.getDomainById(diary.getDomainId());
        List<String> domainNames = List.of(
                domain.getDomain1Name(), domain.getDomain2Name(),
                domain.getDomain3Name(), domain.getDomain4Name(), domain.getDomain5Name()
        );

        Weight weightObj = settingService.getWeightById(diary.getWeightId());
        List<Integer> weights = List.of(
                weightObj.getWeight1Value(), weightObj.getWeight2Value(),
                weightObj.getWeight3Value(), weightObj.getWeight4Value(), weightObj.getWeight5Value()
        );

        List<Integer> rawScores = List.of(
                diary.getScore1(),
                diary.getScore2(),
                diary.getScore3(),
                diary.getScore4(),
                diary.getScore5()
        );

        String aiReply = aiService.getAiFeedback(domainNames, rawScores, weights, diary.getMemo());
        diary.updateAiReply(aiReply);
        return aiReply;
    }
}