package fur.kiyoshi.dragoncore.commands

import fur.kiyoshi.dragoncore.format.Format.rgb
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Info: CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Your not a player")
            return true
        }
        sender.sendMessage(rgb(153, 0, 37, "DragonCore by MyNameIsKiyoshi"))
        return true
    }
}