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
@Transactional(readOnly = true)
public class SettingService {

    private final DomainRepository domainRepository;
    private final WeightRepository weightRepository;
    // 1. 특정 ID의 도메인(영역명) 정보 제공
    public Domain getDomainById(Integer domainId) {
        return domainRepository.findById(domainId.longValue())
                .orElseThrow(() -> new IllegalArgumentException("DOMAIN_NOT_FOUND"));
    }

    // 2. 특정 ID의 가중치 정보 제공
    // 2. 특정 ID의 가중치 정보 제공
    public Weight getWeightById(Integer weightId) {
        return weightRepository.findById(weightId.longValue())
                .orElseThrow(() -> new IllegalArgumentException("WEIGHT_NOT_FOUND"));
    }

    // [수정된 로직] 진짜 '최신(Latest)' 도메인 및 가중치 조회 (GET)
    public DomainLatestResponseDto getLatestDomainAndWeight() {
        // 하드코딩된 1L을 빼고, DB에서 가장 마지막에 저장된(ID가 가장 큰) 데이터를 가져옵니다.
        Domain domain = domainRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new IllegalArgumentException("DOMAIN_NOT_FOUND: 설정된 도메인 데이터가 없습니다."));

        Weight weight = weightRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new IllegalArgumentException("WEIGHT_NOT_FOUND: 설정된 가중치 데이터가 없습니다."));

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

    // [기존 로직 유지] 새로운 도메인 및 가중치 생성 (POST)
    @Transactional
    public SettingResponseDto.CreateResponse createDomainAndWeight(SettingRequestDto request) {

        Domain newDomain = Domain.builder()
                .domain1Name(request.getDomain1Name())
                .domain2Name(request.getDomain2Name())
                .domain3Name(request.getDomain3Name())
                .domain4Name(request.getDomain4Name())
                .domain5Name(request.getDomain5Name())
                .build();
        Domain savedDomain = domainRepository.save(newDomain);

        Weight newWeight = Weight.builder()
                .weight1Value(request.getWeight1Value())
                .weight2Value(request.getWeight2Value())
                .weight3Value(request.getWeight3Value())
                .weight4Value(request.getWeight4Value())
                .weight5Value(request.getWeight5Value())
                .build();
        Weight savedWeight = weightRepository.save(newWeight);

        return SettingResponseDto.CreateResponse.builder()
                .domainId(savedDomain.getId())
                .weightId(savedWeight.getId())
                .build();
    }
}