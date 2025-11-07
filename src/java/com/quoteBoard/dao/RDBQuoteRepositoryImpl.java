package com.quoteBoard.dao;

import com.quoteBoard.dto.PagedQuoteDto;
import com.quoteBoard.dto.SearchQuoteRequest;
import com.quoteBoard.entity.Quote;
import com.quoteBoard.utils.FileUtils;
import com.quoteBoard.utils.JsonUtils;
import dbConfig.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class RDBQuoteRepositoryImpl implements QuoteRepository {

    private static final int PAGE_CNT = 20;

    @Override
    public Quote create(Quote quote) {
        String sql = """
                INSERT INTO quote(content, author)
                VALUES (?, ?)
                """;

        //auto-increment 를 사용하여, ps를 RETURN_GENERATED_KEYS 를 이용해 받고, ResultSet 으로 만들어진 키 값을 반환
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, quote.getQuote());
            ps.setString(2, quote.getAuthor());

            int updated = ps.executeUpdate();
            if(updated != 1) throw new SQLException("unchanged");

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if(rs.next()) {
                    long id = rs.getLong(1);
                    quote.setId(id);
                    return quote;
                } else {
                    throw new SQLException("DB insert error");
                }

            }
        } catch (SQLException ignore) {

        }
        return null;
    }

    @Override
    public List<Quote> findAll() {
        List<Quote> quotes = new ArrayList<>();

        String sql = """
                SELECT * FROM quote
                """;
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while(rs.next()) {
                quotes.add(new Quote(
                        rs.getLong("quote_id"),
                        rs.getString("content"),
                        rs.getString("author")
                ));
            }
            return quotes;
        } catch (SQLException ignore) {
        }
        return List.of();
    }


    @Override
    public Optional<Quote> find(Long id) {

        String sql = """
                SELECT * FROM quote
                WHERE quote_id = ?
                """;
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return Optional.of(new Quote(
                            rs.getLong("quote_id"),
                            rs.getString("content"),
                            rs.getString("author")
                    ));
                }
            }
        } catch (SQLException ignore) {
        }
        return Optional.empty();
    }

    @Override
    public void delete(Long id) {
        String sql = """
                DELETE FROM quote
                WHERE quote_id = ?
                """;

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            int updated = ps.executeUpdate();
            if(updated != 1) {
                throw new IllegalArgumentException("unknown id, nothing to delete");
            }
        } catch (SQLException ignore) {
        }
    }

    @Override
    public void update(Long id, String word, String author) {
        String sql = """
                UPDATE quote
                SET content = ?, author = ?
                WHERE quote_id = ?
                """;

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, word);
            ps.setString(2, author);
            ps.setLong(3, id);

            int updated = ps.executeUpdate();
            if(updated != 1) {
                throw new IllegalArgumentException("unknown id, nothing to update");
            }
        } catch (SQLException ignore) {
        }
    }

    @Override
    public void build() {
        List<String> quotes = this.findAll().stream()
                .map(JsonUtils::entityToJson)
                .toList();
        String json = JsonUtils.build(quotes);
        FileUtils.saveBuild(json);
    }

    @Override
    public PagedQuoteDto findPagedQuote(int page) {
        String sql = """
                SELECT COUNT(*) FROM quote
                """;

        int maxPage = 0;

        //페이징을 위한 레코드 개수 -> 추후 검색 시 ~페이지 범위 까지만 검색
        //todo: 만약 페이지를 쪼개서 보여주는 경우, 내부 쿼리에 limit 걸어서 count 할 것
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if(rs.next()) {
                maxPage = rs.getInt(1);
            }
        } catch (SQLException ignore) {
        }

        List<Quote> quotes = new ArrayList<>();



        sql = """
                SELECT *
                FROM quote
                ORDER BY quote_id DESC
                LIMIT ?
                """;
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int limit = PAGE_CNT * page;

            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                quotes.add(new Quote(
                        rs.getLong("quote_id"),
                        rs.getString("content"),
                        rs.getString("author")
                ));
            }
            return new PagedQuoteDto(maxPage, page, quotes);
        } catch (SQLException ignore) {
        }
        return null;
    }

    @Override
    public PagedQuoteDto search(SearchQuoteRequest.SearchType type, String keyword, int page) {
        System.out.println(keyword);

        String cntSql = String.format("""
                SELECT COUNT(*)
                FROM quote
                WHERE %s LIKE ?
                """, type.label());

        int maxPage = 0;

        //페이지 처리를 위한 레코드 수 확인
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(cntSql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                maxPage = rs.getInt(1);
            }
        } catch (SQLException ignore) {
        }

        List<Quote> quotes = new ArrayList<>();

        String sql = String.format("""
                SELECT *
                FROM quote
                WHERE %s LIKE ?
                ORDER BY quote_id DESC
                LIMIT ?
                """, type.label());

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int limit = PAGE_CNT * page;
            ps.setString(1, "%" + keyword + "%");
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                quotes.add(new Quote(
                        rs.getLong("quote_id"),
                        rs.getString("content"),
                        rs.getString("author")
                ));
            }
            return new PagedQuoteDto(maxPage, page, quotes);
        } catch (SQLException ignore) {
        }
        return null;

    }
}
