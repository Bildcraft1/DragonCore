package com.whixard.dragoncore.events

import com.whixard.dragoncore.format.Format
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAdvancementDoneEvent

class AdvancementsMessages : Listener {
    @EventHandler
    fun onPlayerAdvancement(event: PlayerAdvancementDoneEvent) {
        if (event.advancement.key.key.contains("recipes")) return
        event.player.sendMessage(Format.hex("\uD83D\uDC32&d&lDragonAchievments &f● ") + Format.color("&f" + event.player.name + " ha completato l'impresa &6" + event.advancement.display?.title + "&f, congratulazioni!"))
        event.player.playSound(event.player.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
    }
}