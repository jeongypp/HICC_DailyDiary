package com.hicc.dailydiary.domain.setting.controller;

import com.hicc.dailydiary.domain.setting.dto.DomainLatestResponseDto;
import com.hicc.dailydiary.domain.setting.dto.SettingRequestDto;
import com.hicc.dailydiary.domain.setting.dto.SettingResponseDto;
import com.hicc.dailydiary.domain.setting.service.SettingService;
import com.hicc.dailydiary.global.common.ApiResponse; // 추가된 임포트
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/domain")
public class SettingController {

    private final SettingService settingService;

    // [기존 로직] 최신 도메인 및 가중치 조회 (GET)
    @GetMapping("/latest")
    public ApiResponse<DomainLatestResponseDto> getLatestDomain() {
        DomainLatestResponseDto response = settingService.getLatestDomainAndWeight();

        // ApiResponse.success(상태코드, 커스텀코드, 메시지, 데이터) 형태로 반환
        return ApiResponse.success(
                200,
                "DOMAIN_GET_SUCCESS",
                "최신 가중치 및 키워드 조회 성공",
                response
        );
    }

    // [추가된 로직] 새로운 도메인 및 가중치 생성 (POST)
    @PostMapping
    public ApiResponse<SettingResponseDto.CreateResponse> createDomainAndWeight(@RequestBody SettingRequestDto request) {

        // 1. 서비스 로직 호출
        SettingResponseDto.CreateResponse result = settingService.createDomainAndWeight(request);

        // 2. 공통 응답 포맷인 ApiResponse 활용
        return ApiResponse.success(
                201,
                "DOMAIN_CREATE_SUCCESS",
                "새 도메인 버전이 저장되었습니다.",
                result
        );
    }
}