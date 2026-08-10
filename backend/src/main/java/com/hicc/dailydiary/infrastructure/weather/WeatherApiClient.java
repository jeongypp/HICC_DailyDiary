package com.hicc.dailydiary.infrastructure.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hicc.dailydiary.domain.weather.WeatherService;
import com.hicc.dailydiary.global.exception.CustomException;
import com.hicc.dailydiary.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class WeatherApiClient implements WeatherService {

    @Value("${weather.api.key:default_key}")
    private String apiKey;

    @Value("${weather.api.url:http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public WeatherApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getWeatherCondition(int nx, int ny) {
        // 좌표값 검증 (400 VALIDATION_ERROR)
        if (nx <= 0 || ny <= 0) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR);
        }

        // 단기예보(getVilageFcst) 특성상 특정 base_time(0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300)이 필요
        // ★ 타임존 버그 수정 유지: 클라우드 환경에서도 항상 한국 시간(KST)을 기준으로 하도록 명시
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        if (now.getMinute() < 10) {
            now = now.minusHours(1);
        }
        int hour = now.getHour();
        int baseHour = ((hour + 1) / 3) * 3 - 1;
        if (baseHour < 0) {
            baseHour = 23;
            now = now.minusDays(1);
        }

        String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = String.format("%02d00", baseHour);

        // URL 및 쿼리 파라미터 구성
        URI uri = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("serviceKey", apiKey)
                .queryParam("pageNo", 1)
                // ★ 데이터 누락 버그 수정 유지: 한 번에 받아오는 데이터 개수를 10 -> 30으로 늘림
                .queryParam("numOfRows", 30)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .build(true) // 인코딩된 serviceKey
                .toUri();

        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
            String responseBody = responseEntity.getBody();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            JsonNode items = root.path("response").path("body").path("items").path("item");

            if (items.isMissingNode() || !items.isArray()) {
                return "알 수 없음";
            }

            String sky = "";
            String pty = "";

            for (JsonNode item : items) {
                String category = item.path("category").asText();
                String fcstValue = item.path("fcstValue").asText();
                if ("SKY".equals(category)) {
                    sky = fcstValue;
                } else if ("PTY".equals(category)) {
                    pty = fcstValue;
                }
            }

            // PTY (강수형태): 0(없음), 1(비), 2(비/눈), 3(눈), 4(소나기)
            if ("1".equals(pty) || "4".equals(pty)) return "비";
            if ("2".equals(pty) || "3".equals(pty)) return "눈";

            // SKY (하늘상태): 1(맑음), 3(구름많음), 4(흐림)
            if ("1".equals(sky)) return "맑음";
            if ("3".equals(sky)) return "구름많음";
            if ("4".equals(sky)) return "흐림";

            return "알 수 없음";
        } catch (Exception e) {
            // ★ 우회 코드 제거: 실제 에러 발생 시 정상적으로 502 에러를 던지도록 복구
            System.err.println("[WeatherApiClient] 기상청 API 연동 오류: " + e.getMessage());
            throw new CustomException(ErrorCode.WEATHER_API_ERROR);
        }
    }
}