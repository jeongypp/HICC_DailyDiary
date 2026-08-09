package com.hicc.dailydiary.domain.ai;

public interface AiService {
    /**
     * 사용자의 일기 내용을 바탕으로 AI의 공감/위로 피드백을 반환.
     * @param diaryContent 사용자가 작성한 일기 내용
     * @return AI가 생성한 피드백 텍스트
     */
    String getAiFeedback(String diaryContent);
}
