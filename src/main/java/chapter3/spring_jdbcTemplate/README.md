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
