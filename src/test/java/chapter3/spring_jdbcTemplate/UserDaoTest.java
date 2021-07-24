package chapter3.spring_jdbcTemplate;

import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestDaoFactory.class}) // 테스트에서 사용할 설정정보를 분리
public class UserDaoTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private UserDao dao;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    private void setUp() {
        user1 = new User("asdf", "홍길동", "springno1");
        user2 = new User("qwer", "김민수", "springno2");
        user3 = new User("zxcv", "김영희", "springno3");
    }

    @Test
    public void 사용자를_추가하고_가져오기() {
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
    public void 사용자의_수() {
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
    public void 사용자_가져오기_실패() {
        dao.deleteAll();
        assertEquals(dao.getCount(), 0);

        assertThrows(EmptyResultDataAccessException.class, () -> dao.get("unknown_id"));
    }

    @Test
    public void 모든_사용자_가져오기() {
        dao.deleteAll();

        dao.add(user1);
        List<User> users1 = dao.getAll();
        assertEquals(users1.size(), 1);
        assertTrue(user1.equals(users1.get(0)));

        dao.add(user2);
        List<User> users2 = dao.getAll();
        assertEquals(users2.size(), 2);
        assertTrue(user2.equals(users2.get(1)));

        dao.add(user3);
        List<User> users3 = dao.getAll();
        assertEquals(users3.size(), 3);
        assertTrue(user3.equals(users3.get(2)));
    }
}