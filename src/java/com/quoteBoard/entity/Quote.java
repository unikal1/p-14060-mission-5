package com.quoteBoard.entity;

public class Quote {
    private Long id;
    private String quote;
    private String author;

    public Quote(String quote, String author) {
        this.quote = quote;
        this.author = author;
    }

    public Quote(Long id, String quote, String author) {
        this.id = id;
        this.quote = quote;
        this.author = author;
    }

    public Long getId() { return id; }
    public void setId(Long id) {
        this.id = id;
    }

    public String getQuote() {
        return quote;
    }

    public String getAuthor() {
        return author;
    }

    public void update(String quote, String author) {
        this.quote = quote;
        this.author = author;
    }

}
