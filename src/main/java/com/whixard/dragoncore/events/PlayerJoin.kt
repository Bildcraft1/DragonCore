package com.whixard.dragoncore.events

import com.whixard.dragoncore.api.DragonAPI
import com.whixard.dragoncore.api.DragonDatabase
import com.whixard.dragoncore.commands.tags.Tags
import com.whixard.dragoncore.format.Format.color
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit.getLogger
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.sql.Connection
import java.sql.PreparedStatement
import java.util.*
import java.util.logging.Level


class PlayerJoin : Listener {
    private fun checkUser(user: UUID): Any {
        // Check if the user is already in the database
        val sql = "SELECT * FROM dragoncore WHERE uuid = ?"
        val conn: Connection = DragonDatabase().getConnection()
        val statement: PreparedStatement? = conn.prepareStatement(sql)
        statement?.setString(1, user.toString())
        val result = statement?.executeQuery()
        return result!!.next()
    }

    private fun registerNewUser(user: UUID, name: String) {
        // If the user is not in the database, add him
        val sql = "INSERT INTO dragoncore (uuid, name, tags) VALUES (?, ?, ?)"
        val conn: Connection = DragonDatabase().getConnection()
        var statement: PreparedStatement? = conn.prepareStatement(sql)
        statement?.setString(1, user.toString())
        statement?.setString(2, name)
        statement?.setString(3, "None")
        statement?.executeUpdate()
        getLogger().log(Level.INFO, "Added $name to the database")


        /*
        // Add the player to the friends database
        val sql2 = "INSERT INTO dragoncore_friends (uuid, friends) VALUES (?, ?)"
        val conn2: Connection = DragonDatabase().getConnection()
        val statement2: PreparedStatement? = conn2.prepareStatement(sql2)
        statement2?.setString(1, user.toString())
        statement2?.setString(2, "None")
        statement2?.executeUpdate()
        getLogger().log(Level.INFO, "Added $name to the friends database")*/
    }

    private var hash: ByteArray? = HexFormat.of().parseHex(DragonAPI().getConfig().getString("resourcePack.hash"))
    private var url = DragonAPI().getConfig().getString("resourcePack.url").toString()
    private var force = DragonAPI().getConfig().getBoolean("resourcePack.forced")
    private var prompt = DragonAPI().getConfig().getString("resourcePack.prompt")

    private fun isVanished(player: Player): Boolean {
        for (meta in player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true
        }
        return false
    }

    @EventHandler
    fun salutePlayer(event: PlayerJoinEvent) {
        val mex = DragonAPI().getConfig().getList("salutiplayers")

        if (mex == null) {
            getLogger().log(Level.WARNING, "Non hai impostato nessun saluto per i giocatori!")
            return
        }

        if (isVanished(event.player)) return

        for (p in event.player.server.onlinePlayers) {
            if (p.hasPermission("dragoncore.hello") && p != event.player) {
                val component = TextComponent(
                    ChatColor.translateAlternateColorCodes(
                        '&',
                        "&7&l&oCLICCA QUI&7&o per salutare &6&o" + event.player.name + "&7!"
                    )
                )
                component.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, mex.random().toString().replace("{player_name}", event.player.name))
                p.spigot().sendMessage(component)
            }
        }
    }

    @EventHandler
    fun onPlayerJoin(eventHandler: PlayerJoinEvent) {
        eventHandler.player.sendMessage(color("&bBenvenuto nella modalità Lands di DragonCraft"))

        if (checkUser(eventHandler.player.uniqueId) == true) {
            getLogger().log(Level.INFO, "[DragonCore] Loading user data for ${eventHandler.player.name}")
        } else {
            registerNewUser(eventHandler.player.uniqueId, eventHandler.player.name)
        }

        Tags.loadTags(eventHandler.player)

        if (com.whixard.dragoncore.Main.instance.config.getBoolean("resourcePack.enabled", false)) {
            eventHandler.player.setResourcePack(url, hash, prompt, force)
            getLogger().log(Level.INFO, "[DragonCore] " + "Loading texture pack to: " + eventHandler.player.name)
        }
    }
}