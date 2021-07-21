package chapter1.ioc;

import chapter1.connection.ConnectionMaker;
import chapter1.connection.PostgreSqlConnectionManager;

public class DaoFactory {

    public UserDao userDao() {
        return new UserDao(connectionMaker());
    }

    public ConnectionMaker connectionMaker() {
        return new PostgreSqlConnectionManager();
    }
}
