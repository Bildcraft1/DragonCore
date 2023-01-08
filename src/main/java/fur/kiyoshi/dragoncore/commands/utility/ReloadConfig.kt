package fur.kiyoshi.dragoncore.commands.utility

import fur.kiyoshi.dragoncore.Main
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object ReloadConfig: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Your not a player")
            return true
        }
        if (sender.hasPermission("dragoncore.reload")) {
            sender.sendMessage("Reloading Config")
            Main.instance.reloadConfig()
            sender.sendMessage("Reloaded Config")
        }
        return true
    }
}