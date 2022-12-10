package fur.kiyoshi.dragoncore.commands.utility

import fur.kiyoshi.dragoncore.format.Format.rgb
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Help: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Your not a player")
            return true
        }
        if (sender.hasPermission("dragoncore.help")) {
            sender.sendMessage(rgb(153, 0, 37, "-----DragonCore Help-----"))
            sender.sendMessage(rgb(153, 0, 37, "/fly - Toggles flight"))
            sender.sendMessage(rgb(153, 0, 37, "/info - Shows info about the plugin"))
            sender.sendMessage(rgb(153, 0, 37, "/tp <player> - Teleports to a player"))
            sender.sendMessage(rgb(153, 0, 37, "/tpa <player> - Sends a teleport request to a player"))
            sender.sendMessage(rgb(153, 0, 37, "/staff - Set staff mode"))
            sender.sendMessage(rgb(153, 0, 37, "/stafflist - Shows a list of staff members online"))
            sender.sendMessage(rgb(153, 0, 37, "/heal - Heals you"))

            return true
        } else {
            sender.sendMessage(rgb(153, 0, 37, "You don't have permission"))
            return true
        }
    }
}