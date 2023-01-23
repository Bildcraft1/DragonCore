package com.whixard.dragoncore.commands.utility

import com.whixard.dragoncore.api.DragonAPI
import com.whixard.dragoncore.format.Format.color
import com.whixard.dragoncore.format.Format.defaultrgb
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

object Version: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        sender.sendMessage(color(DragonAPI().getLangFile().getString("messages.about")?.replace("{version}", DragonAPI().getPluginVersion())))
        return true
    }
}