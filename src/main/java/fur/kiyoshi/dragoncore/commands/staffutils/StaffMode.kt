package fur.kiyoshi.dragoncore.commands.staffutils

import fur.kiyoshi.dragoncore.api.DragonAPI
import fur.kiyoshi.dragoncore.format.Format
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object StaffMode: CommandExecutor {
    val staffMap: MutableMap<String, Boolean> = HashMap<String, Boolean>()
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Sender is not a player")
            return true
        }
        if (sender.hasPermission("dragoncore.staffmode")) {
            // Check if the player is in the map and if they are not then add them to the map and set their value to true
            return if (!staffMap.containsKey(sender.name) || staffMap[sender.name] == false) {
                if (sender.hasPermission("group.dragone")) {
                    staffMap[sender.name] = true
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${sender.name} parent add fakedragone")
                    sender.sendMessage(Format.rgb(153, 0, 70, "Staff Mode") + Format.rgb(178, 34, 34, " Disabled"))
                    true
                } else {
                    staffMap[sender.name] = true
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${sender.name} parent add fakeutentestaff")
                    sender.sendMessage(Format.rgb(153, 0, 70, "Staff Mode") + Format.rgb(178, 34, 34, " Disabled"))
                    true
                }
            } else {
                if (sender.hasPermission("group.dragone")) {
                    staffMap[sender.name] = false
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${sender.name} parent remove fakedragone")
                    sender.sendMessage(Format.rgb(153, 0, 70, "Staff Mode") + Format.rgb(0, 250, 154, " Enabled"))
                    true
                } else {
                    staffMap[sender.name] = false
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${sender.name} parent remove fakeutentestaff")
                    sender.sendMessage(Format.rgb(153, 0, 70, "Staff Mode") + Format.rgb(0, 250, 154, " Enabled"))
                    true
                }
            }
        } else if(!sender.hasPermission("dragoncore.staffmode")){
            sender.sendMessage(Format.defaultrgb(DragonAPI().getConfig().getString("messages.no-permission")!!))
            return true
        }
        return true
    }
}