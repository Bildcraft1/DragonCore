package fur.kiyoshi.dragoncore.commands.playercommands

import fur.kiyoshi.dragoncore.format.Format.rgb
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Fly: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Not a player")
            return true
        }; if (!sender.allowFlight) {
            sender.allowFlight = true
            sender.sendMessage(rgb(153, 0, 70, "Flight") + rgb(0, 250, 154, " Enabled"))
            return true
        }
        sender.allowFlight = false
        sender.sendMessage(rgb(153, 0, 70, "Flight") + rgb(178, 34, 34, " Disabled"))
        return true
    }
}