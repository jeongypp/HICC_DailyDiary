package com.hicc.dailydiary.domain.diary.controller;

import com.hicc.dailydiary.domain.diary.dto.*;
import com.hicc.dailydiary.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Tag(name = "Diary API", description = "일기 작성, 조회, 수정, 삭제 관련 API")
@RestController
@RequestMapping("/api/diaries")
public class DiaryController {

    @Operation(summary = "일기 작성", description = "선택한 날짜, 영역 점수, 추가 메모, 날씨를 바탕으로 새로운 일기 데이터를 저장합니다.")
    @PostMapping
    public ApiResponse<DiaryIdResponse> createDiary(@RequestBody DiaryCreateRequest request) {
        // TODO: 서비스 로직 연결
        return ApiResponse.success(
                201,
                "DIARY_CREATE_SUCCESS",
                "일기가 저장되었습니다.",
                new DiaryIdResponse(1L)
        );
    }

    @Operation(summary = "일기 내용 수정", description = "기존에 작성된 특정 일기의 점수, 메모, 날씨를 수정합니다.")
    @PutMapping("/{diary_id}")
    public ApiResponse<DiaryIdResponse> updateDiary(
            @PathVariable("diary_id") Long diaryId,
            @RequestBody DiaryUpdateRequest request) {
        // TODO: 서비스 로직 연결
        return ApiResponse.success(
                200,
                "DIARY_UPDATE_SUCCESS",
                "일기가 수정되었습니다.",
                new DiaryIdResponse(diaryId)
        );
    }

    @Operation(summary = "일기 삭제", description = "DB에서 해당 일기 데이터를 소프트 딜리트 처리합니다.")
    @DeleteMapping("/{diary_id}")
    public ApiResponse<Void> deleteDiary(@PathVariable("diary_id") Long diaryId) {
        // TODO: 서비스 로직 연결
        return ApiResponse.success(
                200,
                "DIARY_DELETE_SUCCESS",
                "일기가 삭제되었습니다.",
                null
        );
    }

    @Operation(summary = "월별 일기장 조회", description = "특정 연월(YYYY/MM)에 작성된 1달간의 일기 요약 데이터를 불러옵니다.")
    @GetMapping
    public ApiResponse<List<DiaryMonthlyResponse>> getMonthlyDiaries(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        // TODO: 서비스 로직 연결
        return ApiResponse.success(
                200,
                "DIARY_LIST_SUCCESS",
                "월별 일기 목록 조회 성공",
                Collections.emptyList() // 빈 리스트 반환 (테스트용)
        );
    }

    @Operation(summary = "일기 검색 (다중 조건)", description = "날짜 범위, 본문 키워드, 해시태그 조건에 일치하는 일기 목록을 검색합니다.")
    @GetMapping("/search")
    public ApiResponse<List<DiarySearchResponse>> searchDiaries(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String keyword) {
        // TODO: 서비스 로직 연결
        return ApiResponse.success(
                200,
                "DIARY_SEARCH_SUCCESS",
                "일기 검색 성공",
                Collections.emptyList() // 빈 리스트 반환 (테스트용)
        );
    }

    @Operation(summary = "일기 상세 조회", description = "특정 날짜의 상세 일기 데이터와 5대 영역 점수, AI 피드백을 조회합니다.")
    @GetMapping("/{diary_id}")
    public ApiResponse<DiaryDetailResponse> getDiaryDetail(@PathVariable("diary_id") Long diaryId) {
        // TODO: 서비스 로직 연결
        return ApiResponse.success(
                200,
                "DIARY_DETAIL_SUCCESS",
                "일기 상세 조회 성공",
                null // 테스트용 null
        );
    }
}