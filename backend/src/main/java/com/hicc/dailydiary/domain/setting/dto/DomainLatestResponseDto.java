package com.hicc.dailydiary.domain.setting.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class) // 🔥 이 부분 추가
public class DomainLatestResponseDto {
    private Long weightId;       // 가중치 버전 식별번호
    private Long domainId;       // 영역 이름 버전 식별번호[cite: 1]

    private String domain1Name;  // 영역 1 이름[cite: 1]
    private Integer weight1Value;// 영역 1 가중치[cite: 1]

    private String domain2Name;  // 영역 2 이름[cite: 1]
    private Integer weight2Value;// 영역 2 가중치[cite: 1]

    private String domain3Name;  // 영역 3 이름[cite: 1]
    private Integer weight3Value;// 영역 3 가중치[cite: 1]

    private String domain4Name;  // 영역 4 이름[cite: 1]
    private Integer weight4Value;// 영역 4 가중치[cite: 1]

    private String domain5Name;  // 영역 5 이름[cite: 1]
    private Integer weight5Value;// 영역 5 가중치[cite: 1]
}