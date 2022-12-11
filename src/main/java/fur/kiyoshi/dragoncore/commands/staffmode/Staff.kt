package fur.kiyoshi.dragoncore.commands.staffmode

import fur.kiyoshi.dragoncore.format.Format.rgb
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*


object Staff: CommandExecutor {
    val staffMap: MutableMap<String, Boolean> = HashMap<String, Boolean>()
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Sender is not a player")
            return true
        }
        if (sender.hasPermission("dragoncore.staff")) {
            sender.sendMessage(rgb(153, 0, 70, "-----") + rgb(153, 0, 70, "Staff Commands") + "------")
            sender.sendMessage(rgb(153, 0, 70, "/staffmode") + " - Toggles staff mode")
            sender.sendMessage(rgb(153, 0, 70, "/stafflist") + " - Lists all staff members")
            return true
        } else {
            sender.sendMessage(rgb(153, 0, 70, "You don't have permission"))
            return true
        }
    }
}