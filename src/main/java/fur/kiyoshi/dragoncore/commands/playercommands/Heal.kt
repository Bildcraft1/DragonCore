package fur.kiyoshi.dragoncore.commands.playercommands

import fur.kiyoshi.dragoncore.format.Format.rgb
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Heal: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player){
            sender.sendMessage("Your not a player")
            return true
        }
        sender.health = 20.0
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect" + sender.displayName + "clear")
        sender.foodLevel = 20
        sender.sendMessage(rgb(153, 0,70, "You have been healed"))
        return true
    }
}