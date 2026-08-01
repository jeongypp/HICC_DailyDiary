package com.hicc.dailydiary.domain.setting.service;

import com.hicc.dailydiary.domain.setting.dto.DomainLatestResponseDto;
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

    public DomainLatestResponseDto getLatestDomainAndWeight() {
        // API 명세서에 따라 MVP 기준 식별번호 = 1 고정으로 조회합니다.
        Domain domain = domainRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("DOMAIN_NOT_FOUND: 초기 설정 데이터가 없습니다.")); // 404 에러 상황[cite: 1]

        Weight weight = weightRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("DOMAIN_NOT_FOUND: 초기 설정 데이터가 없습니다.")); // 404 에러 상황[cite: 1]

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
}