package com.hicc.dailydiary.domain.ai.service;

import java.util.List;

public interface AiService {
    /**
     * 사용자의 일기 내용(점수 및 메모)을 바탕으로 AI의 공감/위로 피드백을 반환.
     * @param domains 5대 영역 이름 리스트
     * @param scores 영역별 점수 리스트
     * @param memo 사용자가 작성한 일기 메모
     * @return AI가 생성한 피드백 텍스트
     */
    String getAiFeedback(List<String> domains, List<Integer> scores, String memo);
}
