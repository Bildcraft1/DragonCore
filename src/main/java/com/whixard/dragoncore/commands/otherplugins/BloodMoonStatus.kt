package com.whixard.dragoncore.commands.otherplugins

import com.whixard.dragoncore.format.Format.defaultrgb
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


object BloodMoonStatus: CommandExecutor {
    var bloodMoon = false
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
        if (sender.hasPermission("dragoncore.bloodmoonstatus")) {
            if (args[0] == "status") {
                sender.sendMessage(defaultrgb("BloodMoon is ${if (bloodMoon) "on" else "off"}"))
                sender.sendMessage(defaultrgb("PlaceHolder response:"))
                sender.sendMessage(PlaceholderAPI.setPlaceholders(sender as Player, defaultrgb("%dragoncore_bloodmoon%")))
                return true
            }

            if (args[0] == "start") {
                bloodMoon = true
                return true
            }

            if (args[0] == "stop") {
                bloodMoon = false
                return true
            }

            return if (bloodMoon) {
                    sender.sendMessage("Blood Moon is active")
                    true
            } else {
                    sender.sendMessage("Blood Moon is not active")
                    true
            }
        }
        sender.sendMessage("You do not have permission to use this command")
        return true
    }

}