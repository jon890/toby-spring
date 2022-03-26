package chapter2.junit;

import chapter2.configuration.DaoFactory;
import chapter2.dao.UserDao;
import model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserDaoTest {

    private static UserDao dao;

    private static User user1;
    private static User user2;
    private static User user3;

    // 테스트 클래스에서 딱 한번만 실행됨
    // vs @BeforeEach
    @BeforeAll
    public static void setUp() {
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        dao = context.getBean("userDao", UserDao.class);

        user1 = new User("asdf", "홍길동", "springno1");
        user2 = new User("zxcv", "김민수", "springno2");
        user3 = new User("qwer", "김영희", "springno3");
    }

    @Test
    public void addAndGet() throws SQLException {
        dao.deleteAll();
        assertEquals(dao.getCount(), 0);

        dao.add(user1);
        dao.add(user2);
        assertEquals(dao.getCount(), 2);

        User userget1 = dao.get(user1.getId());
        assertEquals(user1.getName(), userget1.getName());
        assertEquals(user1.getPassword(), userget1.getPassword());

        User userget2 = dao.get(user2.getId());
        assertEquals(user2.getName(), userget2.getName());
        assertEquals(user2.getPassword(), userget2.getPassword());
    }

    @Test
    public void count() throws SQLException {
        dao.deleteAll();
        assertEquals(dao.getCount(), 0);

        dao.add(user1);
        assertEquals(dao.getCount(), 1);

        dao.add(user2);
        assertEquals(dao.getCount(), 2);

        dao.add(user3);
        assertEquals(dao.getCount(), 3);
    }

    @Test
    public void getUserFailure() throws SQLException {
        dao.deleteAll();
        assertEquals(dao.getCount(), 0);

        assertThrows(EmptyResultDataAccessException.class, () -> dao.get("unknown_id"));
    }
}