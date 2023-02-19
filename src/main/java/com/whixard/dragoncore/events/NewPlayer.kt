package com.whixard.dragoncore.events

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityTargetEvent

class NewPlayer : Listener {
    @EventHandler
    fun onMonsterAttack(event: EntityTargetEvent) {
        if (event.target == null) {
            return
        }
        if (event.target is Player) {
            if ((event.target as Player).world.name == "mondo2" && (PlaceholderAPI.setPlaceholders(event.target as Player,  "%cmi_user_playtime_hours%")) <= "3") {
                if (event.entity is Player) {
                    return
                }

                if (event.reason == EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY || event.reason == EntityTargetEvent.TargetReason.TARGET_ATTACKED_NEARBY_ENTITY || event.reason == EntityTargetEvent.TargetReason.TEMPT) {
                    return
                }

                event.isCancelled = true
            }
        }
    }
}