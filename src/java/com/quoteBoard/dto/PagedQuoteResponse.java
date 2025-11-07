package com.quoteBoard.dto;

import java.util.List;

public record PagedQuoteResponse (
        int maxPage,
        int currentPage,
        List<QuoteResponse> quotes
)
{
    public static PagedQuoteResponse from(PagedQuoteDto dto) {
        return new PagedQuoteResponse(
                dto.maxPage(),
                dto.currentPage(),
                dto.quotes().stream().map(p -> new QuoteResponse(p.getId(), p.getAuthor(), p.getQuote()))
                        .toList()
        );
    }
}
