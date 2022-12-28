package fur.kiyoshi.dragoncore.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Freeze: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage("Your not a player")
            return true
        }
        if (sender.hasPermission("dragoncore.freeze")) {
            if (args.isEmpty()) {
                sender.sendMessage("Usage: /freeze <time> <player>")
                return true
            }

            if (args.size == 2) {
                val target: Player? = sender.server.getPlayer(args[1])

                if (target == null) {
                    sender.sendMessage("Player not found")
                    return true
                }

                if (args[0] == "clear") {
                    sender.sendMessage("Unfreezed ${target.name}")
                    target.freezeTicks = 0
                    return true
                }
                try {
                    target.freezeTicks = args[0].toInt()
                } catch (e: Exception) {
                    sender.sendMessage("Invalid Tick Value")
                    return true
                }
                sender.sendMessage("You have been freezed")
                return true
            }


            if (args[0] == "clear") {
                sender.sendMessage("You have been unfreezed")
                sender.freezeTicks = 0
                return true
            }
            try {
                sender.freezeTicks = args[0].toInt()
            } catch (e: Exception) {
                sender.sendMessage("Invalid Tick Value")
                return true
            }
            sender.sendMessage("You have been freezed")
            return true
        }
        return true
    }
}