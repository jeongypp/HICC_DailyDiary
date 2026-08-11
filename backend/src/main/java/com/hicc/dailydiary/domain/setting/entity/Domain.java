package com.hicc.dailydiary.domain.setting.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder; // 추가됨
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Domain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 명세서의 domain_id 역할

    private String domain1Name; // 영역 1 이름
    private String domain2Name; // 영역 2 이름
    private String domain3Name; // 영역 3 이름
    private String domain4Name; // 영역 4 이름
    private String domain5Name; // 영역 5 이름

    // id는 DB에서 자동 생성되므로 제외하고, 나머지 필드만 받는 생성자에 @Builder 적용
    @Builder
    public Domain(String domain1Name, String domain2Name, String domain3Name, String domain4Name, String domain5Name) {
        this.domain1Name = domain1Name;
        this.domain2Name = domain2Name;
        this.domain3Name = domain3Name;
        this.domain4Name = domain4Name;
        this.domain5Name = domain5Name;
    }
}