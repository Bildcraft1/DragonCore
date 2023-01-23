package com.whixard.dragoncore.commands.playercommands

import com.whixard.dragoncore.api.DragonAPI
import com.whixard.dragoncore.format.Format.color
import com.whixard.dragoncore.format.Format.defaultrgb
import com.whixard.dragoncore.format.Format.rgb
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Fly: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Not a player")
            return true
        }
        if (sender.hasPermission("dragoncore.fly")) {
            if (sender.allowFlight) {
                sender.allowFlight = false
                sender.sendMessage(color(DragonAPI().getLangFile().getString("messages.flyoff")!!))
                return true
            }
            sender.allowFlight = true
            sender.sendMessage(color(DragonAPI().getLangFile().getString("messages.flyon")!!))
            return true
        }
        return true
    }
}