package com.hicc.dailydiary.domain.setting.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Domain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 명세서의 domain_id 역할

    private String domain1Name; // 영역 1 이름[cite: 1]
    private String domain2Name; // 영역 2 이름[cite: 1]
    private String domain3Name; // 영역 3 이름[cite: 1]
    private String domain4Name; // 영역 4 이름[cite: 1]
    private String domain5Name; // 영역 5 이름[cite: 1]
}