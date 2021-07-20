package chapter1.soc;

import chapter1.model.User;

import java.sql.*;

// 관심사의 분리 => 관심이 같은 것 끼리는 하나의 객체 안으로 또는 친한 객체로 모이게 한다
// 관심이 다른 것은 가능한 한 따로 떨어져서 서로 영향을 주지 않도록 분리한다

// 현재 관심 사항
// 1. DB와 연결을 위한 커넥션을 어떻게 가져올까 => 어떤 DB, 어떤 드라이버, 어떤 로그인 정보
// => 리팩토링 : 메소드로 중복된 코드를 뽑아내기 => 메소드 추출 기법
// => 리팩토링 : 슈퍼 클래스에 기본적인 로직의 흐름 (커넥션 가져오기, SQL 생성, 실행 반환)을 만들고,
// 그 기능의 일부를 추상 메소드나 오버라이딩이 가능한 protected 메소드 등으로 만든 뒤
// 서브 클래스에서 이런 메소드를 필요에 맞게 구현해서 사용하도록 하는 방법 => 템플릿 메소드 패턴 (스프링에서 애용 하는 패턴)
// 서브 클래스에서 구체적인 오브젝트 생성 방법을 결정하게 하는 방법 => 팩토리 메소드 패턴
// Connection 객체를 만들어 내는 것은 서브 클래스의 관심사항
// 서버의 DB 커넥션 풀에서 가져올 수도 있고, 드라이버를 직접 이용해 새로운 DB 커넥션을 만들수도 있음
// UserDao는 Connection 오브젝트가 만들어지는 방법과 내부 동작 방식에는 상관없이
// 자신이 필요한 기능을 Connection 인터페이스를 통해 사용하기만 할 뿐이다

// 2. DB에 보낼 SQL 문장을 담은 Statement를 만들고 실행하는 것

// 3. 작업이 끝난 후 사용한 리소스인 Statement와 Connection 오브젝트를 닫아줘서 공유 리소스 반환
public abstract class UserDao {

    public void add(User user) throws ClassNotFoundException, SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("insert into users(id, name, password) values(?,?,?)");
        preparedStatement.setString(1, user.getId());
        preparedStatement.setString(2, user.getName());
        preparedStatement.setString(3, user.getPassword());

        preparedStatement.execute();

        preparedStatement.close();
        connection.close();
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("select * from users where id = ?");
        preparedStatement.setString(1, id);

        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();

        User user = new User();
        user.setId(resultSet.getString("id"));
        user.setName(resultSet.getString("name"));
        user.setPassword(resultSet.getString("password"));

        resultSet.close();
        preparedStatement.close();
        connection.close();

        return user;
    }

    public abstract Connection getConnection() throws  ClassNotFoundException, SQLException;
}