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

import java.util.List;
import java.util.Map;

@Component
public class GeminiApiClient implements AiService {

    @Value("${gemini.api.key:default_key}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite:generateContent}")
    private String apiUrl;

    private static final int MAX_RETRIES = 3; // 서버 오류 시 재시도 횟수(API 토큰 과다사용 방지)

    private final RestTemplate restTemplate;

    public GeminiApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getAiFeedback(List<String> domainNames, List<Integer> rawScores, List<Integer> weights, String memo) {
        // API Key 유효성 사전 체크
        if ("default_key".equals(apiKey) || apiKey == null || apiKey.isBlank()) {
            System.err.println("[GeminiApiClient] ⚠️ GEMINI_API_KEY 환경변수가 설정 오류입니다.");
            return "AI 피드백을 생성하려면 Gemini API 키 설정이 필요합니다.";
        }

        boolean hasMemo = (memo != null && !memo.isBlank());

        // 1. AI 요청 프롬프트 구성
        StringBuilder sb = new StringBuilder();
        sb.append("사용자의 오늘 일기 데이터입니다. 'AI 해석 지시사항'을 엄격하게 준수하여 심리 상담사이자 다정한 친구처럼(본인의 역할 언급 금지) 공감하고 위로해주는 ~해요체의 짧은 피드백(2~3문장)을 작성해 주세요.\n\n");
        
        sb.append("[오늘의 감정 및 평소 가치관]\n");
        for (int i = 0; i < 5; i++) {
            sb.append("- ").append(domainNames.get(i))
              .append(": 오늘 감정점수 ").append(rawScores.get(i)).append("점 (범위: -10 ~ 10점)")
              .append(" / 평소 중요도 ").append(weights.get(i)).append("배 (최대 10배)\n");
        }
        sb.append("\n[사용자가 직접 작성한 추가 메모]\n");
        sb.append(hasMemo ? memo : "추가메모 없음");
        
        sb.append("\n\n['AI 해석 지시사항' - 반드시 지킬 것]\n");
        sb.append("1. (가장 중요) 사용자가 '추가 메모'를 작성했다면, 다른 모든 점수보다 메모의 내용을 최우선으로 분석하여 깊게 공감하고 위로해 주세요.\n");
        sb.append("2. 평소 중요도가 낮더라도, 오늘 유독 극단적인 점수(+10점 만점, 혹은 -10점 최하점)를 받은 영역에 차선으로 분석시 집중해 주세요.\n");
        sb.append("3. (절대 수칙) 피드백 문장에 '10점', '중요도', '가중치' 같은 기계적인 수치나 단어는 일절 직접 언급하지 마세요.\n");
        
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
                
                // 3. 응답 파싱 (Gemini 응답 구조: candidates[0].content.parts[0].text)
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
