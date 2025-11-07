package com.quoteBoard.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 파일 시스템을 이용해 명언 데이터를 저장/조회/삭제/빌드하는 유틸리티.
 * <p>
 * 기본 디렉터리 구조:
 * <pre>
 * db/
 *  └─ wiseSaying/
 *      ├─ {id}.json      // 단건 데이터
 *      ├─ data.json      // build 결과(배치/목록 파일)
 *      └─ lastId.txt     // 다음에 부여할 ID 관리
 * </pre>
 */
public class FileUtils {

    /**
     * 주어진 ID에 대응하는 JSON 파일 경로를 반환한다.
     * @param id 엔티티 ID
     * @return db/wiseSaying/{id}.json
     */
    private static Path getPath(Long id) {
        return Path.of("db", "wiseSaying", id + ".json");
    }

    /**
     * 명언 저장 디렉터리 경로를 반환한다.
     * @return db/wiseSaying
     */
    private static Path getPath() {
        return Path.of("db", "wiseSaying");
    }

    /**
     * 다음에 부여할 ID(lastId)를 읽어 반환한다.
     * 읽기에 실패하면 1L을 기록하고 1L을 반환한다.
     * @return 마지막 ID(다음에 부여할 값)
     */
    public static Long getMaxNumber() {
        Path path = Path.of("db", "wiseSaying", "lastId.txt");
        try {
            return Long.parseLong(Files.readString(path));
        } catch (Exception e) {
            // 초기화: 파일이 없거나 파싱 실패 시 1로 세팅
            putMaxNumber(1L);
        }
        return 1L;
    }

    /**
     * 다음에 부여할 ID(lastId)를 기록한다.
     * @param number 기록할 값
     */
    public static void putMaxNumber(Long number) {
        Path path = Path.of("db", "wiseSaying", "lastId.txt");
        try {
            Files.writeString(path, number.toString());
        } catch (Exception ignore) { }
    }

    /**
     * 단건 JSON 파일을 저장한다. 경로가 없으면 디렉터리를 생성한다.
     * @param id 파일명에 사용할 ID
     * @param content JSON 문자열
     * @throws IOException 쓰기 오류
     */
    public static void save(Long id, String content) throws IOException {
        Path path = getPath(id);
        try {
            Files.writeString(path, content);
        } catch (NoSuchFileException e) {
            // 상위 디렉터리 없으면 생성 후 재시도
            Files.createDirectories(path.getParent());
            Files.writeString(path, content);
        }
    }

    /**
     * 빌드 결과(data.json)를 저장한다.
     * @param content 집계/빌드된 JSON 문자열
     */
    public static void saveBuild(String content) {
        Path path = Path.of("db", "wiseSaying", "data.json");
        try {
            Files.writeString(path, content);
        } catch (Exception ignore) {
        }
    }

    /**
     * 단건 파일({id}.json)을 삭제한다.
     * 존재하지 않으면 {@link IllegalArgumentException}을 던진다.
     * @param id 삭제할 ID
     * @throws IllegalArgumentException 파일이 존재하지 않는 경우
     */
    public static void delete(Long id) {
        try {
            Files.delete(getPath(id));
        } catch (NoSuchFileException e) {
            // 존재하지 않는 ID
            throw new IllegalArgumentException();
        } catch (IOException ignore) {}
    }

    /**
     * 단건 파일({id}.json)을 읽어 문자열로 반환한다.
     *  파일이 없으면 {@link IllegalArgumentException}을 던진다.</li>
     *  기타 I/O 오류는 null 반환.</li>
     * @param id 조회할 ID
     * @return JSON 문자열, 실패 시 null
     * @throws IllegalArgumentException 파일이 존재하지 않는 경우
     */
    public static String readOne(Long id) {
        Path path = getPath(id);
        try {
            return Files.readString(path);
        } catch (NoSuchFileException e) {
            throw new IllegalArgumentException();
        } catch (IOException ignore) {
            return null;
        }
    }

    /**
     * 디렉터리 내 모든 단건 JSON({id}.json) 파일을 읽어 리스트로 반환한다.
     * data.json은 제외한다.
     * 디렉터리가 없거나 파일이 없으면 빈 리스트 반환.
     * @return JSON 문자열 리스트
     */
    public static List<String> readAll() {
        File dir = new File("db/wiseSaying");
        List<String> result = new ArrayList<>();
        if(!dir.exists()) return result;

        File[] files = dir.listFiles();
        if(files == null) return result;

        for(File file : files) {
            // 단건 파일만 대상: *.json && data.json 제외
            if(file.isFile() && file.getName().endsWith(".json") && !file.getName().equals("data.json")) {
                try {
                    String content = Files.readString(file.toPath());
                    result.add(content);
                } catch (IOException ignore) { }
            }
        }
        return result;
    }
}
