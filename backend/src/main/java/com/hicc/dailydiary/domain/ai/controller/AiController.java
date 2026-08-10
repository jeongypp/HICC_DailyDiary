package com.hicc.dailydiary.domain.ai.controller;

import com.hicc.dailydiary.domain.ai.dto.AiAnalyzeRequest;
import com.hicc.dailydiary.domain.ai.dto.AiAnalyzeResponse;
import com.hicc.dailydiary.domain.diary.service.DiaryService;
import com.hicc.dailydiary.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AI API", description = "AI 피드백 생성 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai/analyze")
public class AiController {

    private final DiaryService diaryService;

    @Operation(summary = "AI 피드백 생성", description = "저장된 일기를 바탕으로 가중치와 영역을 반영해 AI 피드백을 생성하고 DB에 저장합니다.")
    @PostMapping("/analyze")
    public ApiResponse<AiAnalyzeResponse> analyzeDiary(@RequestBody AiAnalyzeRequest request) {
        String aiReply = diaryService.generateAndSaveAiFeedback(request.getDiaryId());
        return ApiResponse.success(200, "AI_ANALYZE_SUCCESS", "AI 피드백 생성 성공", new AiAnalyzeResponse(aiReply));
    }
}
