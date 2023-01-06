package fur.kiyoshi.dragoncore.events

import fur.kiyoshi.dragoncore.api.DragonAPI
import fur.kiyoshi.dragoncore.format.Format.color
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class ChatFilter: Listener {
    var prefix = color("&c&lDragonCore >> &7")

    @EventHandler
    fun onChatFilter(event: AsyncPlayerChatEvent) {
        var blacklist: MutableList<String> = arrayListOf<String>()
        val p: Player = event.player
        val msg: String = event.message.lowercase()
        blacklist.addAll((DragonAPI().getConfig().getStringList("chatfilter.blacklist")))

        if (msg.matches("^<.*>.*$".toRegex())) {
            for (player in p.server.onlinePlayers) {
                if (player.hasPermission("dragoncore.staff")) {
                    val teleport = TextComponent(prefix + color(" User &7") + p.name + color(" &ctried to force op!"))
                    teleport.clickEvent =
                        ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp " + p.name)
                    player.spigot().sendMessage(teleport)
                }
            }
            event.isCancelled = true
        }

        if (msg.matches(".*\\{jndi:ldap://.*}.*".toRegex())) {
            for (player in p.server.onlinePlayers) {
                if (player.hasPermission("dragoncore.staff")) {
                    val teleport = TextComponent(prefix + "User §7" + p.name + " §ctried to Log4Shell!")
                    teleport.clickEvent =
                        ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp " + p.name)
                    player.spigot().sendMessage(teleport)
                }
            }
            event.isCancelled = true
        }

        for (x in blacklist) {
            if (msg.contains(x)) {

                for (player in p.server.onlinePlayers) {
                    if (player.hasPermission("dragoncore.staff")) {
                        val mute = net.md_5.bungee.api.chat.TextComponent("[Mute] ")
                        mute.clickEvent = net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, "/tempmute ${p.name} 20m")
                        mute.color = net.md_5.bungee.api.ChatColor.RED
                        mute.isBold = true

                        val kick = net.md_5.bungee.api.chat.TextComponent("[Kick] ")
                        kick.clickEvent = net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, "/kick ${p.name} Chat Filter")
                        kick.color = net.md_5.bungee.api.ChatColor.RED
                        kick.isBold = true

                        val ban = net.md_5.bungee.api.chat.TextComponent("[Ban] ")
                        ban.clickEvent = net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, "/ban ${p.name} Chat Filter")
                        ban.color = net.md_5.bungee.api.ChatColor.RED
                        ban.isBold = true
                        player.sendMessage(color(DragonAPI().getLangFile().getString("messages.chatfiler_notification")))
                        player.sendMessage(color(
                            DragonAPI().getLangFile().getString("messages.chatfilter_blocked_notify")
                                ?.replace("{player}", p.name)
                                ?.replace("{message}", msg)))
                        player.spigot().sendMessage(mute, kick, ban)
                        player.sendMessage(color(DragonAPI().getLangFile().getString("messages.divider")))
                    }
                }

                event.isCancelled = true
                p.sendMessage(color(
                    DragonAPI().getLangFile().getString("messages.chatfilter_blocked")
                        ?.replace("{message}", msg)))
            }
        }

    }
}