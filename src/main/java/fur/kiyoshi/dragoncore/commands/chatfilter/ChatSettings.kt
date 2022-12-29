package fur.kiyoshi.dragoncore.commands.chatfilter

import fur.kiyoshi.dragoncore.api.DragonAPI
import fur.kiyoshi.dragoncore.format.Format.defaultrgb
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object ChatSettings: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Sender is not a player")
            return true
        }
        if (sender.hasPermission("dragoncore.staff")) {
            if (args.isEmpty()) {
                sender.sendMessage("Usage: /chatsettings list")
                return true
            }

            if (args[0] == "list") {
                sender.sendMessage(defaultrgb("List of filtered words:"))
                for (word in DragonAPI().getConfig().getStringList("chatfilter.blacklist")) {
                    sender.sendMessage(defaultrgb(word))
                }
                return true
            }
        }
        return true
    }
}