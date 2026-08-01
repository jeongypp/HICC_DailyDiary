package com.hicc.dailydiary.domain.setting.repository;

import com.hicc.dailydiary.domain.setting.entity.Domain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DomainRepository extends JpaRepository<Domain, Long> {
}