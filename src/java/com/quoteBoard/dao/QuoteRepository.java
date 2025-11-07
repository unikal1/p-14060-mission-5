package com.quoteBoard.dao;


import com.quoteBoard.dto.PagedQuoteDto;
import com.quoteBoard.dto.SearchQuoteRequest;
import com.quoteBoard.entity.Quote;

import java.util.List;
import java.util.Optional;

public interface QuoteRepository {

    /**
     * 명언 생성 메서드
     * @param quote 생성하고자 하는 명언에 대한 데이터
     */
    Quote create(Quote quote);

    /**
     * 저장된 모든 명언 데이터를 반환합니다.
     * @return 저장된 명언 리스트 반환
     */
    List<Quote> findAll();

    /**
     * id 에 기반하여 quote 를 검색합니다.
     * @param id 검색할 아이디
     * @return 검색된 quote 에 대한 optional
     */
    Optional<Quote> find(Long id);

    /**
     * 저장된 명언 데이터를 삭제합니다.
     * @param id 삭제할 아이디
     * @throws IllegalArgumentException 삭제할 명언이 존재하지 않을 시
     */
    void delete(Long id);

    /**
     * 저장된 명언 데이터를 수정합니다.
     * @param id 수정할 명언 아이디
     * @param word 수정할 명언
     * @param author 수정할 명언의 작가
     * @throws IllegalArgumentException 수정할 명언이 존재하지 않을 시
     */
    void update(Long id, String word, String author);

    /**
     * 저장된 파일들을 합치여 하나의 파일로 구성한다.
     */
    void build();

    PagedQuoteDto findPagedQuote(int page);

    PagedQuoteDto search(SearchQuoteRequest.SearchType type, String keyword, int page);

}
