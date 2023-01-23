package com.whixard.dragoncore.commands.staffutils

import com.whixard.dragoncore.api.DragonAPI
import com.whixard.dragoncore.format.Format.defaultrgb
import com.whixard.dragoncore.format.Format.rgb
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object StaffList: CommandExecutor {
    /**
     * Executes the given command, returning its success.
     * <br></br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender Source of the command
     * @param command Command which was executed
     * @param label Alias of the command which was used
     * @param args Passed command arguments
     * @return true if a valid command, otherwise false
     */
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Not a player")
            return true
        }
        if (sender.hasPermission("dragoncore.stafflist")) {
            sender.sendMessage(rgb(153, 0, 70, "-----") + rgb(0, 250, 154, "Staff Online") + rgb(153, 0, 70, "-----"))
            // Check for every player if they have the permission dragoncore.staff and if they have add them to the list
            for (player in sender.server.onlinePlayers) {
                if (player.hasPermission("dragoncore.staff")) {
                    sender.sendMessage(rgb(153, 0, 70, player.displayName))
                }
            }
            sender.sendMessage(rgb(153, 0, 70, "-----") + rgb(0, 250, 154, "Staff List (Staff Mode)") + rgb(153, 0, 70, "-----"))
            for ((key, value) in StaffMode.staffMap) {
                if (value) {
                    sender.sendMessage(rgb(153, 0, 70, key))
                }
            }
            return true
        } else if(!sender.hasPermission("dragoncore.stafflist")) {
            sender.sendMessage(defaultrgb(DragonAPI().getConfig().getString("messages.no-permission")!!))
        }
        return true
    }
}