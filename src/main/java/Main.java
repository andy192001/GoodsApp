import com.mysql.cj.jdbc.MysqlDataSource;

import java.io.*;
import java.sql.SQLException;
import java.util.Properties;

public class Main {

    private static MysqlDataSource dataSource;
    static Properties prop = new Properties();
    static InputStream input = null;

    public static void main(String[] args) throws IOException {
        initDataSource();
        InputStream resourceAsStream = Main.class.getClassLoader().getResourceAsStream("table_goods.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));

        try(var connection = dataSource.getConnection()) {

            String line;
            while ((line = reader.readLine()) != null){
                String[] data = line.split("\t");

                try(var prepareStatement = connection.prepareStatement(
                        "insert into goods(id, title, price, type) values(?, ?, ?, ?);")){
                    prepareStatement.setString(1, data[0]);
                    prepareStatement.setString(2, data[1]);
                    prepareStatement.setString(3, data[2]);
                    prepareStatement.setString(4, data[3]);

                    prepareStatement.executeUpdate();
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Done!!!");
    }

    private static void initDataSource() throws IOException {
        dataSource = new MysqlDataSource();

        input = new FileInputStream("db.properties");
        prop.load(input);

        String url = prop.getProperty("db.url");
        String username = prop.getProperty("db.username");
        String password = prop.getProperty("db.password");

        dataSource.setUrl(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);
    }
}
