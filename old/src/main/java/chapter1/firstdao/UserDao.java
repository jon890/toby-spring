package chapter1.firstdao;

import model.User;

import java.sql.*;

// DB연결을 위한 Connection을 가져온다
// SQL을 담은 Statement를 만든다
// 만들어진 Statement를 실행한다
// 조회의 경우 SQL 쿼리의 실행 결과를 ResultSet으로 받아서 정보를 저장할 오브젝트에 옮겨준다
// 작업 중에 생성된 Connection, Statement, ResultSet 같은 리소스는 작업을 마친 후 반드시 닫아준다
// JDBC API가 만들어내는 예외를 잡아서 직접 처리하거나
// 메소드에 throws를 선언해서 예외가 발생하면 메소드 밖으로 던지게 한다
public class UserDao {

    public void add(User user) throws ClassNotFoundException, SQLException {
//        Class.forName("com.mysql.jdbc.driver");
//        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/springbook", "spring", "book");
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/toby_spring", "postgres", "123456789");

        PreparedStatement preparedStatement = connection.prepareStatement("insert into users(id, name, password) values(?,?,?)");
        preparedStatement.setString(1, user.getId());
        preparedStatement.setString(2, user.getName());
        preparedStatement.setString(3, user.getPassword());

        preparedStatement.execute();

        preparedStatement.close();
        connection.close();
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
//        Class.forName("com.mysql.jdbc.driver");
//        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/springbook", "spring", "book");
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/toby_spring", "postgres", "123456789");

        PreparedStatement preparedStatement = connection.prepareStatement("select * from users where id = ?");
        preparedStatement.setString(1, id);

        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();

        User user = new User();
        user.setId(resultSet.getString("id"));
        user.setName(resultSet.getString("name"));
        user.setPassword(resultSet.getString("password"));

        resultSet.close();
        preparedStatement.close();
        connection.close();

        return user;
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        UserDao dao = new UserDao();

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