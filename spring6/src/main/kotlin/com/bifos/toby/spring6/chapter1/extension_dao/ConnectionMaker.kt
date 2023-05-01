package com.bifos.toby.spring6.chapter1.extension_dao

import java.sql.Connection
import java.sql.SQLException

interface ConnectionMaker {

    @Throws(ClassNotFoundException::class, SQLException::class)
    fun makeNewConnection(): Connection
}