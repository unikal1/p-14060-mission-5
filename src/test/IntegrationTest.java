import com.quoteBoard.App;
import com.quoteBoard.dao.QuoteRepository;
import com.quoteBoard.dao.RDBQuoteRepositoryImpl;
import com.quoteBoard.entity.Quote;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class IntegrationTest {
    private ByteArrayOutputStream out;
    private PrintStream originalOut;

    @BeforeEach
    void before() {
        originalOut = System.out;
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void after() throws Exception {
        System.setOut(originalOut);
        out.close();
    }

    @Test
    @DisplayName("등록 테스트")
    void createTest() {
        //given
        String cmd = """
                등록
                나의 죽음을 적들에게 알리지 말라!
                이순신
                종료
                """;

        byte[] bytes = cmd.getBytes();

        App app = new App(new ByteArrayInputStream(bytes));

        //when
        assertDoesNotThrow(() -> app.run(), "app.run() 실행 중 예외 발생");

        //then
        String output = out.toString();
        Assertions.assertTrue(output.contains("명언 : "));
        Assertions.assertTrue(output.contains("작가 : "));
        Assertions.assertTrue(output.contains("명언이 등록되었습니다."));

    }

    @Test
    @DisplayName("삭제 테스트")
    void deleteTest() {
        //given
        QuoteRepository repository = new RDBQuoteRepositoryImpl();
        Long id = repository.create(new Quote("나의 죽음을 적들에게 알리지 말라!", "이순신")).getId();

        String cmd = String.format("""
                삭제?id=%d
                종료
                """, id);

        byte[] bytes = cmd.getBytes();

        //when

        App app = new App(new ByteArrayInputStream(bytes));

        //then
        assertDoesNotThrow(() -> app.run(), "app.run() 실행 중 예외 발생");

        String output = out.toString();
        Assertions.assertTrue(output.contains("명언이 삭제되었습니다."));

    }

    @Test
    @DisplayName("삭제-실패 테스트")
    void deleteFailTest() throws IOException {
        //given
        String cmd = """
                삭제?id=9999
                종료
                """;
        byte[] bytes = cmd.getBytes();
        App app = new App(new ByteArrayInputStream(bytes));

        //when
        assertDoesNotThrow(() -> app.run(), "app.run() 실행 중 예외 발생");

        //then
        String output = out.toString();
        Assertions.assertTrue(output.contains("명언은 존재하지 않습니다."));
    }

    @Test
    @DisplayName("리스트 테스트")
    void getListTest() throws IOException {
        //given
        File dir = new File("db/wiseSaying");

        //저장된 파일 일괄 삭제
        File[] files = dir.listFiles();
        if(files != null) {
            for(File file : files) {
                file.delete();
            }
        }
        Path path = Path.of("db", "wiseSaying");
        Files.createDirectories(path); //디렉토리 생성

        //테스트 데이터 생성 및 삽입
        List<Quote> quotes = List.of(
                new Quote( "content1", "author1"),
                new Quote("content2", "author2"),
                new Quote("content3", "author3"),
                new Quote("content4", "author4"),
                new Quote("content5", "author5")
        );

        QuoteRepository repository = new RDBQuoteRepositoryImpl();
        List<Long> ids = quotes.stream().map(repository::create).map(Quote::getId).toList();



        String cmd = """
                목록
                종료
                """;
        byte[] bytes = cmd.getBytes();
        App app = new App(new ByteArrayInputStream(bytes));

        //when
        assertDoesNotThrow(() -> app.run(), "app.run() 실행 중 예외 발생");

        //then
        String output = out.toString();
        Assertions.assertTrue(output.contains("번호 / 작가 / 명언"));
        Assertions.assertTrue(output.contains("content2"));
        Assertions.assertTrue(output.contains("content4"));
        Assertions.assertTrue(output.contains("author5"));
        Assertions.assertTrue(output.contains("author2"));

        ids.forEach(repository::delete);
    }

    @Test
    @DisplayName("수정 테스트")
    void updateTest() throws IOException {
        QuoteRepository repository = new RDBQuoteRepositoryImpl();
        Long id = repository.create(new Quote("나의 죽음을 적들에게 알리지 말라!", "이순신")).getId();

        String cmd = String.format("""
                수정?id=%d
                수정된 대사
                수정된 작가
                종료""", id);


        byte[] bytes = cmd.getBytes();

        //when
        App app = new App(new ByteArrayInputStream(bytes));

        assertDoesNotThrow(() -> app.run(), "app.run() 실행 중 예외 발생");

        //then
        String output = out.toString();
        Assertions.assertTrue(output.contains("명언(기존) : "));
        Assertions.assertTrue(output.contains("작가(기존) : "));
        Assertions.assertTrue(output.contains("명언 : "));
        Assertions.assertTrue(output.contains("작가 : "));

        repository.delete(id);

    }
}
