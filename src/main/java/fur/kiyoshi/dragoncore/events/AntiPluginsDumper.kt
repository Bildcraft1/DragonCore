package fur.kiyoshi.dragoncore.events

import fur.kiyoshi.dragoncore.api.DragonAPI
import fur.kiyoshi.dragoncore.format.Format.color
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.server.TabCompleteEvent
import java.util.*

class AntiPluginsDumper : Listener {
    private var prefix = color("&c&lDragonCore >> &7")
    private fun blacklist(command: String): Boolean {
        for (msg in DragonAPI().getConfig().getStringList("exploit.blacklist")) {
            if (command.contains(msg)) {
                return true
            }
        }
        return false
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPluginsDumper(e: PlayerCommandPreprocessEvent) {
        if (DragonAPI().getConfig().getBoolean("functions.anti-exploit")) {
            val message = e.message.lowercase(Locale.getDefault())
            val player = e.player
            if (blacklist(message) && !player.hasPermission("dragoncore.staff")) {
                player.server.broadcast(
                    prefix + color("&c" + player.name + " has been kicked for using a plugins dumper!"),
                    "dragoncore.notify"
                )
                player.kickPlayer(color("&cYou have been kicked for using a plugins dumper!"))
                e.isCancelled = true
                e.player.sendMessage(prefix + color("&cPlugins dumpers are disabled!"))
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onTabComplete(e: TabCompleteEvent) {
        if (DragonAPI().getConfig().getBoolean("functions.anti-exploit")) {
            val message = e.buffer.lowercase(Locale.getDefault())
            val player = e.sender
            if (blacklist(message) && !player.hasPermission("dragoncore.staff")) {
                player.server.broadcast(
                    prefix + color("&c" + player.name + " has been kicked for using a plugins dumper!"),
                    "dragoncore.notify"
                )
                e.isCancelled = true
                player.sendMessage(prefix + color("&cPlugins dumpers are disabled!"))
            }
        }
    }
}