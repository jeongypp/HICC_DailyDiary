package com.hicc.dailydiary.domain.diary.repository;

import com.hicc.dailydiary.domain.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    // 특정 날짜에 삭제되지 않은 일기가 이미 존재하는지 확인
    boolean existsByDiaryDateAndIsDeletedFalse(String diaryDate);

    // ID로 조회 시 삭제되지 않은 일기만 가져옴
    Optional<Diary> findByIdAndIsDeletedFalse(Long id);

    // 월별 일기장 조회: 특정 연월(YYYY-MM)로 시작하는 삭제되지 않은 일기 검색
    List<Diary> findByDiaryDateStartingWithAndIsDeletedFalse(String yearMonth);

    // 일기 검색 (다중 조건): 날짜 범위와 키워드(메모 내용)로 필터링
    @Query("SELECT d FROM Diary d WHERE d.isDeleted = false " +
            "AND (:startDate IS NULL OR d.diaryDate >= :startDate) " +
            "AND (:endDate IS NULL OR d.diaryDate <= :endDate) " +
            "AND (:keyword IS NULL OR d.memo LIKE %:keyword%)")
    List<Diary> searchDiaries(@Param("startDate") String startDate,
                              @Param("endDate") String endDate,
                              @Param("keyword") String keyword);
}