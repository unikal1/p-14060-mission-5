package dbConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public class ConnectionManager {

    private static final String SERVER_HOST = "localhost";
    private static final String SERVER_PORT = "3306";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static final String DATABASE = "quoteDB";

    //드라이버를 jvm에 붙이기
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL Driver not found", e);
        }
    }

    private static String makeUrl() {
        return "jdbc:mysql://" + SERVER_HOST + ":" + SERVER_PORT + "/" + DATABASE;
    }

    //Connection 에 대한 정적 팩토리 메서드
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(makeUrl(), USER, PASSWORD);

    }

    /**
     * 주어진 connection 객체를 닫습니다. 다만, connection 자체를 try - with -resource 를 이용하여 관리하는 것을 권장합니다.
     * @param conn 닫을 connection
     */
    public static void close(Connection conn) {
        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException ignore) {}
        }
    }

    /**
     * 테이블을 생성합니다. src/resources/create_table.sql 에 명시된 단일 sql 쿼리문을 실행합니다.
     */
    public static void createTable() {
        try {
            String sql = Files.readString(Path.of("src", "resources", "create_table.sql"));
            try (Connection conn = ConnectionManager.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            }
        } catch (Exception e) {
            System.out.println("sql file not found");
        }
    }

    /**
     * 예제용 레코드를 삽입합니다.
     * @param n 삽입할 레코드 개수
     */
    public static void insertExample(int n) {

        String cntSql = """
                SELECT COUNT(*) FROM quote;
                """;
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(cntSql);
             ResultSet rs = ps.executeQuery()) {

            if(rs.next()) {
                return;
            }
        } catch (SQLException ignore) {
        }

        StringBuilder sb = new StringBuilder("""
                INSERT INTO quote(content, author) values
                """);
        for(int i = 1; i <= n; i++) {
            sb.append(String.format("('명언 %d', '작자미상 %d'),\n", i, i));
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);


        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeQuery(sb.toString());

        } catch (Exception ignore) {
        }

    }
}
