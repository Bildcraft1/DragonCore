package com.whixard.dragoncore.api

import java.sql.Connection
import java.sql.DriverManager
import java.util.*

class DragonDatabase {
    internal var conn: Connection? = null
    internal var username = DragonAPI().getConfig().getString("database.username") // provide the username
    internal var password = DragonAPI().getConfig().getString("database.password") // provide the password

    fun getConnection(): Connection {
        val connectionProps = Properties()
        connectionProps["user"] = username
        connectionProps["password"] = password

        try {
            Class.forName("org.mariadb.jdbc.Driver")
            conn = DriverManager.getConnection(
                "jdbc:" + DragonAPI().getConfig().getString("database.type")
                    ?.lowercase() + "://" + DragonAPI().getConfig()
                    .getString("database.host") + ":" + DragonAPI().getConfig()
                    .getString("database.port") + "/" + DragonAPI().getConfig().getString("database.database"),
                connectionProps
            )
            return conn!!
        } catch (e: Exception) {
            e.printStackTrace()
            return null!!
        }
    }

    fun closeConnection() {
        conn?.close()
    }


    fun initializeDatabase() {
        getConnection()
        val statement = conn?.createStatement()
        statement?.executeUpdate("CREATE TABLE IF NOT EXISTS `dragoncore` (`id` INT NOT NULL AUTO_INCREMENT, `uuid` VARCHAR(36) NOT NULL, `name` VARCHAR(16) NOT NULL, `tags` VARCHAR(255) NOT NULL, PRIMARY KEY (`id`));")
        statement?.executeUpdate("CREATE TABLE IF NOT EXISTS `dragoncore_reports` (id INT NOT NULL AUTO_INCREMENT, uuid VARCHAR(36) NOT NULL, name VARCHAR(16) NOT NULL, status VARCHAR(255) NOT NULL DEFAULT 'false', reason VARCHAR(255) NOT NULL, reporter VARCHAR(255) NOT NULL, PRIMARY KEY (id));")
        closeConnection()
    }

}