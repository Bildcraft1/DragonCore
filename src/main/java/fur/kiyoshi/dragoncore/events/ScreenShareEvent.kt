package fur.kiyoshi.dragoncore.events

import fur.kiyoshi.dragoncore.commands.staffutils.ScreenShare.screenShareMap
import fur.kiyoshi.dragoncore.format.Format.defaultrgb
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerQuitEvent

class ScreenShareEvent: Listener {
    var prefixes = arrayOf("/ss", "/screenshare", "/controllo")
    @EventHandler
    fun onCommand(e: PlayerCommandPreprocessEvent) {
        val player = e.player
        if (!player.hasPermission("dragoncore.staff.bypass")) {
            if (screenShareMap.contains(player.name) && screenShareMap[player.name] == true && !e.message.startsWith(prefixes[1]) && !e.message.startsWith(prefixes[2])) {
                e.isCancelled = true
                player.sendMessage(defaultrgb("You are in screenshare mode"))
            }
        }
    }

    @EventHandler
    fun onPlayerLeave(e: PlayerQuitEvent) {
        val player = e.player
        if (screenShareMap.contains(player.name) && screenShareMap[player.name] == true) {
            screenShareMap.remove(player.name)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban ${player.name} 30d Cheating")
            Bukkit.broadcast(defaultrgb("${player.name} has been banned for leaving during SS"), "dragoncore.screenshare")
        }
    }
}