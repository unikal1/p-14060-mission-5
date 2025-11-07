package com.quoteBoard.utils;

import com.quoteBoard.entity.Quote;

import java.util.List;

/**
 * JSON 직렬화/역직렬화를 아주 단순한 포맷으로 수행하는 유틸리티.
 * 외부 라이브러리 없이 문자열 연산만 사용한다.
 * 간단한 학습/과제용으로 적합하며, 이스케이프 처리 등은 최소한으로만 수행한다.
 */
public class JsonUtils {

    /**
     * Quote 엔티티를 고정된 JSON 포맷의 문자열로 직렬화한다.
     * 내부 동작:
     * 1) String.format 템플릿에 id, content, author 값을 순서대로 대입한다.
     * 2) 들여쓰기/개행을 포함한 멀티라인 문자열을 그대로 반환한다.
     * 주의: content/author에 따옴표, 개행 등 특수문자가 포함되면 별도 이스케이프를 하지 않는다.
     *
     * @param quote 직렬화할 엔티티
     * @return JSON 형태의 문자열
     */
    public static String entityToJson(Quote quote) {
        // 템플릿 문자열에 필드값을 그대로 삽입
        return String.format("""
                {
                    "id": %d,
                    "content": "%s",
                    "author": "%s"
                }
            """, quote.getId(), quote.getQuote(), quote.getAuthor());
    }

    /**
     * 여러 JSON 조각을 하나의 JSON 배열 문자열로 합친다.
     * 내부 동작:
     * 1) 시작 대괄호와 개행을 추가한다.
     * 2) 각 요소를 trim한 뒤, 들여쓰기와 함께 추가하고 뒤에 쉼표를 붙인다.
     * 3) 마지막 요소 뒤에 붙은 여분의 쉼표를 찾아 제거한다.
     * 4) 닫는 대괄호를 붙여 마무리한다.
     *
     * @param jsons 요소별 JSON 문자열 리스트
     * @return JSON 배열 문자열
     */
    public static String build(List<String> jsons) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (String json : jsons) {
            // 각 요소를 한 줄 들여쓰기 후, 쉼표를 붙여 나열
            json = json.trim();
            sb.append("\t").append(json).append(",").append("\n");
        }
        // 마지막 쉼표 제거
        if (!jsons.isEmpty()) {
            int lastCommaIdx = sb.lastIndexOf(",");
            if (lastCommaIdx != -1) sb.deleteCharAt(lastCommaIdx);
        }
        sb.append("\n").append("]");
        return sb.toString();
    }

    /**
     * 단일 객체 JSON 문자열을 Quote 엔티티로 역직렬화한다.
     * 내부 동작:
     * 1) lines()로 줄 단위 분해 후 순회한다.
     * 2) 각 줄을 trim한 뒤, 키에 해당하는 접두어로 시작하는지 확인한다.
     * 3) "id": 라인은 콜론 기준 분리 후 숫자만 남기도록 쉼표 제거, 공백 제거 → long 파싱.
     * 4) "content", "author" 라인은 콜론 기준 분리 후 쉼표/따옴표를 제거해 문자열 값만 추출한다.
     * 5) 추출된 값들로 Quote를 생성해 반환한다.
     * 주의: 포맷이 어긋나거나 중첩 객체, 이스케이프된 문자 등은 처리하지 않는다.
     *
     * @param json 단일 객체 JSON 문자열
     * @return 역직렬화된 Quote 엔티티
     */
    public static Quote jsonToEntity(String json) {
        List<String> lines = json.lines().toList();
        long id = -1L; String content = null; String author = null;

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("\"id\"")) {
                // "id": 123, 형태 → 콜론 기준 분리 후 숫자 부분만 추출
                String value = line.split(":")[1].replace(",", "").trim();
                id = Long.parseLong(value);
            } else if (line.startsWith("\"content\"")) {
                // "content": "문자열", 형태 → 콜론 기준 분리 후 쉼표/따옴표 제거
                content = line.split(":")[1]
                        .replace(",", "")
                        .replace("\"", "")
                        .trim();
            } else if (line.startsWith("\"author\"")) {
                // "author": "문자열", 형태 → 콜론 기준 분리 후 쉼표/따옴표 제거
                author = line.split(":")[1]
                        .replace(",", "")
                        .replace("\"", "")
                        .trim();
            }
        }
        return new Quote(id, content, author);
    }
}