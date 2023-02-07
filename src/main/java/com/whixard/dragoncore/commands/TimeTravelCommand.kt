package com.whixard.dragoncore.commands

import com.whixard.dragoncore.Main
import com.whixard.dragoncore.format.Format
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable


object TimeTravelCommand: CommandExecutor {
    var bossbar: BossBar? = null
    var pre_bossbar: BossBar? = null

    private var items: MutableMap<String, ItemStack> = HashMap<String, ItemStack>()
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        items["diamond"] = ItemStack(Material.DIAMOND)
        items["diamond"]!!.amount = 3
        items["diamond"]!!.itemMeta = items["diamond"]!!.itemMeta?.apply {
            lore = listOf("Time Traveler")
        }

        items["gold"] = ItemStack(Material.GOLD_INGOT)
        items["gold"]!!.amount = 10
        items["gold"]!!.itemMeta = items["gold"]!!.itemMeta?.apply {
            lore = listOf("Time Traveler")
        }

        items["iron"] = ItemStack(Material.IRON_INGOT)
        items["iron"]!!.amount = 20
        items["iron"]!!.itemMeta = items["iron"]!!.itemMeta?.apply {
            lore = listOf("Time Traveler")
        }

        items["ancient"] = ItemStack(Material.ANCIENT_DEBRIS)
        items["ancient"]!!.amount = 1
        items["ancient"]!!.itemMeta = items["ancient"]!!.itemMeta?.apply {
            lore = listOf("Time Traveler")
        }

        if (sender.hasPermission("dragoncore.timetravelevent")) {

            if (args[0] == "start") {
                if (pre_bossbar == null) {
                    for (player in Main.instance.server.onlinePlayers) {
                        player.playSound(player.location, Sound.BLOCK_PORTAL_TRAVEL,100F, 1F)
                    }
                    pre_bossbar = Bukkit.createBossBar(Format.color("&f Sta per cominciare &c- &dViaggio del Drago"), BarColor.PURPLE, BarStyle.SOLID)
                    pre_bossbar!!.addFlag(BarFlag.CREATE_FOG)
                    pre_bossbar!!.addFlag(BarFlag.DARKEN_SKY)
                    pre_bossbar!!.isVisible = true
                    pre_bossbar!!.progress = 1.0
                    for (player in Main.instance.server.onlinePlayers) {
                        pre_bossbar!!.addPlayer(player)
                    }
                    sender.server.broadcastMessage(Format.color("&dEvento viaggio del Drago inizierà tra 10 secondi."))
                    sender.server.broadcastMessage(Format.color("&7È consigliabile svuotarsi l'inventario!"))
                    preStartTimer()
                }


            }
            if (args[0] == "stop") {

                if(bossbar == null){

                    sender.sendMessage(Format.color("&cNon si sta svolgendo nessun viaggio del Drago al momento."))
                    return true

                }

                bossbar?.progress=0.1

            }
        }
        return true
    }


    private fun preStartTimer() {
        object : BukkitRunnable() {
            override fun run() {
                if (pre_bossbar != null) {
                    if (pre_bossbar!!.progress > 0.1) {
                        pre_bossbar!!.progress -= 0.1
                    } else {
                        pre_bossbar!!.isVisible = false
                        pre_bossbar!!.removeAll()
                        pre_bossbar = null
                        cancel()
                        startTimer()
                    }
                }
            }
        }.runTaskTimer(Main.instance, 0, 20)
    }

    private fun startTimer() {
        bossbar = Bukkit.createBossBar(Format.color("&d&lViaggio del Drago"), BarColor.PURPLE, BarStyle.SOLID)
        bossbar!!.isVisible = true
        bossbar!!.progress = 1.0
        bossbar!!.addFlag(BarFlag.CREATE_FOG)
        bossbar!!.addFlag(BarFlag.DARKEN_SKY)
        bossbar!!.style = BarStyle.SEGMENTED_10

        for (player in Main.instance.server.onlinePlayers) {
            bossbar!!.addPlayer(player)
        }

        object : BukkitRunnable() {
            override fun run() {
                if (bossbar != null) {
                    if (bossbar!!.progress > 0.1) { // CA. 39 CICLI ESATTI ( 0.025 * 40 = 1)
                        bossbar!!.progress -= 0.025
                    } else {
                        bossbar!!.isVisible = false
                        bossbar!!.removeAll()
                        bossbar = null
                        cancel()
                    }
                }
                // Randomly decide if we should give items
                if (Math.random() < 0.5) {
                    giveItems()
                }
            }
        }.runTaskTimer(Main.instance, 0, 20)
    }

    private fun giveItems() {
        for (player in Main.instance.server.onlinePlayers) {
            val world = player.world
            world.dropItem(player.location.add(0.0, 2.0, 0.0), items.values.random())
            player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100F, 1F)
        }
    }
}