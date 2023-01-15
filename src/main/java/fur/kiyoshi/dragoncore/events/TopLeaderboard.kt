package fur.kiyoshi.dragoncore.events

import fur.kiyoshi.dragoncore.Main
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import java.util.logging.Level

class TopLeaderboard: BukkitRunnable() {
    override fun run() {
        Main.instance.logger.log(Level.INFO,  "Checking for top player")
        for (x in Main.instance.server.onlinePlayers) {

            if (x.player?.name == PlaceholderAPI.setPlaceholders(null, "%ajlb_lb_cmi_user_playtime_days_1_alltime_name%")) {
                if (x.player!!.hasPermission("dragoncore.tags.TopPlayer")) {
                    return
                } else {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${x.player!!.name} permission set dragoncore.tags.TopPlayer true")
                }
            } else if (x.player!!.hasPermission("dragoncore.tags.TopPlayer")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${x.player!!.name} permission unset dragoncore.tags.TopPlayer")
            }

        }
    }
}