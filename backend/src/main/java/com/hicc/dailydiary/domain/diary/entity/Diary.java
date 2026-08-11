package com.hicc.dailydiary.domain.diary.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long id;

    @Column(name = "diary_date", nullable = false)
    private String diaryDate;

    @Column(name = "domain_id", nullable = false)
    private Integer domainId;

    @Column(name = "weight_id", nullable = false)
    private Integer weightId;

    private Integer score1;
    private Integer score2;
    private Integer score3;
    private Integer score4;
    private Integer score5;

    @Column(name = "weather")
    private String weather;
    
    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    @Column(name = "ai_reply", columnDefinition = "TEXT")
    private String aiReply;

    @Column(name = "last_updated")
    private java.time.LocalDateTime lastUpdated;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false; // 소프트 딜리트 여부

    @Builder
    public Diary(String diaryDate, Integer domainId, Integer weightId, Integer score1, Integer score2, Integer score3, Integer score4, Integer score5, String weather, String memo, String aiReply) {
        this.diaryDate = diaryDate;
        this.domainId = domainId;
        this.weightId = weightId;
        this.score1 = score1;
        this.score2 = score2;
        this.score3 = score3;
        this.score4 = score4;
        this.score5 = score5;
        this.weather = weather;
        this.memo = memo;
        this.aiReply = aiReply;
    }

    // 일기 수정 로직
    public void update(Integer score1, Integer score2, Integer score3, Integer score4, Integer score5, String memo) {
        this.score1 = score1;
        this.score2 = score2;
        this.score3 = score3;
        this.score4 = score4;
        this.score5 = score5;
        this.memo = memo;
        this.lastUpdated = java.time.LocalDateTime.now();
    }

    // AI 피드백 업데이트 로직(일기 수정 시)
    public void updateAiReply(String aiReply) {
        this.aiReply = aiReply;
    }

    // 소프트 딜리트 로직
    public void delete() {
        this.isDeleted = true;
    }
}