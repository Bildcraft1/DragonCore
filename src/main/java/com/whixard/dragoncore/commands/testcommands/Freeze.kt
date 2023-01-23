package com.whixard.dragoncore.commands.testcommands

import com.whixard.dragoncore.format.Format.defaultrgb
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Freeze : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage(defaultrgb("Your not a player"))
            return true
        }
        if (sender.hasPermission("dragoncore.freeze")) {
            if (args.isEmpty()) {
                sender.sendMessage(defaultrgb("Usage: /freeze <time> <player>"))
                return true
            }

            if (args.size == 2) {
                val target: Player? = sender.server.getPlayer(args[1])

                if (target == null) {
                    sender.sendMessage(defaultrgb("Player not found"))
                    return true
                }

                if (args[0] == "clear") {
                    sender.sendMessage(defaultrgb("Unfreezed ${target.name}"))
                    target.freezeTicks = 0
                    return true
                }
                try {
                    target.freezeTicks = args[0].toInt()
                } catch (e: Exception) {
                    sender.sendMessage(defaultrgb("Invalid Tick Value"))
                    return true
                }
                sender.sendMessage(defaultrgb("You have freezed ${target.name} for ${args[0]} ticks"))
                return true
            }


            if (args[0] == "clear") {
                sender.sendMessage(defaultrgb("You have been unfreezed"))
                sender.freezeTicks = 0
                return true
            }
            try {
                sender.freezeTicks = args[0].toInt()
            } catch (e: Exception) {
                sender.sendMessage(defaultrgb("Invalid Tick Value"))
                return true
            }
            sender.sendMessage(defaultrgb("You have been freezed"))
            return true
        }
        return true
    }
}