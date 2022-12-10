package fur.kiyoshi.dragoncore.commands.staffmode

import fur.kiyoshi.dragoncore.format.Format.rgb
import org.bukkit.Bukkit
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
            // Check if the player is in the map and if they are not then add them to the map and set their value to true
            return if (!staffMap.containsKey(sender.name) || staffMap[sender.name] == false) {
                staffMap[sender.name] = true
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pv on ${sender.name}")
                sender.sendMessage(rgb(153, 0, 70, "Staff Mode") + rgb(0, 250, 154, " Enabled"))
                true
            } else {
                staffMap[sender.name] = false
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pv off ${sender.name}")
                sender.sendMessage(rgb(153, 0, 70, "Staff Mode") + rgb(178, 34, 34, " Disabled"))
                true
            }
        } else {
            sender.sendMessage(rgb(153, 0, 70, "You don't have permission"))
            return true
        }
    }
}