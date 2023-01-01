package fur.kiyoshi.dragoncore.api

import fur.kiyoshi.dragoncore.Main
import org.bukkit.Bukkit.getLogger
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.util.logging.Level

class DragonDatabase {
    fun getConnection(): Connection {
        try {

            val connection = DriverManager.getConnection(Main.instance.getDatabaseUrl())
            return connection
        } catch (SQLEx: Exception) {
            getLogger().log(Level.SEVERE, "Could not connect to H2 server! because: " + SQLEx.message)
        }
        return null!!
    }

    fun initialiteDatabase() {
        val connection: Connection = getConnection()
        var preparedStatement: PreparedStatement = null!!
        try {
            preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS dragoncore (uuid VARCHAR(36), username VARCHAR(16), ip VARCHAR(16), tags VARCHAR(16), PRIMARY KEY (uuid));")
            preparedStatement.execute()
        }catch (SQLEx: Exception) {
            getLogger().log(Level.SEVERE, "Could not initialize database! because: " + SQLEx.message)
        }
    }

}