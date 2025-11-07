package com.quoteBoard.dto;

/**
 * quote 생성을 위한 DTO
 * @param quote quote 문자열
 * @param author 작가
 */
public record CreateQuoteDto(String quote, String author) {
}
