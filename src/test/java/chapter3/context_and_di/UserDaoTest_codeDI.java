package chapter3.context_and_di;

import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestDaoFactory_codeDI.class}) // 테스트에서 사용할 설정정보를 분리
public class UserDaoTest_codeDI {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private UserDao_codeDI dao;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    private void setUp() {
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