package com.whixard.dragoncore.commands.staffutils

import com.whixard.dragoncore.api.DragonAPI
import com.whixard.dragoncore.format.Format.color
import com.whixard.dragoncore.format.Format.defaultrgb
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import net.md_5.bungee.api.chat.*
import net.md_5.bungee.api.ChatColor
import java.util.stream.Collectors

object ScreenShare: CommandExecutor {

    //1° = player, 2° = staffer
    val screenShareMap: MutableMap<Player, Player> = HashMap()
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

            val targetPlayer = Bukkit.getPlayer(target)

            if (targetPlayer == null) {
                sender.sendMessage(color(DragonAPI().getLangFile().getString("messages.player_not_online")!!))
                return true
            }

            if (targetPlayer.hasPermission("dragoncore.staff")) {
                sender.sendMessage(color(DragonAPI().getLangFile().getString("messages.screenshare_bypass")?.replace("{player}", target)))
                return true
            }

            if (args.size > 1) {
                return if (args[1] == "legit" && screenShareMap.contains(targetPlayer)) {
                    screenShareMap.remove(targetPlayer)
                    target.let { Bukkit.getPlayer(it) }?.sendMessage(color(DragonAPI().getLangFile().getString("messages.spawn")!!))
                    target.let { Bukkit.getPlayer(it) }?.performCommand("back")
                    true
                } else {
                    sender.sendMessage(color(DragonAPI().getLangFile().getString("messages.usage")?.replace("{usage}","/screenshare <player> <legit> (Player not in SS)" )))
                    true
                }
            }

            if (screenShareMap.containsValue(sender)) {
                sender.sendMessage("You cant screenshare more than one people")
            }

            if (screenShareMap.contains(targetPlayer)) {
                sender.sendMessage(color(DragonAPI().getLangFile().getString("messages.already_screensharing")!!))
                return true
            }

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvtp $target screenshare")
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvtp ${sender.name} screenshare")

            sender.sendMessage(color(DragonAPI().getLangFile().getString("messages.generic_divider")!!.replace("{title}", "ScreenShare")))
            sender.sendMessage(color(DragonAPI().getLangFile().getString("messages.screenshare_staff")?.replace("{player}", target)!!))

            val ban = TextComponent("[Ban] ")
            ban.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tempban ${targetPlayer.name} 14d Hacking")
            ban.color = ChatColor.RED
            ban.isBold = true

            val legit = TextComponent("[Legit]")
            legit.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/screenshare ${targetPlayer.name} legit")
            legit.color = ChatColor.AQUA
            legit.isBold = true

            sender.spigot().sendMessage(ban, legit)

            sender.sendMessage(color(DragonAPI().getLangFile().getString("messages.divider")!!))
            targetPlayer.sendMessage(defaultrgb(DragonAPI().getLangFile().getString("messages.screenshare")!!.replace("{staffer}", sender.name)))
            screenShareMap[targetPlayer] = sender.player!!

            sender.sendMessage(screenShareMap.entries.stream().map { e -> e.key.name + ": " + e.value.name }.collect(Collectors.toList()).joinToString(", ", "[", "]"))
            true
        } else {
            sender.sendMessage(defaultrgb(DragonAPI().getConfig().getString("messages.no-permission")!!))
            true
        }
    }
}