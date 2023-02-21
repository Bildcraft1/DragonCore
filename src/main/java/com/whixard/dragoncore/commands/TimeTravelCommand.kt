package com.whixard.dragoncore.commands

import com.whixard.dragoncore.Main
import com.whixard.dragoncore.format.Format
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
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
import javax.swing.Action


object TimeTravelCommand: CommandExecutor {
    var bossbar: BossBar? = null
    var pre_bossbar: BossBar? = null

    private var items: MutableMap<String, ItemStack> = HashMap<String, ItemStack>()
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        items["diamond"] = ItemStack(Material.DIAMOND)
        items["diamond"]!!.amount = 3

        items["gold"] = ItemStack(Material.GOLD_INGOT)
        items["gold"]!!.amount = 10

        items["iron"] = ItemStack(Material.IRON_INGOT)
        items["iron"]!!.amount = 20

        items["ancient"] = ItemStack(Material.ANCIENT_DEBRIS)
        items["ancient"]!!.amount = 1

        items["netheritescrap"] = ItemStack(Material.NETHERITE_SCRAP)
        items["netheritescrap"]!!.amount = 2

        items["goldblock"] = ItemStack(Material.GOLD_BLOCK)
        items["goldblock"]!!.amount = 2

        items["ironblock"] = ItemStack(Material.IRON_BLOCK)
        items["ironblock"]!!.amount = 6

        items["goldenapple"] = ItemStack(Material.GOLDEN_APPLE)
        items["goldenapple"]!!.amount = 4

        if (sender.hasPermission("dragoncore.timetravelevent")) {

            if (args[0] == "start") {
                if (pre_bossbar == null) {
                    for (player in sender.server.onlinePlayers) {
                        player.playSound(player.location, Sound.BLOCK_PORTAL_TRAVEL,100F, 1F)
                    }
                    pre_bossbar = Bukkit.createBossBar(Format.color("&4&l!! &fAvvio... &c- &dSveltina del Drago"), BarColor.RED, BarStyle.SEGMENTED_10)
                    pre_bossbar!!.addFlag(BarFlag.CREATE_FOG)
                    pre_bossbar!!.addFlag(BarFlag.DARKEN_SKY)
                    pre_bossbar!!.isVisible = true
                    pre_bossbar!!.progress = 1.0
                    for (player in sender.server.onlinePlayers) {
                        pre_bossbar!!.addPlayer(player)
                    }
                    sender.server.broadcastMessage(Format.color("&4&l!! &f&lEvento &f- &d&lSveltina del Drago&7 comincierà tra 10 secondi."))
                    sender.server.broadcastMessage(Format.color("&4&l!! &7È consigliabile svuotarsi l'inventario!"))
                    preStartTimer(sender)
                }


            }
            if (args[0] == "stop") {

                if(bossbar == null){

                    sender.sendMessage(Format.color("&cNon si sta svolgendo nessun viaggio del Drago al momento."))
                    return true

                }

                sender.server.broadcastMessage(Format.color("&dEvento viaggio del Drago &cannullato da un'amministratore."))
                bossbar?.progress=0.025

            }
        }
        return true
    }


    private fun preStartTimer(sender: CommandSender) {
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
                        startTimer(sender)
                    }
                }
            }
        }.runTaskTimer(Main.instance, 0, 20)
    }

    private fun startTimer(sender : CommandSender) {
        bossbar = Bukkit.createBossBar(Format.color("&d&lSveltina del Drago"), BarColor.PURPLE, BarStyle.SEGMENTED_20)
        bossbar!!.isVisible = true
        bossbar!!.progress = 1.0
        bossbar!!.addFlag(BarFlag.CREATE_FOG)
        bossbar!!.addFlag(BarFlag.DARKEN_SKY)
        bossbar!!.style = BarStyle.SEGMENTED_20

        for (player in sender.server.onlinePlayers) {
            bossbar!!.addPlayer(player)
        }

        object : BukkitRunnable() {
            override fun run() {
                if (bossbar != null) {
                    if (bossbar!!.progress > 0.06) {
                        bossbar!!.progress -= 0.05
                    } else {
                        for(player in sender.server.onlinePlayers){
                            player.playSound(player.location,Sound.ENTITY_ENDER_DRAGON_DEATH,0.35f,1f)
                        }
                        bossbar!!.isVisible = false
                        bossbar!!.removeAll()
                        bossbar = null
                        cancel()
                    }
                }
                // Randomly decide if we should give items
                if (Math.random() < 0.9) {
                    giveItems(sender)
                }
            }
        }.runTaskTimer(Main.instance, 0, 5)
    }
    private fun giveItems(sender : CommandSender) {
        for (player in sender.server.onlinePlayers) {

            val world = player.world

            val oggetto = items.values.random()
            player.playSound(player,Sound.ENTITY_ITEM_PICKUP, 1f, 1f)
            if(player.inventory.firstEmpty()==-1) world.dropItem(player.location, oggetto)
            else player.inventory.addItem(oggetto)

            player.world.spawnParticle(Particle.PORTAL,player.location,125,5.0,2.0,5.0)
            player.world.spawnParticle(Particle.DRAGON_BREATH,player.location,125,5.0,2.0,5.0)

        }
    }
}
