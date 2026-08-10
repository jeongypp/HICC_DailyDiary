package com.hicc.dailydiary.infrastructure.ai;

import com.hicc.dailydiary.domain.ai.service.AiService;
import com.hicc.dailydiary.global.exception.CustomException;
import com.hicc.dailydiary.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class GeminiApiClient implements AiService {

    @Value("${gemini.api.key:default_key}")
    private String apiKey;

    // 말씀하신 무료 모델(gemini-3.1-flash-lite) URL 그대로 유지했습니다.
    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite:generateContent}")
    private String apiUrl;

    private static final int MAX_RETRIES = 3; // 서버 오류 시 재시도 횟수(API 토큰 과다사용 방지)

    private final RestTemplate restTemplate;

    public GeminiApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getAiFeedback(List<String> domainNames, List<Integer> rawScores, List<Integer> weights, String memo) {

        // 1. API Key 유효성 사전 체크
        if ("default_key".equals(apiKey) || apiKey == null || apiKey.isBlank()) {
            System.err.println("[GeminiApiClient] ⚠️ GEMINI_API_KEY 환경변수 설정 오류입니다.");
            return "AI 피드백을 생성하려면 Gemini API 키 설정이 필요합니다.";
        }

        // 2. 원점수(rawScores)와 가중치(weights)를 곱해서 가중 점수 계산
        List<Integer> weightedScores = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            weightedScores.add(rawScores.get(i) * weights.get(i));
        }

        // 3. 프롬프트 구성: 영역별 가중 점수와 추가 메모를 조합
        StringBuilder sb = new StringBuilder();
        sb.append("다음 일기 내용(5대 감정 영역명, 영역별 일일 감정 점수, 사용자 추가 메모(NULLABLE: '추가메모 없음'시 감정 영역별 점수에 집중한 답변 생성))을 읽고, 상담사이자 친구처럼 다정하게 공감하고 위로해주는 ~해요체의 짧은 피드백을 한국어로 2~3문장으로 작성해 줘:\n\n");
        sb.append("[오늘의 영역별 평가 점수]\n");
        for (int i = 0; i < 5; i++) {
            sb.append("- ").append(domainNames.get(i)).append(": ").append(weightedScores.get(i)).append("점 (범위: -100 ~ 100점)\n");
        }
        sb.append("\n[추가 메모(일기)]\n");
        sb.append(memo != null && !memo.isBlank() ? memo : "추가메모 없음");

        String prompt = sb.toString();

        // 4. Gemini API 요청 바디 구성
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String url = apiUrl + "?key=" + apiKey;

        // 5. 429 에러 시 자동 재시도 (최대 3회, 간격 점점 증가)
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                System.out.println("[GeminiApiClient] Gemini API 호출 중... (시도 " + attempt + "/" + MAX_RETRIES + ")");
                ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
                System.out.println("[GeminiApiClient] ✅ Gemini API 응답 수신 (HTTP " + response.getStatusCode() + ")");

                // 6. 응답 파싱
                Map<String, Object> body = response.getBody();
                if (body != null && body.containsKey("candidates")) {
                    List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
                    if (!candidates.isEmpty()) {
                        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                        return (String) parts.get(0).get("text");
                    }
                }

                System.err.println("[GeminiApiClient] ⚠️ 예기치 못한 응답 구조: " + body);
                throw new RuntimeException("Invalid response structure");

            } catch (Exception e) {
                System.err.println("[GeminiApiClient] ❌ Gemini API 호출 실패 (시도 " + attempt + "): " + e.getMessage());

                // 마지막 시도에만 최종 에러 반환
                if (attempt == MAX_RETRIES) {
                    System.err.println("[GeminiApiClient] ❌ " + MAX_RETRIES + "회 재시도 후에도 실패 지속.");
                    throw new CustomException(ErrorCode.AI_SERVER_ERROR);
                }

                // 다음 시도 전 1초 대기 (API 서버 과부하 방지)
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        throw new CustomException(ErrorCode.AI_SERVER_ERROR);
    }
}