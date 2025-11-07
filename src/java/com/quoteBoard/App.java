package com.quoteBoard;

import com.quoteBoard.controller.SystemController;
import com.quoteBoard.dto.Command;
import com.quoteBoard.dto.SearchQuoteRequest;
import com.quoteBoard.utils.UIUtils;
import dbConfig.ConnectionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class App {

    private final BufferedReader br;
    private final SystemController controller;

    public App(InputStream in) {
        this.br = new BufferedReader(new InputStreamReader(in));
        this.controller = new SystemController(this.br);
    }

    public void run() throws IOException {


        //if table not exist, create new table
        ConnectionManager.createTable();

        //if record not exist, create 10 records
        ConnectionManager.insertExample(10);
        try {
            String sql = Files.readString(Path.of("src", "resources", "create_table.sql"));
            try (Connection conn = ConnectionManager.getConnection();
                 Statement stmt = conn.createStatement()) {
                     stmt.execute(sql);
            }
        } catch (Exception e) {
            System.out.println("sql file not found");
        }



        System.out.println(UIUtils.title());

        while(true) {
            System.out.print(UIUtils.cmdPrefix());
            String cmd = br.readLine();

            if(cmd.equals(Command.TERMINATE.label())) {   // 종료
                break;
            } else if(cmd.equals(Command.REGISTER.label())) {   // 등록
                controller.register();
            } else if(cmd.startsWith(Command.LIST.label())) {   // 목록/검색
                String value = cmd.substring(Command.LIST.label().length()).trim();
                Map<String, String> val = parseQuery(value);

                // 쿼리 파라미터 없거나(page만 있는 경우 포함) → 일반 목록
                if (!val.containsKey("keywordType") || !val.containsKey("keyword")) {
                    String rawPage = val.get("page");
                    controller.list(rawPage == null ? 1 : Integer.parseInt(rawPage));
                } else { // 검색 모드
                    String typeStr = val.get("keywordType");
                    String keyword = val.get("keyword");
                    int page = (val.get("page") == null ? 1 : Integer.parseInt(val.get("page")));
                    if (typeStr == null || keyword == null || keyword.isBlank()) continue;

                    SearchQuoteRequest.SearchType type = switch (typeStr.toLowerCase()) {
                        case "author"  -> SearchQuoteRequest.SearchType.AUTHOR;
                        case "content" -> SearchQuoteRequest.SearchType.CONTENT;
                        default -> null;
                    };

                    if (type == null) continue; // 지원하지 않는 타입

                    SearchQuoteRequest request = new SearchQuoteRequest(type, keyword, page);
                    controller.search(request);
                }
            } else if(cmd.startsWith(Command.DELETE.label())) { // 삭제
                String value = cmd.substring(Command.DELETE.label().length());
                Long id = Long.parseLong(parseQuery(value).get("id"));
                controller.delete(id);
            } else if(cmd.startsWith(Command.UPDATE.label())) { // 수정
                String value = cmd.substring(Command.UPDATE.label().length());
                Long id = Long.parseLong(parseQuery(value).get("id"));
                controller.update(id);
            } else if(cmd.equals(Command.BUILD.label())) { // 빌드
                controller.build();
            }
        }
    }

    // "?a=b&c=d" 형식의 쿼리를 Map으로 파싱 (중복 키 없음 가정)
    private Map<String, String> parseQuery(String cmd) {
        Map<String, String> map = new HashMap<>();
        int idx = cmd.indexOf('?');
        if(idx == -1|| idx == cmd.length() - 1) return Map.of();  // '?' 없음 또는 뒤에 값 없음
        String query = cmd.substring(idx + 1);
        String[] values = query.split("&");

        for(String value : values) {
            if(value.contains("=")) {
                String key = value.split("=")[0];
                String val = value.split("=")[1];
                map.put(key, val);
            }
        }
        return map;
    }
}
