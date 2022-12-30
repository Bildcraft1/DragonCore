package fur.kiyoshi.dragoncore.commands.staffutils

import fur.kiyoshi.dragoncore.api.DragonAPI
import fur.kiyoshi.dragoncore.format.Format.color
import fur.kiyoshi.dragoncore.format.Format.defaultrgb
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object ScreenShare: CommandExecutor {
    val screenShareMap: MutableMap<String, Boolean> = HashMap()
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
            sender.sendMessage("Sender is not a player")
            return true
        }

        return if (sender.hasPermission("dragoncore.screenshare")) {
            if (args.isEmpty()) {
                sender.sendMessage(defaultrgb("Usage: /screenshare <player>"))
                return true
            }

            val target = args[0]

            if (args.size > 1) {

                if (args[1] == "ban") {
                    sender.performCommand(PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(target), DragonAPI().getConfig().getString("screenshare.ban-command")!!))
                    return true
                }

                if (args[1] == "legit") {
                    screenShareMap[target] = false
                    target.let { Bukkit.getPlayer(it) }?.sendMessage(color(DragonAPI().getLangFile().getString("messages.spawn")!!))
                    target.let { Bukkit.getPlayer(it) }?.performCommand("spawn")
                    return true
                }
            }

            if (screenShareMap[target] == true) {
                sender.sendMessage(defaultrgb("The user is already in a screenshare"))
                return true
            }

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvtp $target screenshare")
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvtp ${sender.name} screenshare")
            target.let { Bukkit.getPlayer(it) }?.sendMessage(defaultrgb(DragonAPI().getLangFile().getString("messages.screenshare")!!.replace("{staffer}", sender.name)))
            screenShareMap[target] = true

            if (Bukkit.getPlayer(target) == null) {
                sender.sendMessage(defaultrgb("Player is not online"))
                return true
            }

            true
        } else {
            sender.sendMessage(defaultrgb(DragonAPI().getConfig().getString("messages.no-permission")!!))
            true
        }
    }
}