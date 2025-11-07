package com.quoteBoard.controller;

import com.quoteBoard.dto.*;
import com.quoteBoard.service.QuoteService;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class SystemController {

    private final BufferedReader br;
    private final QuoteService quoteService;

    public SystemController(BufferedReader br) {
        this.br = br;
        this.quoteService = new QuoteService();
    }

    public void register() throws IOException {
        System.out.print("명언 : ");
        String word = br.readLine();
        System.out.print("작가 : ");
        String author = br.readLine();

        CreateQuoteDto dto = new CreateQuoteDto(word, author);
        Long id = quoteService.create(dto);

        System.out.println(id + "번 명언이 등록되었습니다.");
    }

    public void list(int page) {
        PagedQuoteResponse data = quoteService.getList(page);
        paging(data); // 목록 페이징 출력
    }

    public void search(SearchQuoteRequest request) {
        System.out.println("----------------------");
        System.out.println("검색 타입 : " + request.type().label());
        System.out.println("검색어 : " + request.keyword());
        System.out.println("----------------------");

        PagedQuoteResponse responses = quoteService.search(request);
        paging(responses); // 검색 결과 페이징 출력
    }

    public void delete(Long id) {
        if(id == -1L) System.out.println("유효하지 않은 템플릿입니다.");
        try {
            quoteService.delete(id);
            System.out.println(id + "번 명언이 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println(id + "번 명언은 존재하지 않습니다.");
        }
    }

    public void update(Long id) throws IOException {
        if(id == -1L) System.out.println("유효하지 않은 템플릿입니다.");
        QuoteResponse oldQuote = quoteService.get(id);
        if(oldQuote.id() == -1) {
            System.out.println(id + "번 명언은 존재하지 않습니다.");
            return;
        }
        System.out.println("명언(기존) : " + oldQuote.word());
        System.out.print("명언 : ");
        String word = br.readLine();
        System.out.println("작가(기존) : " + oldQuote.author());
        System.out.print("작가 : ");
        String author = br.readLine();

        quoteService.update(new UpdateQuoteDto(id, word, author)); // 업데이트 수행
    }

    public void build() {
        quoteService.build();
        System.out.println("data.json 파일의 내용이 갱신되었습니다.");
    }

    private void paging(PagedQuoteResponse response) {

        int maxPage = response.maxPage();
        int page = response.currentPage();
        List<QuoteResponse> quotes = response.quotes();

        System.out.println("번호 / 작가 / 명언");
        System.out.println("----------------------");
        for(QuoteResponse quote : quotes) {
            System.out.println(quote.id() + " / " + quote.author() + " / " + quote.word());
        }
        System.out.println("----------------------");

        // 페이지 인디케이터 출력
        StringBuilder sb = new StringBuilder();
        sb.append("페이지 : ");
        for(int i = 1; i <= maxPage; i++) {
            if(i == page) {
                sb.append("[").append(i).append("]");
            } else {
                sb.append(i);
            }
            sb.append(" / ");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        System.out.println(sb.toString());
    }
}
