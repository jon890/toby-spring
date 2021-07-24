package chapter3.spring_jdbcTemplate;

import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Types;
import java.util.List;

public class UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> userRowMapper = (rs, rowNum) -> new User(rs.getString("id"),
            rs.getString("name"),
            rs.getString("password"));

    @Autowired
    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void add(User user) {
        jdbcTemplate.update("insert into users(id, name, password) values(?, ?, ?)",
                user.getId(),
                user.getName(),
                user.getPassword());
    }

    public User get(String id) {
        return jdbcTemplate.queryForObject("select * from users where id = ?",
                new Object[]{id},
                new int[]{Types.VARCHAR},
                userRowMapper);
    }

    public void deleteAll() {
        jdbcTemplate.update("delete from users");
    }

    public int getCount() {
        Integer result = jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
        if (result == null) return 0;
        else return result;
    }

    public List<User> getAll() {
        return jdbcTemplate.query("select * from users order by id", userRowMapper);
    }
}