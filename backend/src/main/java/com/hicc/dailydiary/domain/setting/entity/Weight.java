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
public class Weight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 명세서의 weight_id 역할[cite: 1]

    private Integer weight1Value; // 영역 1 가중치[cite: 1]
    private Integer weight2Value; // 영역 2 가중치[cite: 1]
    private Integer weight3Value; // 영역 3 가중치[cite: 1]
    private Integer weight4Value; // 영역 4 가중치[cite: 1]
    private Integer weight5Value; // 영역 5 가중치[cite: 1]
}