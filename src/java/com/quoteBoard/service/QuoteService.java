package com.quoteBoard.service;


import com.quoteBoard.dao.QuoteRepository;
import com.quoteBoard.dao.RDBQuoteRepositoryImpl;
import com.quoteBoard.dto.*;
import com.quoteBoard.entity.Quote;

import java.util.Optional;

public class QuoteService {

    private final QuoteRepository quoteRepository = new RDBQuoteRepositoryImpl();


    /**
     * 생성을 위한 서비스 메서드. repository 에 저장하기 위한 엔티티를 조립
     * @param dto 저장할 데이터
     * @return 만들어진 데이터의 id
     */
    public Long create(CreateQuoteDto dto) {
        Quote quote = new Quote(dto.quote(), dto.author());
        quoteRepository.create(quote);

        return quote.getId();
    }

    /**
     * 현재 저장된 명언 리스트 검색 및 반환
     * @return 저장된 명언 리스트
     */
    public PagedQuoteResponse getList(int page) {
        PagedQuoteDto dto = quoteRepository.findPagedQuote(page);

        return PagedQuoteResponse.from(dto);
    }

    /**
     * id에 대한 단건 조회 메서드
     * @param id 검색할 이이디
     * @return 조회된 데이터 만약 id == -1 이면 검색되지 않은 것
     */
    public QuoteResponse get(Long id) {
        Optional<Quote> quote = quoteRepository.find(id);
        if(quote.isEmpty()) return new QuoteResponse(-1L, null, null);
        else return new QuoteResponse(id, quote.get().getAuthor(), quote.get().getQuote());
    }

    /**
     * 삭제
     * @param id 삭제할 아이디
     */
    public void delete(Long id) {
        quoteRepository.delete(id);
    }


    /**
     * 갱신
     * @param dto 갱신 데이터
     */
    public void update(UpdateQuoteDto dto) {
        quoteRepository.update(dto.id(), dto.word(), dto.author());

    }

    /**
     * 빌드
     */
    public void build() {
        quoteRepository.build();
    }


    /**
     * 검색 / type(author / content) 와 keyword 가 주어진다.
     * @param request 검색할 파라미터
     * @return 검색된 데이터 리스트
     */
    public PagedQuoteResponse search(SearchQuoteRequest request) {
        PagedQuoteDto dto = quoteRepository.search(request.type(), request.keyword(), request.page());


        return PagedQuoteResponse.from(dto);
    }
}
