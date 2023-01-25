package com.whixard.dragoncore.commands.staffutils

import com.whixard.dragoncore.Main
import com.whixard.dragoncore.api.DragonAPI
import com.whixard.dragoncore.format.Format
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object RandomTp: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Sender is not a player")
            return true
        }
        return if (sender.hasPermission("dragoncore.staff")) {
            sender.teleport(Main.instance.server.onlinePlayers.random().location)
            true
        } else {
            sender.sendMessage(Format.defaultrgb(DragonAPI().getConfig().getString("messages.no-permission")!!))
            true
        }
    }

}