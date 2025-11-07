package com.quoteBoard.dto;

/**
 * 응답을 위한 명언 데이터
 * @param id 명언 아이디
 * @param author 명언작가
 * @param word 명언
 */
public record QuoteResponse(
        Long id, String author, String word
) { }
