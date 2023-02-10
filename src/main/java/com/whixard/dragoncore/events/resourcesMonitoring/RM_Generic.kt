package com.whixard.dragoncore.events.resourcesMonitoring

import com.whixard.dragoncore.Main
import com.whixard.dragoncore.api.DragonAPI
import com.whixard.dragoncore.format.Format
import me.lucko.spark.api.statistic.StatisticWindow
import org.bukkit.Sound
import org.bukkit.scheduler.BukkitRunnable

class RM_Generic : BukkitRunnable() {   // Runs everytime and anytime

    val api : DragonAPI = DragonAPI()

    override fun run() {

        if(api.getServerTPS()?.poll(StatisticWindow.TicksPerSecond.SECONDS_10)!! < api.getConfig().getDouble("alerts.tps-when-under") && !Main.instance.is_resources_alert_running){


            for(player in Main.instance.server.onlinePlayers){

                if(player.hasPermission("dragoncore.see_resources_alerts")){

                    player.sendTitle("", Format.color("&c&lCritical Error: &cResources lacking!"),0,100,0)
                    player.playSound(player, Sound.ENTITY_ENDERMAN_DEATH,50f,0.3f)



                }

            }

            Main.instance.is_resources_alert_running = true

            //Main.instance.RAM_Alert_BossBar!!.isVisible = true
            Main.instance.TPS_Alert_BossBar!!.isVisible = true

            RM_AlertInProgress().runTaskTimerAsynchronously(Main.instance,0,20)



        }

    }

}