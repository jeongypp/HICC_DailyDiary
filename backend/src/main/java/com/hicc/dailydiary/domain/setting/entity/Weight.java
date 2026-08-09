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
public class Weight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 명세서의 weight_id 역할

    private Integer weight1Value; // 영역 1 가중치
    private Integer weight2Value; // 영역 2 가중치
    private Integer weight3Value; // 영역 3 가중치
    private Integer weight4Value; // 영역 4 가중치
    private Integer weight5Value; // 영역 5 가중치

    // id는 DB에서 자동 생성되므로 제외하고, 나머지 필드만 받는 생성자에 @Builder 적용
    @Builder
    public Weight(Integer weight1Value, Integer weight2Value, Integer weight3Value, Integer weight4Value, Integer weight5Value) {
        this.weight1Value = weight1Value;
        this.weight2Value = weight2Value;
        this.weight3Value = weight3Value;
        this.weight4Value = weight4Value;
        this.weight5Value = weight5Value;
    }
}