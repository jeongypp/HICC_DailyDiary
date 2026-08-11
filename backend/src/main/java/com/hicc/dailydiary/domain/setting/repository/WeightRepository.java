package com.hicc.dailydiary.domain.setting.repository;

import com.hicc.dailydiary.domain.setting.entity.Weight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeightRepository extends JpaRepository<Weight, Long> {
    // 가장 최근에 저장된(ID가 내림차순으로 가장 큰) 가중치 1개 조회
    Optional<Weight> findTopByOrderByIdDesc();
}
