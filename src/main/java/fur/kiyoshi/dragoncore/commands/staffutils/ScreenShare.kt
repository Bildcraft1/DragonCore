package fur.kiyoshi.dragoncore.commands.staffutils

import fur.kiyoshi.dragoncore.api.DragonAPI
import fur.kiyoshi.dragoncore.format.Format.color
import fur.kiyoshi.dragoncore.format.Format.defaultrgb
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
                sender.sendMessage(color(DragonAPI().getLangFile().getString("messages.usage")?.replace("{usage}","/screenshare <player>" )))
                return true
            }

            val target = args[0]

            if (Bukkit.getPlayer(target) == null) {
                sender.sendMessage(color(DragonAPI().getLangFile().getString("messages.player_not_online")!!))
                return true
            }

            if (target.let {Bukkit.getPlayer(it)}?.hasPermission("dragoncore.staff") == true) {
                sender.sendMessage(color(DragonAPI().getLangFile().getString("messages.screenshare_bypass")?.replace("{player}", target)))
                return true
            }

            if (args.size > 1) {
                return if (args[1] == "legit" && screenShareMap[target] == true) {
                    screenShareMap[target] = false
                    target.let { Bukkit.getPlayer(it) }?.sendMessage(color(DragonAPI().getLangFile().getString("messages.spawn")!!))
                    target.let { Bukkit.getPlayer(it) }?.performCommand("back")
                    true
                } else {
                    sender.sendMessage(color(DragonAPI().getLangFile().getString("messages.usage")?.replace("{usage}","/screenshare <player> <legit> (Player not in SS)" )))
                    true
                }
            }

            if (screenShareMap[target] == true) {
                sender.sendMessage(color(DragonAPI().getLangFile().getString("messages.already_screensharing")!!))
                return true
            }

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvtp $target screenshare")
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvtp ${sender.name} screenshare")

            sender.sendMessage(color(DragonAPI().getLangFile().getString("messages.generic_divider")!!.replace("{title}", "ScreenShare")))
            sender.sendMessage(color(DragonAPI().getLangFile().getString("messages.screenshare_staff")?.replace("{player}", target)!!))

            val ban = net.md_5.bungee.api.chat.TextComponent("[Ban] ")
            ban.clickEvent = net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, "/tempban ${target.let {Bukkit.getPlayer(it)}?.name} 14d Hacking")
            ban.color = net.md_5.bungee.api.ChatColor.RED
            ban.isBold = true

            val legit = net.md_5.bungee.api.chat.TextComponent("[Legit]")
            legit.clickEvent = net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/screenshare ${target.let {Bukkit.getPlayer(it)}?.name} legit")
            legit.color = net.md_5.bungee.api.ChatColor.AQUA
            legit.isBold = true

            sender.spigot().sendMessage(ban, legit)

            sender.sendMessage(color(DragonAPI().getLangFile().getString("messages.divider")!!))
            target.let { Bukkit.getPlayer(it) }?.sendMessage(defaultrgb(DragonAPI().getLangFile().getString("messages.screenshare")!!.replace("{staffer}", sender.name)))
            screenShareMap[target] = true


            true
        } else {
            sender.sendMessage(defaultrgb(DragonAPI().getConfig().getString("messages.no-permission")!!))
            true
        }
    }
}