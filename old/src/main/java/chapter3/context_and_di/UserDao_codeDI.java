package chapter3.context_and_di;

import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao_codeDI {

    private final DataSource dataSource;
    private final JdbcContext jdbcContext;

    @Autowired
    public UserDao_codeDI(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcContext = new JdbcContext(dataSource);
    }

    public void add(User user) throws SQLException {
        jdbcContext.workWithStatementStrategy(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into users(id, name, password) values(?,?,?)");

            preparedStatement.setString(1, user.getId());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setString(3, user.getPassword());

            return preparedStatement;
        });
    }

    public User get(String id) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement("select * from users where id = ?");
            preparedStatement.setString(1, id);

            resultSet = preparedStatement.executeQuery();
            User user = null;

            if (resultSet.next()) {
                user = new User(resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getString("password"));
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

            if (user == null) throw new EmptyResultDataAccessException(1);

            return user;
        } catch (SQLException e) {
            throw e;
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {

                }
            }

            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {

                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {

                }
            }
        }
    }

    public void deleteAll() throws SQLException {
        jdbcContext.workWithStatementStrategy(connection -> connection.prepareStatement("delete from users"));
    }

    public int getCount() throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement("select count(*) from users");

            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw e;
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {

                }
            }

            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {

                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {

                }
            }
        }
    }
}