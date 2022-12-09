package fur.kiyoshi.dragoncore.commands.funcommands

import fur.kiyoshi.dragoncore.format.Format.rgb
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

object Horse: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Not a player")
            return true
        }
        sender.world.spawnEntity(sender.getLocation(), EntityType.HORSE)
        sender.sendMessage(rgb(153,0,37,"Successfully spawned the ") + rgb(255,255,255, "H O R S E"))
        return true
    }
}