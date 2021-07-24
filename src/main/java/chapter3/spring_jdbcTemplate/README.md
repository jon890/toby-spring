## 6. 스프링의 JdbcTemplate

- 스프링은 JDBC를 이용하는 DAO에서 사용할 수 있도록 준비된 다양한 템플릿과 콜백을 제공한다
- 거의 모든 종류의 JDBC 코드에 사용 가능한 템플릿과 콜백을 제공할 뿐만 아니라, 자주 사용되는 패턴을 가진 콜백은 다시 템플릿에 결합시켜서 간단한 메소드 호출만으로 사용이 가능하도록 만들어져 있다
- 스프링이 제공하는 JDBC 코드용 기본 템플릿은 JdbcTemplate이다

### 1. update()

~~~
deleteAll() : jdbcTemplate.update("delete from users");
add(User user) : jdbcTemplate.update("insert into users(id, name, password) values(?, ?, ?)",
    user.getId(), user.getName(), user.getPassword());
~~~

- 사용방법 : update(sql, params);
- 결과 SQL을 조작하는 DML 구문을 실행한다
- ? 에 대응해서 파라미터를 varargs로 넣어준다

### 3. queryForObject()

~~~
get() : jdbcTemplate.queryForObject("select * from users where id = ?",
            new Object[]{id},
            new int[]{Types.VARCHAR},
            (rs, rowNum) -> new User(rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("password")));
~~~

- 사용방법 : queryForObject(sql, params, paramTypes, rowMapper);
- 결과 : 단건의 query를 실행하고 결과를 반환한다
- ?에 대응하여 Object[] 타입으로 파라미터를 넣어준다
- 파라미터의 타입들을 int[] 로 넣어준다
- rowMapper를 통하여 결과를 반환한다

### 4. query()

~~~
getAll() : jdbcTemplate.query("select * from users order by id", 
                (rs, rowNum) -> new User(rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("password")));
~~~

- 사용방법 : query(sql, rowMapper);
- 결과 : 여러건의 결과에 대해 각각 rowMapper를 실행하고 List로 반환한다

### 5. 재사용 가능한 콜백의 분리

- User 오브젝트를 반환하는 rowMapper를 공통으로 묶어내자

~~~
최종 UserDao

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
~~~

- 템플릿/콜백 패턴과 DI를 이용해 예외처리와 리소스 관리, 유연한 DataSource 활용방법까지 제공하면서도 군더더기 하나 없는 깔끔하고 간결한 코드로 정리할 수 있게 됐다
- UserDao에는 User 정보를 DB에 넣거나 가져오거나 조작하는 방법에 대한 핵심적인 로직만 담겨 있다


- 만약 사용할 테이블과 필드정보가 바뀌면 UserDao의 거의 모든 코드가 함께 바뀐다 => 응집도가 높다


- 반면에 JDBC API를 사용하는 방식, 예외처리, 리소스의 반납, DB 연결을 어떻게 가져올지에 관한 책임과 관심은 모두 JdbcTemplate에 있다
- 따라서 변경이 일어난다고 해도 UserDao 코드에는 아무런 영향을 주지 않는다
- 그런 면에서 책임이 다른 코드와 낮은 결합도를 유지하고 있다


- 다만 JdbcTemplate이라는 템플릿 클래스를 직접 이용한다면 면에서 특정 템플릿/콜백 구현에 대한 강한 결합을 갖고 있다


- 여기서 UserDao를 더 개선할 수도 있을까?
    1. userRowMapper가 인스턴스 변수로 설정되어 있고, 한 번 만들어지면 변경되지 않는 프로퍼티와 같은 성격을 띠고 있으니  
       아예 UserDao 빈의 DI용 프로퍼티로 만들어버리면 어떨까?  
       UserMapper를 독립된 빈으로 만들고 User 테이블의 필드 이름과 User 오브젝트 프로퍼티의 매핑정보를 담을수 도 있을 것이다
    2. DAO 메소드에서 사용하는 SQL 문장을 UserDao 코드가 아니라 외부 리소스에 담고 이를 읽어와 사용하게 하는 것이다  
       이렇게 해두면 DB 테이블의 이름이나 필드 이름을 변경하거나 SQL 쿼리를 최적화해야 할 때도 UserDao 코드에는 손을 댈 필요가 없다

