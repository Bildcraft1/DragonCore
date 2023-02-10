package com.whixard.dragoncore.events.ResourcesMonitoring

import com.whixard.dragoncore.Main
import com.whixard.dragoncore.api.DragonAPI
import com.whixard.dragoncore.format.Format
import me.lucko.spark.api.statistic.StatisticWindow
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class RM_AlertInProgress : BukkitRunnable() {

    val api : DragonAPI = DragonAPI()

    override fun run() {

        if(api.getServerTPS()?.poll(StatisticWindow.TicksPerSecond.SECONDS_10)!! > api.getConfig().getDouble("alerts.tps-when-under")){

            Main.instance.is_resources_alert_running = false

            for(player in Main.instance.server.onlinePlayers){

                if(player.hasPermission("dragoncore.see_resources_alerts")){

                    player.sendTitle("", Format.color("&c&lAlert finished, we are safe!"),0,100,0)
                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP,100f,0.9f)

                }

            }

            Main.instance.TPS_Alert_BossBar!!.isVisible = false
            //Main.instance.RAM_Alert_BossBar!!.isVisible = false

            Main.instance.TPS_Alert_BossBar!!.progress = 0.1
            //Main.instance.RAM_Alert_BossBar!!.progress = 0.0

            this.cancel()

        }
        else{

            if((api.getServerTPS()!!.poll(StatisticWindow.TicksPerSecond.SECONDS_5) / 20.0) >=0 && (api.getServerTPS()!!.poll(StatisticWindow.TicksPerSecond.SECONDS_5) / 20.0) <=20){

                Main.instance.TPS_Alert_BossBar!!.progress = (api.getServerTPS()!!.poll(StatisticWindow.TicksPerSecond.SECONDS_5) / 20.0)
                Main.instance.TPS_Alert_BossBar!!.setTitle(Format.color("&fTPS: &c"+String.format("%.1f", api.getServerTPS()!!.poll(StatisticWindow.TicksPerSecond.SECONDS_5))))

            }



            //Main.instance.RAM_Alert_BossBar!!.progress = api.getServerUsingRamPercentage().toDouble()/100
            //Main.instance.RAM_Alert_BossBar!!.setTitle(Format.color("&fRAM USAGE: &c"+api.getServerUsingRamPercentage()+"%"))

        }






    }

}