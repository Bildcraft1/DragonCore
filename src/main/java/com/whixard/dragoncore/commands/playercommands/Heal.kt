package com.whixard.dragoncore.commands.playercommands

import com.whixard.dragoncore.api.DragonAPI
import com.whixard.dragoncore.format.Format.color
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Heal : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Your not a player")
            return true
        }
        sender.health = 20.0
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect " + sender.displayName + " clear")
        sender.foodLevel = 20
        sender.sendMessage(
            color(
                DragonAPI().getLangFile().getString("messages.heal")?.replace("{player}", sender.displayName)
            )
        )
        return true
    }
}