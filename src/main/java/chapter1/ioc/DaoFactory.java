package chapter1.ioc;

import chapter1.ioc.connection.ConnectionMaker;
import chapter1.ioc.connection.PostgreSqlConnectionManager;

public class DaoFactory {

    public UserDao userDao() {
        return new UserDao(connectionMaker());
    }

    public ConnectionMaker connectionMaker() {
        return new PostgreSqlConnectionManager();
    }
}
