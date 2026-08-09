package com.hicc.dailydiary.infrastructure.ai;

import com.hicc.dailydiary.domain.ai.AiService;
import com.hicc.dailydiary.global.exception.CustomException;
import com.hicc.dailydiary.global.exception.ErrorCode;
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
    public String getAiFeedback(List<String> domains, List<Integer> scores, String memo) {
        // API Key 유효성 사전 체크
        if ("default_key".equals(apiKey) || apiKey == null || apiKey.isBlank()) {
            System.err.println("[GeminiApiClient] ⚠️ GEMINI_API_KEY 환경변수가 설정되지 않았습니다.");
            return "AI 피드백을 생성하려면 Gemini API 키 설정이 필요합니다.";
        }

        // 1. 프롬프트 구성: 영역별 점수와 일기 메모를 조합
        StringBuilder sb = new StringBuilder();
        sb.append("다음 일기 내용을 읽고, 친구처럼 다정하게 공감하고 위로해주는 짧은 피드백을 한국어로 2~3문장으로 작성해 줘:\n\n");
        sb.append("[오늘의 영역별 평가 점수]\n");
        for (int i = 0; i < 5; i++) {
            sb.append("- ").append(domains.get(i)).append(": ").append(scores.get(i)).append("점 (최대 10점)\n");
        }
        sb.append("\n[추가 메모(일기)]\n");
        sb.append(memo != null && !memo.isBlank() ? memo : "특별한 메모는 남기지 않았어요.");
        
        String prompt = sb.toString();

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
                throw new CustomException(ErrorCode.AI_SERVER_ERROR);

                /* 
                 * [백엔드 개발 단계의 에러 핸들링]
                 * 기존 429, 404 등의 세부 에러 핸들링은 명세서에 정의된 
                 * AI_SERVER_ERROR (500) 로 통합하기 위해 주석 처리합니다.
                 *
            } catch (HttpClientErrorException.TooManyRequests e) {
                long waitSeconds = attempt * 10L;
                System.err.println("[GeminiApiClient] ⏳ 429 할당량 초과 (시도 " + attempt + ") → " + waitSeconds + "초 후 재시도...");
                try {
                    Thread.sleep(waitSeconds * 1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new CustomException(ErrorCode.AI_SERVER_ERROR);
                }
            } catch (HttpClientErrorException e) {
                System.err.println("[GeminiApiClient] ❌ HTTP 에러: " + e.getStatusCode() + " - " + e.getStatusText());
                System.err.println("[GeminiApiClient] ❌ 구글 API 상세 에러 내용: " + e.getResponseBodyAsString());
                throw new CustomException(ErrorCode.AI_SERVER_ERROR);
                 */
            } catch (Exception e) {
                System.err.println("[GeminiApiClient] ❌ Gemini API 호출 실패: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                throw new CustomException(ErrorCode.AI_SERVER_ERROR);
            }
        }

        // 3번 재시도 후에도 실패
        System.err.println("[GeminiApiClient] ❌ " + MAX_RETRIES + "회 재시도 후에도 실패 지속.");
        throw new CustomException(ErrorCode.AI_SERVER_ERROR);
    }
}
