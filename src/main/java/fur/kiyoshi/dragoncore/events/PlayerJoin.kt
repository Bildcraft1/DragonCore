package fur.kiyoshi.dragoncore.events

import fur.kiyoshi.dragoncore.Main
import fur.kiyoshi.dragoncore.api.DragonAPI
import fur.kiyoshi.dragoncore.api.DragonDatabase
import fur.kiyoshi.dragoncore.commands.tags.Tags
import fur.kiyoshi.dragoncore.format.Format.color
import org.bukkit.Bukkit.getLogger
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.sql.Connection
import java.sql.PreparedStatement
import java.util.*
import java.util.logging.Level


class PlayerJoin: Listener {
    fun checkUser(user: UUID): Any {
        // Check if the user is already in the database
        val sql = "SELECT * FROM dragoncore WHERE uuid = ?"
        val conn: Connection = DragonDatabase().getConnection()
        val statement: PreparedStatement? = conn.prepareStatement(sql)
        statement?.setString(1, user.toString())
        val result = statement?.executeQuery()
        return result!!.next()
    }

    private var hash: ByteArray? = HexFormat.of().parseHex(DragonAPI().getConfig().getString("resourcePack.hash"))
    private var url = DragonAPI().getConfig().getString("resourcePack.url").toString()
    private var force = DragonAPI().getConfig().getBoolean("resourcePack.forced")
    private var prompt = DragonAPI().getConfig().getString("resourcePack.prompt")
    @EventHandler
    fun onPlayerJoin(eventHandler: PlayerJoinEvent) {
        eventHandler.player.sendMessage(color("&bBenvenuto nella modalità Lands di DragonCraft"))

        if (checkUser(eventHandler.player.uniqueId) == true) {
            getLogger().log(Level.INFO, "[DragonCore] Loading user data for ${eventHandler.player.name}")
        } else {
            // If the user is not in the database, add him
            val sql = "INSERT INTO dragoncore (uuid, name, tags) VALUES (?, ?, ?)"
            val conn: Connection = DragonDatabase().getConnection()
            var statement: PreparedStatement? = conn.prepareStatement(sql)
            statement?.setString(1, eventHandler.player.uniqueId.toString())
            statement?.setString(2, eventHandler.player.name)
            statement?.setString(3, "None")
            statement?.executeUpdate()
            getLogger().log(Level.INFO, "Added ${eventHandler.player.name} to the database")
        }

        Tags.loadTags(eventHandler.player)

        if (Main.instance.config.getBoolean("resourcePack.enabled", false)) {
            eventHandler.player.setResourcePack(url, hash, prompt, force)
            getLogger().log(Level.INFO, "[DragonCore] " + "Loading texture pack to: " + eventHandler.player.name)
        }
    }
}