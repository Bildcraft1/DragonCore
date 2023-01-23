package com.whixard.dragoncore.commands.utility

import com.whixard.dragoncore.format.Format.color
import com.whixard.dragoncore.format.Format.rgb
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Help : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Your not a player")
            return true
        }
        if (sender.hasPermission("dragoncore.help")) {
            sender.sendMessage(color("&7------------=[ &bDragonCore &7]=--------------"))
            sender.sendMessage(color("&b/Staff &7- Shows the staff commands"))
            sender.sendMessage(color("&b/Help &7- Shows the help commands"))
            sender.sendMessage(color("&b/tp <Player> &7- Teleports you to the player"))
            sender.sendMessage(color("&b/tpa <Player> &7- Teleports you to the player"))
            sender.sendMessage(color("&b/heal &7- Heals you"))
            return true
        } else {
            sender.sendMessage(rgb(153, 0, 37, "You don't have permission"))
            return true
        }
    }
}