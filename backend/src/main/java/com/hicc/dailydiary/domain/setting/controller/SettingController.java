package com.hicc.dailydiary.domain.setting.controller;

import com.hicc.dailydiary.domain.setting.dto.DomainLatestResponseDto;
import com.hicc.dailydiary.domain.setting.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/domain")
public class SettingController {

    private final SettingService settingService;

    @GetMapping("/latest")
    public ResponseEntity<DomainLatestResponseDto> getLatestDomain() {
        DomainLatestResponseDto response = settingService.getLatestDomainAndWeight();

        // TODO: 팀 내에 공통 응답 포맷(status, code, message, result)을 감싸는
        // GlobalResponseDto 같은 클래스가 있다면 ResponseEntity.ok(GlobalResponseDto.of(response)) 형태로 묶어서 반환해야 합니다.[cite: 1]
        return ResponseEntity.ok(response);
    }
}