package com.whixard.dragoncore.commands.testcommands

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


object Mute: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Your not a player")
            return true
        }
        if (sender.hasPermission("dragoncore.test")) {
            val message = TextComponent("Click me")
            message.clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org")
            message.hoverEvent = HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                ComponentBuilder("Visit the Spigot website!").create()
            )
            sender.spigot().sendMessage(message)
        }
        return true
    }
}