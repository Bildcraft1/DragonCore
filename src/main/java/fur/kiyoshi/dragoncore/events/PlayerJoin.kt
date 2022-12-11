package fur.kiyoshi.dragoncore.events

import fur.kiyoshi.dragoncore.Main
import fur.kiyoshi.dragoncore.api.DragonAPI
import fur.kiyoshi.dragoncore.format.Format.rgb
import org.bukkit.Bukkit.getLogger
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.util.*
import java.util.logging.Level


class PlayerJoin: Listener {
    private var hash: ByteArray? = HexFormat.of().parseHex(DragonAPI().getConfig().getString("resourcePack.hash"))
    private var url = DragonAPI().getConfig().getString("resourcePack.url").toString()
    private var force = DragonAPI().getConfig().getBoolean("resourcePack.forced", false)
    private var prompt = DragonAPI().getConfig().getString("resourcePack.prompt")
    @EventHandler
    fun onPlayerJoin(eventHandler: PlayerJoinEvent) {
        eventHandler.player.sendMessage(rgb(156,0,230,"Benvenuto nella modalità Lands di DragonCraft"))
        if (Main.instance.config.getBoolean("resourcePack.enabled", false)) {
            eventHandler.player.setResourcePack(url, hash, prompt, force)
            getLogger().log(Level.INFO, "[DragonCore] " + "Loading texture pack to: " + eventHandler.player.name)
        }
    }
}