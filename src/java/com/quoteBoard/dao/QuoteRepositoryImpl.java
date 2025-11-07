package com.quoteBoard.dao;

import com.quoteBoard.dto.PagedQuoteDto;
import com.quoteBoard.dto.SearchQuoteRequest;
import com.quoteBoard.entity.Quote;
import com.quoteBoard.utils.FileUtils;
import com.quoteBoard.utils.JsonUtils;

import java.io.IOException;
import java.util.*;

/**
 * QuoteRepository 구현체
 */
public class QuoteRepositoryImpl implements QuoteRepository {

    private Long cnt = 1L;

    public QuoteRepositoryImpl() {
        cnt = FileUtils.getMaxNumber(); // 마지막 ID 로드
    }

    @Override
    public Quote create(Quote quote) {
        quote.setId(cnt++); // ID 할당
        String json = JsonUtils.entityToJson(quote);
        try {
            FileUtils.save(quote.getId(), json); // 저장
            FileUtils.putMaxNumber(cnt); // 마지막 ID 갱신
        } catch (IOException ignore) { }
        return quote;
    }

    @Override
    public List<Quote> findAll() {
        return FileUtils.readAll().stream().map(JsonUtils::jsonToEntity).toList(); // 전체 조회
    }

    @Override
    public Optional<Quote> find(Long id) {
        try {
            String json = FileUtils.readOne(id);
            if(json == null) return Optional.empty();
            return Optional.of(JsonUtils.jsonToEntity(json)); // 단건 조회
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(Long id) {
        FileUtils.delete(id); // 삭제
    }

    @Override
    public void update(Long id, String word, String author) {
        String json = JsonUtils.entityToJson(new Quote(id, word, author));
        try {
            FileUtils.save(id, json); // 업데이트
        } catch (IOException ignore) { }
    }

    @Override
    public void build() {
        String buildFile = JsonUtils.build(FileUtils.readAll()); // 빌드 파일 생성
        FileUtils.saveBuild(buildFile);
    }

    @Override
    public PagedQuoteDto findPagedQuote(int page) {
        return null;
    }

    @Override
    public PagedQuoteDto search(SearchQuoteRequest.SearchType type, String keyword, int page) {
        return null;
    }
}
