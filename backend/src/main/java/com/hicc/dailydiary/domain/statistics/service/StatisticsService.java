package com.hicc.dailydiary.domain.statistics.service;

import com.hicc.dailydiary.domain.statistics.dto.StatisticsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    public StatisticsResponseDto getStatistics() {
        // TODO: 통계 데이터 조회 비즈니스 로직 작성
        return StatisticsResponseDto.builder()
                .totalCount(0L)
                .build();
    }
}