package fur.kiyoshi.dragoncore.events

import fur.kiyoshi.dragoncore.commands.staffutils.ScreenShare.screenShareMap
import fur.kiyoshi.dragoncore.format.Format.color
import fur.kiyoshi.dragoncore.format.Format.defaultrgb
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerQuitEvent

class ScreenShareEvent: Listener {
    private var prefixes = arrayOf("/ss", "/screenshare", "/controllo")
    @EventHandler
    fun onCommand(e: PlayerCommandPreprocessEvent) {
        val player = e.player
        if (!player.hasPermission("dragoncore.staff.bypass")) {
            if (screenShareMap.contains(player) && !e.message.startsWith(prefixes[1]) && !e.message.startsWith(prefixes[2])) {
                e.isCancelled = true
                player.sendMessage(defaultrgb("You are in screenshare mode"))
            }
        }
    }

    @EventHandler
    fun onPlayerLeave(e: PlayerQuitEvent) {
        val player = e.player
        if (screenShareMap.contains(player)) {
            screenShareMap.remove(player)
            screenShareMap[player]?.performCommand("ban ${player.name} 30d Leaving during screenshare")
            Bukkit.broadcast(defaultrgb("${player.name} has been banned for leaving during SS"), "dragoncore.screenshare")
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerChat(e: AsyncPlayerChatEvent) {
        val player = e.player
        if (screenShareMap.containsKey(player)) {
            e.isCancelled = true
            val recipient = screenShareMap[player]!!
            player.sendMessage(color("§c${player.name}§7: ${e.message}"))
            recipient.sendMessage(color("§c${player.name}§7: ${e.message}"))
            //manda a staffer
        } else if (screenShareMap.containsValue(player)) {
            e.isCancelled = true
            val recipient = screenShareMap.firstNotNullOf { it.takeIf { it.value == player } }.key
            player.sendMessage(color("§9${player.name}§7: ${e.message}"))
            recipient.sendMessage(color("§9${player.name}§7: ${e.message}"))
            //manda a player
        }
    }
}