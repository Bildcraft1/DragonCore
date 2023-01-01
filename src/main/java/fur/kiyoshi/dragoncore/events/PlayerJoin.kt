package fur.kiyoshi.dragoncore.events

import fur.kiyoshi.dragoncore.Main
import fur.kiyoshi.dragoncore.api.DragonAPI
import fur.kiyoshi.dragoncore.api.DragonDatabase
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
    private var hash: ByteArray? = HexFormat.of().parseHex(DragonAPI().getConfig().getString("resourcePack.hash"))
    private var url = DragonAPI().getConfig().getString("resourcePack.url").toString()
    private var force = DragonAPI().getConfig().getBoolean("resourcePack.forced", false)
    private var prompt = DragonAPI().getConfig().getString("resourcePack.prompt")
    @EventHandler
    fun onPlayerJoin(eventHandler: PlayerJoinEvent) {
        eventHandler.player.sendMessage(color("&bBenvenuto nella modalità Lands di DragonCraft"))

        var statement: PreparedStatement? = null
        val connection: Connection = DragonDatabase().getConnection()
        try {
            statement = connection.prepareStatement("INSERT INTO dragoncore (uuid, username, ip, tags) VALUES (?, ?, ?, ?);")
            statement.setString(1, eventHandler.player.uniqueId.toString())
            statement.setString(2, eventHandler.player.name)
            statement.setString(3, eventHandler.player.address?.hostString ?: "null")
            statement.setString(4, "None")

            connection.close()
        } catch (SQLEx: Exception) {
            getLogger().log(Level.SEVERE, "Could not insert player into database! because: " + SQLEx.message)
        }
        if (Main.instance.config.getBoolean("resourcePack.enabled", false)) {
            eventHandler.player.setResourcePack(url, hash, prompt, force)
            getLogger().log(Level.INFO, "[DragonCore] " + "Loading texture pack to: " + eventHandler.player.name)
        }
    }
}