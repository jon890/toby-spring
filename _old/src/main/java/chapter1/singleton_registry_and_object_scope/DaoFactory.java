package chapter1.singleton_registry_and_object_scope;

import chapter1.connection.ConnectionMaker;
import chapter1.connection.PostgreSqlConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 애플리케이션 컨텍스트가 사용할 설정정보라는 표시
public class DaoFactory {

    @Bean // 오브 젝트 생성을 담당하는 IoC용 메소드라는 표시
    public UserDao userDao() {
        return new UserDao(connectionMaker());
    }

    @Bean
    public ConnectionMaker connectionMaker() {
        return new PostgreSqlConnectionManager();
    }
}
