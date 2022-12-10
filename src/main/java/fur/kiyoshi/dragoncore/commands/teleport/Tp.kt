package fur.kiyoshi.dragoncore.commands.teleport

import fur.kiyoshi.dragoncore.format.Format.rgb
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


object Tp: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Sender is not a player")
            return true
        }
        if (sender.hasPermission("dragoncore.tp")) {
            val potentialPlayer = args[0]
            val targetPlayer = Bukkit.getServer().getPlayer(potentialPlayer)
            if (targetPlayer != null) {
                sender.teleport(targetPlayer.location)
                sender.sendMessage(rgb(153, 0, 70,"Teleported to: ") + rgb(0, 250, 154, targetPlayer.displayName))
                return true
            }
            sender.sendMessage(rgb(153, 0, 70,"Not a player"))
            return true
        } else {
            sender.sendMessage(rgb(153, 0, 70,"You don't have permission"))
            return true
        }
    }
}