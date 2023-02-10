package com.whixard.dragoncore.events

import com.whixard.dragoncore.format.Format
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAdvancementDoneEvent

class AdvancementsMessages : Listener {
    @EventHandler
    fun onPlayerAdvancement(event: PlayerAdvancementDoneEvent) {
        if (event.advancement.key.key.contains("recipes")) return
        event.player.sendMessage(Format.hex("\uD83D\uDC32&5&lDragonAchievments &7● ") + Format.color("&7&l" + event.player.name + " ha completato l'impresa &6" + event.advancement.display?.title + "&7!"))
        event.player.playSound(event.player.location, "minecraft:entity.player.levelup", 1f, 1f)
    }
}