package com.quoteBoard.dto;

/**
 * 수정할 명언 데이터입니다.
 * @param id 삭제할 명언 아이디
 * @param word 명언
 * @param author 작가
 */
public record UpdateQuoteDto(Long id, String word, String author) {
}
