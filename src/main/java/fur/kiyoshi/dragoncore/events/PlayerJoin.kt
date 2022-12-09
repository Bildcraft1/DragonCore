package fur.kiyoshi.dragoncore.events

import fur.kiyoshi.dragoncore.format.Format.rgb
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoin: Listener {
    @EventHandler
    fun onPlayerJoin(eventHandler: PlayerJoinEvent) {
        eventHandler.player.sendMessage(rgb(156,0,230,"Benvenuto nella modalità Lands di DragonCraft"))
    }
}