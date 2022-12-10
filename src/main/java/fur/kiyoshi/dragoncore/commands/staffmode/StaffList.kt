package fur.kiyoshi.dragoncore.commands.staffmode

import fur.kiyoshi.dragoncore.format.Format.rgb
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object StaffList: CommandExecutor {
    /**
     * Executes the given command, returning its success.
     * <br></br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender Source of the command
     * @param command Command which was executed
     * @param label Alias of the command which was used
     * @param args Passed command arguments
     * @return true if a valid command, otherwise false
     */
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Not a player")
            return true
        }
        if (sender.hasPermission("dragoncore.stafflist")) {
            sender.sendMessage(rgb(153, 0, 70, "-----") + rgb(0, 250, 154, "Staff List") + rgb(153, 0, 70, "-----"))
            // For every player inside the staffMap if the value is true then send the player's name to the sender
            for ((key, value) in Staff.staffMap) {
                if (value) {
                    sender.sendMessage(rgb(153, 0, 70, key))
                }
            }
            return true
        }
        return true
    }
}