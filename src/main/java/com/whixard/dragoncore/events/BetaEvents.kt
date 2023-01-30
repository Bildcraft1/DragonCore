package com.whixard.dragoncore.events

import com.whixard.dragoncore.format.Format.color
import com.whixard.dragoncore.format.Format.hex
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerJoinEvent


class BetaEvents: Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        for (p in event.player.server.onlinePlayers) {
            if (p.hasPermission("dragoncore.hello") && p != event.player) {
                val component = TextComponent(
                    ChatColor.translateAlternateColorCodes(
                        '&',
                        "&7&l&oCLICCA QUI&7&o per salutare &6&o" + event.player.name + "&7!"
                    )
                )
                component.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "Ciao " + event.player.name + "!")
                p.spigot().sendMessage(component)
            }
        }
    }


    @EventHandler
    fun onPlayerAdvancement(event: PlayerAdvancementDoneEvent) {
        if (event.advancement.key.key.contains("recipes")) return
        event.player.sendMessage(hex("#9a00b3DragonAchivements >> ") + color("&7&l" + event.player.name + " ha completato l'impresa &6" + event.advancement.display?.title + "&7!"))
        event.player.playSound(event.player.location, "minecraft:entity.player.levelup", 1f, 1f)
    }
}