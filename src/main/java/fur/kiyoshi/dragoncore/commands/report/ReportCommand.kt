package fur.kiyoshi.dragoncore.commands.report

import fur.kiyoshi.dragoncore.api.ReportAPI
import fur.kiyoshi.dragoncore.format.Format.color
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object ReportCommand: CommandExecutor {
    val deletebutton = net.md_5.bungee.api.chat.TextComponent("[Delete] ")
    val checkbutton = net.md_5.bungee.api.chat.TextComponent("[Check] ")
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
                    sender.sendMessage("§7List of reports")
                        for (report in ReportAPI().getReports()) {
                            val status = if (report.status.toBoolean()) "§aStaff is checking" else "§cOpen"
                            sender.sendMessage(color("ID: §c${report.id} §7Player: §b${report.name} §7Reason: §c${report.reason} §7 Reported by: §b${report.reporter} §7Status: §c${status}"))
                            deletebutton.clickEvent = net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/report delete ${report.id}")
                            deletebutton.color = net.md_5.bungee.api.ChatColor.RED
                            deletebutton.isBold = true

                            checkbutton.clickEvent = net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/report check ${report.id}")
                            checkbutton.color = net.md_5.bungee.api.ChatColor.GREEN
                            checkbutton.isBold = true
                            sender.spigot().sendMessage(deletebutton, checkbutton)
                        }
                    return true
                }
                sender.sendMessage("Please specify a reason to report")
                return true
            }
            if (args.size >= 2) {
                if (args[0] == "delete") {
                    if (args[1].toIntOrNull() == null) {
                        sender.sendMessage("Please specify a report ID to delete")
                        return true
                    }
                    if (args.size == 2) {
                        ReportAPI().deleteReport(args[1].toInt())
                        sender.sendMessage("Report deleted")
                        return true
                    }
                    return true
                }

                if (args[0] == "check") {
                    if (args[1].toIntOrNull() == null) {
                        sender.sendMessage("Please specify a report ID to check")
                        return true
                    }
                    if (args.size == 2) {
                        val report = ReportAPI().getReport(args[1].toInt())

                        if (ReportAPI().checkReport(args[1].toInt())) {
                            sender.sendMessage("Report is already checked")
                            return true
                        }

                        ReportAPI().setReport(args[1].toInt(), true)
                        sender.sendMessage("§7Report ID: §c${report.id} §7Player: §b${report.name} §7Reason: §c${report.reason} §7 Reported by: §b${report.reporter}")
                        return true
                    }
                    return true
                }

                if (sender.server.getPlayer(args[0]) == null) {
                    sender.sendMessage("Player not found")
                    return true
                }
                val player = sender.server.getPlayer(args[0])
                val reason = args[1]
                sender.sendMessage("You have reported ${player?.name} for $reason")
                if (player != null) {
                    ReportAPI().createReport(player, reason, sender, false)
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