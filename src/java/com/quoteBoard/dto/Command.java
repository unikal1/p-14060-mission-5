package com.quoteBoard.dto;

/**
 * 명령어 문자열을 관리
 */
public enum Command {

    TERMINATE("종료"),
    REGISTER("등록"),
    LIST("목록"),
    DELETE("삭제"),
    UPDATE("수정"),
    BUILD("빌드");

    private final String label;

    Command(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}