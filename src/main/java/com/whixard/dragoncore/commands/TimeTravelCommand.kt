package com.whixard.dragoncore.commands

import com.whixard.dragoncore.Main
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.boss.BossBar
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable


object TimeTravelCommand: CommandExecutor {
    var bossbar: BossBar? = null;
    var pre_bossbar: BossBar? = null;

    private var items: MutableMap<String, ItemStack> = HashMap<String, ItemStack>()
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        items["sword"] = ItemStack(Material.DIAMOND_SWORD)
        items["sword"]!!.addEnchantment(Enchantment.DAMAGE_ALL, 5)
        items["sword"]!!.itemMeta = items["sword"]!!.itemMeta?.apply {
            lore = listOf("Time Traveler")
        }

        items["helmet"] = ItemStack(Material.DIAMOND_HELMET)
        items["helmet"]!!.itemMeta = items["helmet"]!!.itemMeta?.apply {
            lore = listOf("Time Traveler")
        }

        if (sender.hasPermission("dragoncore.timetravelevent")) {
            if (args[0] == "start") {
                if (pre_bossbar == null) {
                    for (player in sender.server.onlinePlayers) {
                        player.playSound(player.location, Sound.BLOCK_PORTAL_TRAVEL,100F, 1F);
                    }
                    pre_bossbar = sender.server.createBossBar("Time Travel Event", org.bukkit.boss.BarColor.PURPLE, org.bukkit.boss.BarStyle.SOLID)
                    pre_bossbar!!.isVisible = true
                    pre_bossbar!!.progress = 1.0
                    for (player in sender.server.onlinePlayers) {
                        pre_bossbar!!.addPlayer(player)
                    }
                    sender.sendMessage("Time Travel Event starting in 10 seconds")
                    preStartTimer()
                }


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
        bossbar = Main.instance.server.createBossBar("Time Travel Event", org.bukkit.boss.BarColor.PURPLE, org.bukkit.boss.BarStyle.SOLID)
        bossbar!!.isVisible = true
        bossbar!!.progress = 1.0
        bossbar!!.addFlag(org.bukkit.boss.BarFlag.CREATE_FOG)
        bossbar!!.addFlag(org.bukkit.boss.BarFlag.DARKEN_SKY)
        bossbar!!.style = org.bukkit.boss.BarStyle.SEGMENTED_10

        for (player in Main.instance.server.onlinePlayers) {
            bossbar!!.addPlayer(player)
        }

        object : BukkitRunnable() {
            override fun run() {
                if (bossbar != null) {
                    if (bossbar!!.progress > 0.1) {
                        bossbar!!.progress -= 0.1
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
            world.dropItem(player.location.add(0.0, 5.0, 0.0), items.values.random())
            player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100F, 1F)
        }
    }
}