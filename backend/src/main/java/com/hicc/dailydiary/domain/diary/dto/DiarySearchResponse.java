package com.hicc.dailydiary.domain.diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.AllArgsConstructor;

// 6. 일기 검색
@Getter
@AllArgsConstructor
@Schema(description = "일기 검색 결과 DTO")
public class DiarySearchResponse {

    @Schema(description = "일기 고유 ID", example = "1")
    private Long diaryId; //[cite: 6]

    @Schema(description = "작성 날짜", example = "2026-07-22")
    private String diaryDate; //[cite: 6]

    @Schema(description = "메모 미리보기 텍스트", example = "오늘은 비가 와서 기분이 처진다.")
    private String memoPreview; //[cite: 6]
}