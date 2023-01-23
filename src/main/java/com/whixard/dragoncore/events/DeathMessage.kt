package com.whixard.dragoncore.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class DeathMessage: Listener {
    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.entity
        val killer = player.killer
        val message = event.deathMessage
        if (killer != null) {
            event.deathMessage = "§c${player.name} §7was killed by §c${killer.name}§7."
        } else {
            event.deathMessage = "§c${player.name} §7died."
        }
    }
}