package com.bifos.toby.spring6.chapter1.extension_dao

import java.sql.Connection
import java.sql.DriverManager

class SimpleConnectionMaker : ConnectionMaker {

    override fun makeNewConnection(): Connection {
        Class.forName("com.mysql.cj.jdbc.Driver")
        val connection = DriverManager.getConnection("jdbc:mysql://localhost/springbook", "spring", "book")
        return connection
    }
}