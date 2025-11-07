package com.quoteBoard.dto;

import com.quoteBoard.entity.Quote;

import java.util.List;

public record PagedQuoteDto(
        int maxPage,
        int currentPage,
        List<Quote> quotes
) {
}
