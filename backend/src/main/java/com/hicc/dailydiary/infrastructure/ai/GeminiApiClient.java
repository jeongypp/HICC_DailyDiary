package com.hicc.dailydiary.infrastructure.ai;

import com.hicc.dailydiary.domain.ai.AiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@Component
public class GeminiApiClient implements AiService {

    @Value("${gemini.api.key:default_key}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite:generateContent}")
    private String apiUrl;

    private static final int MAX_RETRIES = 3;

    private final RestTemplate restTemplate;

    public GeminiApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getAiFeedback(String diaryContent) {
        // API Key 유효성 사전 체크
        if ("default_key".equals(apiKey) || apiKey == null || apiKey.isBlank()) {
            System.err.println("[GeminiApiClient] ⚠️ GEMINI_API_KEY 환경변수가 설정되지 않았습니다.");
            return "AI 피드백을 생성하려면 Gemini API 키 설정이 필요합니다.";
        }

        // 1. 프롬프트 구성: 일기 내용을 바탕으로 공감과 위로의 피드백을 작성하도록 지시
        String prompt = "다음 일기를 읽고, 친구처럼 다정하게 공감하고 위로해주는 짧은 피드백을 한국어로 2~3문" +
                "장으로 작성해 줘:\n" + diaryContent;

        // 2. Gemini API 요청 바디 구성
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

        // 429 에러 시 자동 재시도 (최대 3회, 간격 점점 증가)
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                System.out.println("[GeminiApiClient] Gemini API 호출 중... (시도 " + attempt + "/" + MAX_RETRIES + ")");
                ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
                System.out.println("[GeminiApiClient] ✅ Gemini API 응답 수신 (HTTP " + response.getStatusCode() + ")");
                
                // 4. 응답 파싱 (Gemini 응답 구조: candidates[0].content.parts[0].text)
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
                return "오늘 하루도 고생했어요. 푹 쉬세요!";

            } catch (HttpClientErrorException.TooManyRequests e) {
                // 429 에러: 토큰 할당량 초과 → 대기 후 재시도
                long waitSeconds = attempt * 10L; // 10초, 20초, 30초 순으로 대기
                System.err.println("[GeminiApiClient] ⏳ 429 할당량 초과 (시도 " + attempt + ") → " + waitSeconds + "초 후 재시도...");
                try {
                    Thread.sleep(waitSeconds * 1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return "AI 피드백 생성이 중단되었습니다.";
                }
            } catch (HttpClientErrorException e) {
                // 401, 403, 404 등 기타 HTTP 에러 상세 출력
                System.err.println("[GeminiApiClient] ❌ HTTP 에러: " + e.getStatusCode() + " - " + e.getStatusText());
                System.err.println("[GeminiApiClient] ❌ 구글 API 상세 에러 내용: " + e.getResponseBodyAsString());
                return "AI 서비스 연결에 문제가 있습니다. 잠시 후 다시 시도해주세요.";
            } catch (Exception e) {
                System.err.println("[GeminiApiClient] ❌ Gemini API 호출 실패: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                return "AI 서비스에 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요.";
            }
        }


        // 3번 재시도 후에도 실패
        System.err.println("[GeminiApiClient] ❌ " + MAX_RETRIES + "회 재시도 후에도 429 에러 지속.");
        return "AI 서버가 혼잡합니다. 잠시 후 다시 시도해주세요.";
    }
}
