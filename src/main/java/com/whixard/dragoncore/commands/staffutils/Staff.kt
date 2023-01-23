package com.whixard.dragoncore.commands.staffutils

import com.whixard.dragoncore.api.DragonAPI
import com.whixard.dragoncore.format.Format
import com.whixard.dragoncore.format.Format.color
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Staff : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Sender is not a player")
            return true
        }
        if (sender.hasPermission("dragoncore.staff")) {
            sender.sendMessage(color(DragonAPI().getLangFile().getString("messages.staff")!!))
            for (x in DragonAPI().getLangFile().getStringList("messages.staff_command_list")) {
                sender.sendMessage(color(x))
            }
            return true
        } else {
            sender.sendMessage(Format.defaultrgb(DragonAPI().getConfig().getString("messages.no-permission")!!))
            return true
        }
    }
}