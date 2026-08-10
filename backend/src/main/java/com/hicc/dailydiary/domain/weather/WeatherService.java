package com.hicc.dailydiary.domain.weather;

public interface WeatherService {
    /**
     * 특정 좌표(x, y)의 날씨 정보를 조회하여 간단한 텍스트(맑음, 구름많음, 흐림, 비, 눈 등)로 반환.
     * @param nx x좌표 (MVP: 고정값 사용)
     * @param ny y좌표 (MVP: 고정값 사용)
     * @return 날씨 상태 텍스트
     */
    String getWeatherCondition(int nx, int ny);
}
