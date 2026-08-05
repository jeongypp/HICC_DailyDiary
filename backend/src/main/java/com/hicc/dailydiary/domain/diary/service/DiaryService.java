package com.hicc.dailydiary.domain.diary.service;

import com.hicc.dailydiary.domain.diary.dto.*;
import com.hicc.dailydiary.domain.diary.entity.Diary;
import com.hicc.dailydiary.domain.diary.repository.DiaryRepository;
import com.hicc.dailydiary.domain.ai.AiService;
import com.hicc.dailydiary.domain.weather.WeatherService;
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
    private final AiService aiService;
    private final WeatherService weatherService;

    @Transactional
    // 1. 일기 작성(생성)
    public Long createDiary(DiaryCreateRequest request) {
        if (diaryRepository.existsByDiaryDateAndIsDeletedFalse(request.getDiaryDate())) {
            throw new CustomException(ErrorCode.DIARY_ALREADY_EXISTS);
        }

        // TODO: AI API 연결 후 실제 값으로 교체
        // Mock API 호출 (실제 구현 전 임시용)
        String weatherMock = weatherService.getWeatherCondition(60, 127); // 서울 좌표 임시 사용
        String aiReplyMock = aiService.getAiFeedback(request.getMemo() != null && !request.getMemo().isBlank() 
                ? request.getMemo() : "특별한 일 없이 무난한 하루였어요.");

        Diary diary = Diary.builder()
                .diaryDate(request.getDiaryDate())
                .domainId(request.getDomainId())
                .weightId(request.getWeightId())
                .score1(request.getScore1())
                .score2(request.getScore2())
                .score3(request.getScore3())
                .score4(request.getScore4())
                .score5(request.getScore5())
                .weather(weatherMock)
                .memo(request.getMemo())
                .aiReply(aiReplyMock)
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

        // TODO: AI API 연결 후 실제 값으로 교체
        // 메모 수정 -> AI 피드백 재생성
        String aiReplyMock = aiService.getAiFeedback(request.getMemo() != null && !request.getMemo().isBlank() 
                ? request.getMemo() : "특별한 일 없이 무난한 하루였어요.");
        diary.updateAiReply(aiReplyMock);

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
            List<Integer> weights = getMockWeights(diary.getWeightId());

            // 각 영역 원점수에 가중치(1~10배)를 곱하여 합산
            int weightedSum = diary.getScore1() * weights.get(0)
                    + diary.getScore2() * weights.get(1)
                    + diary.getScore3() * weights.get(2)
                    + diary.getScore4() * weights.get(3)
                    + diary.getScore5() * weights.get(4);
            
            int avgScore = weightedSum / 5;

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

        List<String> domainNames = getMockDomainNames(diary.getDomainId());
        List<Integer> weights = getMockWeights(diary.getWeightId());

        // 각 영역 원점수에 가중치(1~10배)를 곱해 최종 점수 산출
        List<Integer> weightedScores = List.of(
                diary.getScore1() * weights.get(0),
                diary.getScore2() * weights.get(1),
                diary.getScore3() * weights.get(2),
                diary.getScore4() * weights.get(3),
                diary.getScore5() * weights.get(4)
        );

        return new DiaryDetailResponse(
                diary.getId(),
                diary.getDiaryDate(),
                diary.getWeather(),
                domainNames,
                weightedScores,
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

    // TODO: setting >> SettingService 호출로 실제 값 교체
    private List<Integer> getMockWeights(Integer weightId) {
        // 실제로는 weight_id를 통해 weight 테이블에서 weight1_value ~ weight5_value 를 조회해와야 함.
        return List.of(1, 2, 3, 2, 1); // 임시 가중치 값 (1~10배)
    }

    // TODO: setting 패키지 로직 pull 이후 협업 개발자의 서비스 호출로 교체할 임시 Mock 메서드
    private List<String> getMockDomainNames(Integer domainId) {
        // 실제로는 domain_id를 통해 domain 테이블에서 domain1_name ~ domain5_name 을 조회해와야 함.
        return List.of("수면", "식사", "학업", "관계", "운동");
    }

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
        // 프론트엔드에서 <strong> 태그를 인식하여 볼드 및 색상 처리
        String highlighted = snippet.replace(keyword, "<strong class=\"font-extrabold text-brand-600 dark:text-brand-400\">" + keyword + "</strong>");

        return prefix + highlighted + suffix;
    }
}