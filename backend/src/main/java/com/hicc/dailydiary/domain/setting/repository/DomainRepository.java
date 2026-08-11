package com.hicc.dailydiary.domain.setting.repository;

import com.hicc.dailydiary.domain.setting.entity.Domain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DomainRepository extends JpaRepository<Domain, Long> {
    // 가장 최근에 저장된(ID가 내림차순으로 가장 큰) 도메인 1개 조회
    Optional<Domain> findTopByOrderByIdDesc();
}