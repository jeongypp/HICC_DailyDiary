package com.hicc.dailydiary.domain.setting.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class SettingResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CreateResponse {
        @JsonProperty("domain_id")
        private Long domainId;

        @JsonProperty("weight_id")
        private Long weightId;
    }
}