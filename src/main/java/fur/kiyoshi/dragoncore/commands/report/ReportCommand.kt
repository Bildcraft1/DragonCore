package fur.kiyoshi.dragoncore.commands.report

import fur.kiyoshi.dragoncore.api.ReportAPI
import fur.kiyoshi.dragoncore.format.Format.color
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object ReportCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender !is Player) {
            sender.sendMessage("Sender is not a player")
            return true
        }

        if (sender.hasPermission("dragoncore.report")) {
            if (args.isEmpty()) {
                sender.sendMessage("Please specify a player to report")
                return true
            }
            if (args.size == 1) {
                if (args[0].equals("list", true)) {
                    sender.sendMessage("List of reports")
                    var i = 0
                    while (i <= 10) {
                        for (report in ReportAPI().getReports()) {
                            sender.sendMessage(color("§7Player: §b${report.name} §7Reason: §c${report.reason} §7 Reported by: §b${report.reporter}"))
                        }
                        i++
                    }
                    return true
                }
                sender.sendMessage("Please specify a reason to report")
                return true
            }
            if (args.size >= 2) {
                if (sender.server.getPlayer(args[0]) == null) {
                    sender.sendMessage("Player not found")
                    return true
                }
                val player = sender.server.getPlayer(args[0])
                val reason = args[1]
                sender.sendMessage("You have reported ${player?.name} for $reason")
                if (player != null) {
                    ReportAPI().createReport(player, reason, sender)
                    ReportAPI().sendDiscordReport(player, reason, sender)
                }
                return true
            }
        } else if (!sender.hasPermission("dragoncore.report")) {
            sender.sendMessage("You do not have permission to use this command")
            return true
        }
        return true
    }
}