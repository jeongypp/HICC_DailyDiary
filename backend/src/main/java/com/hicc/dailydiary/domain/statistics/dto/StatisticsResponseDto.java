package com.hicc.dailydiary.domain.statistics.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StatisticsResponseDto {

    private Long totalCount;
    // 필요한 추가 통계 필드들을 여기에 정의하세요.

    @Builder
    public StatisticsResponseDto(Long totalCount) {
        this.totalCount = totalCount;
    }
}