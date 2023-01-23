package com.whixard.dragoncore.events.externalplugins

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

class CheckLands: BukkitRunnable() {
    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (PlaceholderAPI.setPlaceholders(player, "%lands_land_id%").toInt() >= 0){
                if (!player.hasPermission("tab.scoreboard.land")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${player.name} permission set tab.scoreboard.land")
                }
            } else {
                if (player.hasPermission("tab.scoreboard.land")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${player.name} permission unset tab.scoreboard.land")
                }
            }
        }
    }
}