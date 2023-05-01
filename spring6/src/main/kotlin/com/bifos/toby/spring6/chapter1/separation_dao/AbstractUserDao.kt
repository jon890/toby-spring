package com.bifos.toby.spring6.chapter1.separation_dao

import com.bifos.toby.spring6.chapter1.common.User
import java.sql.Connection
import java.sql.DriverManager

// 템플릿 메소드 패턴 : 슈퍼클래스에 기본적인 흐름 (케녁선 가져오기, SQL 생성, 실행, 반환)을 만들고
// 그 기능의 일부를 추상 메소드나 오버라이딩이 가능한 protected 메소드 등으로 만든 뒤
// 서브 클래스에서 이런 메소드를 필요에 맞게 구현해서 사용하도록 하는 방법
abstract class AbstractUserDao {

    // 팩토리 메소드 패턴 : 서브클래스에서 구체적인 오브젝트 생성 방법을 결정하게 하는 것
    protected abstract fun getConnection(): Connection

    fun add(user: User) {
        val connection = getConnection()
        val preparedStatement = connection.prepareStatement("insert into users(id, name, password) values(?, ?, ?)")
        preparedStatement.setString(1, user.id)
        preparedStatement.setString(2, user.name)
        preparedStatement.setString(3, user.password)

        preparedStatement.executeUpdate()

        preparedStatement.close()
        connection.close()
    }

    fun get(id: String): User {
        val connection = getConnection()

        val preparedStatement = connection.prepareStatement("select * from users where id = ?")
        preparedStatement.setString(1, id)

        val resultSet = preparedStatement.executeQuery()
        resultSet.next()
        val user = User(resultSet.getString("id"), resultSet.getString("name"), resultSet.getString("password"))

        resultSet.close()
        preparedStatement.close()
        connection.close()

        return user
    }
}

fun main() {
    val dao = UserDao()

    val user = User("bifos", "김병태", "1234")
    dao.add(user)

    println("${user.id} 등록 성공")

    val user2 = dao.get(user.id)
    println(user2.name)
    println(user2.password)
    println("${user2.id} 조회 성공")
}