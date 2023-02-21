package com.whixard.dragoncore.events

import com.whixard.dragoncore.format.Format
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class Mention : Listener {

    @EventHandler
    fun messageReceived(e: AsyncPlayerChatEvent){
        for(player in e.player.server.onlinePlayers){
            if(e.player.uniqueId==player.uniqueId) continue
            if(e.message.contains(player.name.toRegex())){
                e.recipients.remove(player)
                var msg = e.format
                msg = msg.replace("%1\$s",e.player.name)
                msg = msg.replace("%2\$s",e.message)
                msg = msg.replace(player.name,Format.color("&d${player.name}&f")+ChatColor.getLastColors(msg))
                player.sendMessage(msg)
                player.playSound(player, Sound.BLOCK_BELL_USE,1f,1f)
            }
        }
    }

}
