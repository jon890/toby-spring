package chapter1.expansion;

import chapter1.connection.ConnectionMaker;
import chapter1.connection.PostgreSqlConnectionManager;
import chapter1.model.User;

import java.sql.SQLException;

public class UserDaoTest {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // UserDao가 사용할 ConnectionMaker 구현 클래스를 결정하고 객체를 만든다
        ConnectionMaker connectionMaker = new PostgreSqlConnectionManager();

        // 1. UserDao 생성
        // 2. 사용할 ConnectionMaker 타입의 객체 전달 => 결국 두 객체 사이의 의존관계 설정 효과
        UserDao dao = new UserDao(connectionMaker);

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