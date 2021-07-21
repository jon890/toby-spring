package chapter1.ioc;

import model.User;

import java.sql.SQLException;

public class UserDaoTest {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // 이제 UserDao가 어떻게 만들어지는지 어떻게 초기화되어 있는지에 신경 쓰지 않고
        // 팩토리로 부터 UserDao 객체를 받아다가, 자신의 관심사인 테스트를 위해 활용하기만 하면 그만이다
        UserDao dao = new DaoFactory().userDao();

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