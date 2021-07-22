package chapter3.context_and_di;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration
public class TestDaoFactory_springDI {

    @Bean
    public UserDao_springDI userDao() {
        return new UserDao_springDI(dataSource(), jdbcContext());
    }

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(org.postgresql.Driver.class);
        dataSource.setUrl("jdbc:postgresql://localhost/toby_spring_test");
        dataSource.setUsername("postgres");
        dataSource.setPassword("123456789");

        return dataSource;
    }

    @Bean
    public JdbcContext jdbcContext() {
        return new JdbcContext(dataSource());
    }
}
