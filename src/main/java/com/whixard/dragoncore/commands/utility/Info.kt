package com.whixard.dragoncore.commands.utility

import com.whixard.dragoncore.api.DragonAPI
import com.whixard.dragoncore.format.Format.color
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Info : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Your not a player")
            return true
        }
        sender.sendMessage(
            color(
                "&7This server is running &bDragonCore &7version &b{version}&7 by &bMyNameIsKiyoshi&7 and &bWhiXard (0x7E6)&7.".replace(
                    "{version}",
                    DragonAPI().getPluginVersion()
                )
            )
        )
        sender.sendMessage(color("&7DragonCore is a plugin that adds a bunch of useful commands and features to your server."))
        sender.sendMessage(color("&7DragonCore is licensed under the &bGNU General Public License v3.0&7."))
        sender.sendMessage(color("&7DragonCore is open source and can be found at &bhttps://github.com/MyNameIsKiyoshi/DragonCore&7."))
        return true
    }
}