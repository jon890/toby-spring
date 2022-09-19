## 4. 컨텍스트와 DI

### 1. JdbcContext의 분리

- 전략 패턴의 구조로 보자면
- 클라이언트 : UserDao의 메소드 (add, deleteAll ...)
- 익명 내부 클래스 : 개별적인 전략
- 컨텍스트 : jdbcContextWithStatementStrategy() 메소드
- 컨텍스트 메소드는 UserDao 내의 PreparedStatement를 실행하는 기능을 가진 메소드에서 공유 가능
- JDBC의 일반적인 작업 흐름을 담고 있는 jdbcContextWithStatementStrategy()는 다른 DAO에서도 사용 가능하다!
- 독립시켜서 모든 DAO가 사용할 수 있게 해보자

#### 클래스 분리

- 참고 : JdbcContext

#### 빈 의존관계 변경

- 새롭게 작성된 오브젝트 간의 의존관계를 살펴보고 이를 스프링 설정에 적용해보자
- UserDao는 이제 JdbcContext에 의존하고 있다
- 그런데 JdbcContext는 인터페이스인 DataSource와 달리 구체 클래스다
- 스프링의 DI는 기본적으로 인터페이스를 사이에 두고 의존 클래스를 바꿔서 사용하도록 하는 게 목적이다
- 하지만 이 경우 JdbcContext는 그 자체로 독립적인 JDBC 컨텍스트를 제공해주는 서비스 오브젝트로 의미가 있고 구현 방법이 바뀔 가능성은 없다

~~~
    오브젝트 팩토리 TestDaoFacotry에 의존관계를 설정
    @Bean
    public JdbcContext jdbcContext() {
        return new JdbcContext(dataSource());
    }
~~~

### 2. JdbcContext의 특별한 DI

- 비록 런타임 시에 DI 방식으로 외부에서 오브젝트를 주입해주는 방식을 사용하긴 했지만, 의존 오브젝트의 구현 클래스를 변경할 수는 없다

#### 스프링 빈으로 DI

- 스프링 DI의 기본 의도에는 맞지 않지만 꼭 그럴 필요는 없다
- 스프링의 DI는 넓게 보자면 객체의 생성과 관계설정에 대한 제어권한을 오브젝트에서 제거하고 외부로 위임했다는 IoC의 개념을 포괄한다
- DI의 기본을 따르고 있다고 볼 수 있음!


- 그렇다면 왜 DI 구조로 만들어야 하는가?
    1. JdbcContext가 스프링 컨테이너의 싱글톤 레지스트리에서 관리되는 싱글톤 빈이 되기 때문 => JdbcContext는 JDBC 컨텍스트 메소드를 제공해주는 일종의 서비스 오브젝트로서의 의미가 있고,
       그래서 싱글톤으로 등록되서 여러 오브젝트에서 공유해 사용되는 것이 이상적이다
    2. JdbcContext가 DI를 통해 다른 빈에 의존하고 있기 떄문  
       => DataSource 오브젝트르 주입받도록 되어있음  
       => DI를 위해서는 주입되는 오브젝트와 주입받는 오브젝트 양쪽 모두 스프링 빈으로 등록돼야 함


- 실제로 스프링에서 드물지만 이렇게 인터페이스를 사용하지 않는 클래스를 직접 의존하는 DI가 등장하는 경우도 있다
- 왜 인터페이스를 사용하지 않았을까?
- UserDao와 JdbcContext가 매우 긴밀한 관계를 가지고 강하게 결합되어 있다는 의미다
- UserDao가 JDBC 방식 대신 JPA나 하이버네이트 같은 ORM을 사용해야 한다면 JdbcContext도 통쨰로 바뀌어야 한다


- 단, 이런 클래스를 바로 사용하는 코드 구성을 DI에 적용하는 것은 가장 마지막 단계에서 고려해볼 사항임을 잊지 말자
- 인터페이스를 만들기 귀찮으니깐 그냥 클래스를 사용하자는 건 잘못된 생각이다

#### 코드를 이용한 수동 DI

- 스프링 빈으로 등록하지 않고 코드를 이용한 수동 DI를 알아보자
- JdbcContext에는 내부에 두는 상태정보가 없다 => 오브젝트가 수백 개가 만들어져도 메모리에 주는 부담은 거의 없다
- 자주 만들어졌다가 제거되는 게 아니기 때문에 GC에 대한 부담도 없다
- 남은 문제는 JdbcContext는 다른 빈을 인터페이스를 통해 간접적으로 의존하고 있다
- 의존 오브젝트를 DI를 통해 제공받기 위해서라도 자신도 빈으로 등록돼야 한다고 했다
- 그렇다면 어떻게 해야할까?


- JdbcContext에 댛란 제어권을 갖고 생성과 관리를 담당하는 UserDao에게 DI까지 맡기는 것이다
- 오브젝트를 생성하고 그 의존 오브젝트를 생성자 메소드로 주입해주는 것이 바로 DI의 동작원리가 아닌가?
- 그렇다면 UserDao가 임시로 DI 컨테이너 처럼 동작하게 만들면 된다

~~~
    UserDao_codeDI
    
    @Autowired
    public UserDao_codeDI(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcContext = new JdbcContext(dataSource);
    }
~~~

- UserDao의 메소드에서는 JdbcContext가 외부에서 빈으로 만들어져 주입된 것인지, 내부에서 직접 만들고 초기화 한것인지 구분할 필요도 없고 구분할 수도 없다
- 이 방법의 장점은 굳이 인터페이스를 두지 않아도 될 만큼 긴밀한 관계를 갖는 DAO 클래스와 JdbcContext를 어색하게 따로 빈으로 분리하지 않고 내부에서 직접 만들어 사용하면서도 다른 오브젝트에 대한 DI를 적용할 수 있다는 점이다


- 장단점이 있으니 상황에 따라 적절하다고 판단되는 방법을 선택해서 사용하면 된다