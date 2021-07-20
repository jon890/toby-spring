package chapter1.soc;

import chapter1.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreSqlDao extends UserDao {

    @Override
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/toby_spring", "postgres", "123456789");
        return connection;
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        PostgreSqlDao dao = new PostgreSqlDao();

        User user = new User();
        user.setId("bifos");
        user.setName("김병태");
        user.setPassword("12345678");

        dao.add(user);

        System.out.println(user.getId() + " 등록 성공");

        User user2 = dao.get(user.getId());
        System.out.println(user2.getName());
        System.out.println(user2.getPassword());

        System.out.println(user2.getId() + " 조회 성공");
    }
}