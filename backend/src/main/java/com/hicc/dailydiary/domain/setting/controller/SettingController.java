package com.hicc.dailydiary.domain.setting.controller;

import com.hicc.dailydiary.domain.setting.dto.DomainLatestResponseDto;
import com.hicc.dailydiary.domain.setting.dto.SettingRequestDto;
import com.hicc.dailydiary.domain.setting.dto.SettingResponseDto;
import com.hicc.dailydiary.domain.setting.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/domain")
public class SettingController {

    private final SettingService settingService;

    // [기존 로직] 최신 도메인 및 가중치 조회 (GET)
    @GetMapping("/latest")
    public ResponseEntity<DomainLatestResponseDto> getLatestDomain() {
        DomainLatestResponseDto response = settingService.getLatestDomainAndWeight();

        // TODO: 팀 내에 공통 응답 포맷(status, code, message, result)을 감싸는
        // GlobalResponseDto 같은 클래스가 있다면 ResponseEntity.ok(GlobalResponseDto.of(response)) 형태로 묶어서 반환해야 합니다.
        return ResponseEntity.ok(response);
    }

    // [추가된 로직] 새로운 도메인 및 가중치 생성 (POST)
    @PostMapping
    public ResponseEntity<Map<String, Object>> createDomainAndWeight(@RequestBody SettingRequestDto request) {

        // 1. 서비스 로직 호출
        SettingResponseDto.CreateResponse result = settingService.createDomainAndWeight(request);

        // 2. 명세서에 정의된 공통 응답 포맷으로 조합[cite: 1]
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", 201);
        response.put("code", "DOMAIN_CREATE_SUCCESS");
        response.put("message", "새 도메인 버전이 저장되었습니다.");
        response.put("result", result);

        // 3. HTTP Status 201(Created) 반환[cite: 1]
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}