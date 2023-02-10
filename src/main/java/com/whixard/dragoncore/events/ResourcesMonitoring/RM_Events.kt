package com.whixard.dragoncore.events.ResourcesMonitoring

import com.whixard.dragoncore.Main
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class RM_Events : Listener {

    @EventHandler
    fun onJoin(e: PlayerJoinEvent){

        if(e.player.hasPermission("dragoncore.see_resources_alerts")){
            //Main.instance.RAM_Alert_BossBar!!.addPlayer(e.player)
            Main.instance.TPS_Alert_BossBar!!.addPlayer(e.player)
        }

    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent){

        /*if(Main.instance.RAM_Alert_BossBar!!.players.contains(e.player)){
            Main.instance.RAM_Alert_BossBar!!.removePlayer(e.player)
        }*/
        if(Main.instance.TPS_Alert_BossBar!!.players.contains(e.player)){
            Main.instance.TPS_Alert_BossBar!!.removePlayer(e.player)
        }

    }

}