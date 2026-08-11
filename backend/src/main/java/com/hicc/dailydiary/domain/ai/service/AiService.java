package com.hicc.dailydiary.domain.ai.service;

import java.util.List;

public interface AiService {
    /**
     * AI 피드백을 생성합니다.
     * @param domainNames 영역명 리스트
     * @param rawScores 가중치가 반영되지 않은 영역별 원점수 리스트 (-10 ~ 10점)
     * @param weights 사용자가 설정한 영역별 중요도 가중치 리스트 (1 ~ 10배)
     * @param memo 사용자가 작성한 일기 추가 메모
     * @return AI가 생성한 피드백 텍스트
     */
    String getAiFeedback(List<String> domainNames, List<Integer> rawScores, List<Integer> weights, String memo);
}
