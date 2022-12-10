package fur.kiyoshi.dragoncore.commands.teleport

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Tpa: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Sender is not a player")
            return true
        }
        if (sender.hasPermission("dragoncore.tpa")) {
            val potentialPlayer = args[0]
            val targetPlayer = Bukkit.getServer().getPlayer(potentialPlayer)
            if (targetPlayer != null) {
                return if (targetPlayer.world.name == sender.world.name){
                    TODO("Need to work on a Accept/Denying System")
                    true
                } else {
                    sender.sendMessage("Not in the same world")
                    true
                }
            }
            sender.sendMessage("Not a player")
            return true
        } else {
            sender.sendMessage("You don't have permission")
            return true
        }
    }
}