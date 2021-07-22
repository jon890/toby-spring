## 2. 변하는 것과 변하지 않는 것

### 1. JDBC try/catch/finally 코드의 문제점

- 예외처리가 적용돼서 완성도 높은 DAO 코드가 된 UserDao이지만, 막상 코드를 훑어보면 한숨부터 나온다
- 복잡한 try/catch/finally 블록이 2중으로 중첩까지 나오는데다, 모든 메소드마다 반복된다
- 반복되는 코드를 제거하거나 개선할 수 없을까?
- 그렇다면 테스트를 통해 DAO마다 예외상황에서 리소스를 반납하는지 체크하면 어떨까? => 쉽지 않다
- 이런 코드를 효과적으로 다룰 수 있는 방법은 없을까?


- 이 문제의 핵심은 <b>변하지 않는</b>, 그러나 많은 곳에서 중복되는 코드와 로직에 따라 자꾸 확장되고 자주 변하는 코드를 잘 분리해내는 작업이다

### 2. 분리와 재사용을 위한 디자인 패턴 적용

- 메소드 추출 : 먼저 생각해볼 수 있는 방법은 변하는 부분을 메소드로 빼는 것이다 =>  
  분리시킨 메소드를 다른 곳에서 재사용할 수 있어야 하는데, 이건 반대로 분리시키고 남은 메소드가 재사용이 필요한 부분이 되어버림 <b>(X)</b>
- 템플릿 메소드 패턴의 적용 : 상속을 통해 기능을 확장해서 사용하는 부분이다 =>
  가장 큰 문제는 DAO 로직마다 상속을 통해 새로운 클래스를 만들어야 한다는 점이다  
  또 확장구조가 이미 클래스를 설계하는 시점에서 고정되어 버린다는 점이다 변하지 않는 코드를 가진 UserDao의 JDBC try/catch/finally 블록과 변하는 PreparedStatement를 담고 있는
  서브 클래스들이 이미 클래스 레벨에서 컴파일 시점에 관계가 결정되어 있다  
  따라서 관계에 대한 유연성이 떨어진다 <b>(X)</b>


- 전략 패턴의 적용 : 오브젝트를 아예 둘로 분리하고 클래스 레벨에서는 인터페이스를 통해서만 의존하도록 만드는 전략 패턴이다
- deleteAll() 메소드에서 변하지 않는 부분이라고 명시한 것이 바로 이 JDBC를 이용해 DB를 업데이트하는 작업이다

    deleteAll() 의 컨텍스트
    1. DB 커넥션 가져오기
    2. PreparedStatement를 만들어줄 외부 기능 호출하기 => 전략 패턴에서 말하는 전략!
    3. 전달받은 PreparedStatement 실행하기
    4. 예외가 발생하면 이를 다시 메소드 밖으로 던지기
    5. 모든 경우에 만들어진 PreparedStatement와 Connection을 적절히 닫아주기

- 전략 패턴의 구조를 따라 2번 기능을 인터페이스로 만들어두고 인터페이스의 메소드를 통해 PreparedStatement 생성 전략을 호출해주면 된다

~~~
connection = dataSource.getConnection();
StatementStrategy strategy = new DeleteAllStatement();
preparedStatement = strategy.makePreparedStatement(connection);
preparedStatement.executeUpdate();
~~~

- 하지만 전략 패턴은 필요에 따라 컨텍스트는 그대로 유지되면서 전략을 바꿔 쓸 수 있다는 것인데
- 이렇게 컨텍스트 안에서 이미 구체적인 전략 클래스인 DeleteAllStatement를 사용하도록 고정되어 있다면 뭔가 이상하다
- 컨텍스트가 StatementStrategy 인터페이스뿐 아니라 특정 구현 클래스인 DeleteAllStatement를 직접 알고 있다는 건, 전략 패턴에도 OCP에도 잘 들어맞는다고 볼 수 없기 때문이다

#### DI 적용을 위한 클라이언트/컨텍스트 분리
- 이 문제를 해결하기 위해 전략 패턴의 실제적인 사용 방법을 좀 더 살펴보자
- 전략 패턴에 따르면 Context가 어떤 전략을 사용하게 할 것인가는 Context를 사용하는 앞단의 Client가 결정하는 게 일반적이다
- Client가 구체적인 전략의 하나를 선택하고 오브젝트로 만들어서 Context로 전달하는 것이다
- Context는 전달받은 그 Strategy 구현 클래스의 오브젝트를 사용한다


- 1장에서 처음 UserDao와 ConnectionMaker를 독립시키고 나서 UserDao가 구체적인 ConnectionMaker 구현 클래스를 만들어 사용하는데 문제가 있다고 판단됐을 때 적용했던 바로 그 방법이다
- 결국 이 구조에서 전략 오브젝트의 생성과 컨텍스트로의 전달을 담당하는 책임을 분리시킨 것이 바로 ObjectFactory이며, 이를 일반화 한것이 앞에서 살펴봤던 의존관계 주입이었다
- <b>결국 DI란 이러한 전략 패턴의 장점을 일반적으로 활용할 수 있도록 만든 구조</b>라고 볼 수 있다


~~~
Client => deleteAll() 메소드
    public void deleteAll() throws SQLException {
        // 클라이언트가 컨텍스트가 사용할 전략을 정해서 전달하는 면에서 DI 구조라고 이해할 수 있다
        StatementStrategy strategy = new DeleteAllStatement();
        jdbcContextWithStatementStrategy(strategy);
    }
    
Context => jdbcContextWithStatementStrategy() 메소드
    public void jdbcContextWithStatementStrategy(StatementStrategy statementStrategy) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = dataSource.getConnection();

            preparedStatement = statementStrategy.makePreparedStatement(connection);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
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
~~~

