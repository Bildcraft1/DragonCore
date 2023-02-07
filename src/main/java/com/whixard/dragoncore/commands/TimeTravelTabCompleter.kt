package com.whixard.dragoncore.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import java.util.*

object TimeTravelTabCompleter : TabCompleter{
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String>? {


        return Arrays.asList("start","stop");

    }
}