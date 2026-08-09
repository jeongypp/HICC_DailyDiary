package com.hicc.dailydiary.domain.setting.service;

import com.hicc.dailydiary.domain.setting.dto.DomainLatestResponseDto;
import com.hicc.dailydiary.domain.setting.dto.SettingRequestDto;
import com.hicc.dailydiary.domain.setting.dto.SettingResponseDto;
import com.hicc.dailydiary.domain.setting.entity.Domain;
import com.hicc.dailydiary.domain.setting.entity.Weight;
import com.hicc.dailydiary.domain.setting.repository.DomainRepository;
import com.hicc.dailydiary.domain.setting.repository.WeightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 클래스 레벨: 기본적으로 읽기 전용 적용
public class SettingService {

    private final DomainRepository domainRepository;
    private final WeightRepository weightRepository;

    // [기존 로직] 최신 도메인 및 가중치 조회 (GET)
    public DomainLatestResponseDto getLatestDomainAndWeight() {
        // API 명세서에 따라 MVP 기준 식별번호 = 1 고정으로 조회합니다.
        Domain domain = domainRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("DOMAIN_NOT_FOUND: 초기 설정 데이터가 없습니다.")); // 404 에러 상황

        Weight weight = weightRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("DOMAIN_NOT_FOUND: 초기 설정 데이터가 없습니다.")); // 404 에러 상황

        // DB에서 가져온 실제 엔티티 데이터를 DTO에 매핑하여 반환합니다.
        return DomainLatestResponseDto.builder()
                .weightId(weight.getId())
                .domainId(domain.getId())
                .domain1Name(domain.getDomain1Name())
                .weight1Value(weight.getWeight1Value())
                .domain2Name(domain.getDomain2Name())
                .weight2Value(weight.getWeight2Value())
                .domain3Name(domain.getDomain3Name())
                .weight3Value(weight.getWeight3Value())
                .domain4Name(domain.getDomain4Name())
                .weight4Value(weight.getWeight4Value())
                .domain5Name(domain.getDomain5Name())
                .weight5Value(weight.getWeight5Value())
                .build();
    }

    // [추가된 로직] 새로운 도메인 및 가중치 생성 (POST)
    @Transactional // ★ 중요: 데이터를 저장(Insert)해야 하므로 readOnly를 덮어씁니다!
    public SettingResponseDto.CreateResponse createDomainAndWeight(SettingRequestDto request) {

        // 1. 새로운 도메인(키워드) 엔티티 생성 및 저장 (기존 데이터 덮어쓰기 아님)[cite: 1]
        Domain newDomain = Domain.builder()
                .domain1Name(request.getDomain1Name())
                .domain2Name(request.getDomain2Name())
                .domain3Name(request.getDomain3Name())
                .domain4Name(request.getDomain4Name())
                .domain5Name(request.getDomain5Name())
                .build();
        Domain savedDomain = domainRepository.save(newDomain);

        // 2. 새로운 가중치 엔티티 생성 및 저장[cite: 1]
        Weight newWeight = Weight.builder()
                .weight1Value(request.getWeight1Value())
                .weight2Value(request.getWeight2Value())
                .weight3Value(request.getWeight3Value())
                .weight4Value(request.getWeight4Value())
                .weight5Value(request.getWeight5Value())
                .build();
        Weight savedWeight = weightRepository.save(newWeight);

        // 3. 새로 발급된 ID를 DTO에 담아 반환[cite: 1]
        return SettingResponseDto.CreateResponse.builder()
                .domainId(savedDomain.getId())
                .weightId(savedWeight.getId())
                .build();
    }
}