package com.quoteBoard.dto;


public record SearchQuoteRequest(SearchType type, String keyword, int page) {
    public enum SearchType {
        AUTHOR("author"), CONTENT("content");

        private final String label;

        SearchType(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }
}
