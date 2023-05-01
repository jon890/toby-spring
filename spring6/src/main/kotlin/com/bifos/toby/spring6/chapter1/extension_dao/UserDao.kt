package com.bifos.toby.spring6.chapter1.extension_dao

import com.bifos.toby.spring6.chapter1.common.User

class UserDao(
    private val connectionMaker: ConnectionMaker,
) {
    fun add(user: User) {
        val connection = connectionMaker.makeNewConnection()
        val preparedStatement = connection.prepareStatement("insert into users(id, name, password) values(?, ?, ?)")
        preparedStatement.setString(1, user.id)
        preparedStatement.setString(2, user.name)
        preparedStatement.setString(3, user.password)

        preparedStatement.executeUpdate()

        preparedStatement.close()
        connection.close()
    }

    fun get(id: String): User {
        val connection = connectionMaker.makeNewConnection()

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
    val simpleConnectionMaker = SimpleConnectionMaker()
    val dao = UserDao(simpleConnectionMaker)

    val user = User("bifos", "김병태", "1234")
    dao.add(user)

    println("${user.id} 등록 성공")

    val user2 = dao.get(user.id)
    println(user2.name)
    println(user2.password)
    println("${user2.id} 조회 성공")
}