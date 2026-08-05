package com.hicc.dailydiary.domain.setting.repository;

import com.hicc.dailydiary.domain.setting.entity.Weight;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WeightRepository extends JpaRepository<Weight, Long> {

    // 에러나던 메서드 추가
    Optional<Weight> findTopByOrderByIdDesc();
}