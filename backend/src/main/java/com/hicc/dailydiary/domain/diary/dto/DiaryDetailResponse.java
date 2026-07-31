package com.hicc.dailydiary.domain.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import java.util.List;

@Getter
@Schema(description = "일기 상세 정보 응답 DTO")
public class DiaryDetailResponse {

    @Schema(description = "일기 고유 ID", example = "1")
    private Long diaryId; //[cite: 7]

    @Schema(description = "작성 날짜", example = "2026-07-22")
    private String diaryDate; //[cite: 7]

    @Schema(description = "날씨", example = "비")
    private String weather; //[cite: 7]

    @Schema(description = "영역별 키워드 이름 배열", example = "[\"수면\", \"식사\", \"학업\", \"관계\", \"운동\"]")
    private List<String> keywordNames; //[cite: 7]

    @Schema(description = "가중치 반영 점수 배열", example = "[80, 50, 0, 70, 90]")
    private List<Integer> weightedScores; //[cite: 7]

    @Schema(description = "추가 메모", example = "오늘은 비가 와서 기분이 처진다 #비 #우울")
    private String memo; //[cite: 7]

    @Schema(description = "AI 피드백 내용", example = "학업(0점)에 집중하지 못했지만, 운동(90점)으로 활력을 찾은 하루군요!")
    private String aiReply; //[cite: 7]
}