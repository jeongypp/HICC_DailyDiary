package com.hicc.dailydiary.domain.setting.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SettingRequestDto {

    @JsonProperty("domain1_name") private String domain1Name;
    @JsonProperty("domain2_name") private String domain2Name;
    @JsonProperty("domain3_name") private String domain3Name;
    @JsonProperty("domain4_name") private String domain4Name;
    @JsonProperty("domain5_name") private String domain5Name;

    @JsonProperty("weight1_value") private Integer weight1Value;
    @JsonProperty("weight2_value") private Integer weight2Value;
    @JsonProperty("weight3_value") private Integer weight3Value;
    @JsonProperty("weight4_value") private Integer weight4Value;
    @JsonProperty("weight5_value") private Integer weight5Value;
}