package com.whixard.dragoncore.commands.teleport

import com.whixard.dragoncore.api.DragonAPI
import com.whixard.dragoncore.format.Format
import com.whixard.dragoncore.format.Format.defaultrgb
import com.whixard.dragoncore.format.Format.rgb
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


object Tp : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Sender is not a player")
            return true
        }
        if (sender.hasPermission("dragoncore.tp")) {
            if (args.isEmpty()) {
                sender.sendMessage(defaultrgb("Usage: /tp <PlayerName>"))
                return true
            }

            val potentialPlayer = args[0]
            val targetPlayer = Bukkit.getServer().getPlayer(potentialPlayer)
            if (targetPlayer != null) {
                sender.teleport(targetPlayer.location)
                sender.sendMessage(rgb(153, 0, 70, "Teleported to: ") + rgb(0, 250, 154, targetPlayer.displayName))
                return true
            }
            sender.sendMessage(Format.color(DragonAPI().getLangFile().getString("messages.player_not_online")!!))
            return true
        } else {
            sender.sendMessage(defaultrgb(DragonAPI().getConfig().getString("messages.no-permission")!!))
            return true
        }
    }
}