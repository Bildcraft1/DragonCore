package fur.kiyoshi.dragoncore.events

import fur.kiyoshi.dragoncore.api.DragonAPI
import fur.kiyoshi.dragoncore.format.Format
import fur.kiyoshi.dragoncore.format.Format.color
import fur.kiyoshi.dragoncore.format.Format.defaultrgb
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class ChatFilter: Listener {

    @EventHandler
    fun onChatFilter(event: AsyncPlayerChatEvent) {
        var blacklist: MutableList<String> = arrayListOf<String>()
        val p: Player = event.getPlayer()
        val msg: String = event.getMessage()
        blacklist.addAll((DragonAPI().getConfig().getStringList("chatfilter.blacklist")))

        for (x in blacklist) {
            if (msg.contains(x)) {

                for (player in p.server.onlinePlayers) {
                    if (player.hasPermission("dragoncore.staff")) {
                        val mute = net.md_5.bungee.api.chat.TextComponent("Mute ")
                        mute.clickEvent = net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, "/mute ${p.name} 20m")
                        mute.color = net.md_5.bungee.api.ChatColor.RED
                        mute.isBold = true

                        val kick = net.md_5.bungee.api.chat.TextComponent("Kick ")
                        kick.clickEvent = net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, "/kick ${p.name} Chat Filter")
                        kick.color = net.md_5.bungee.api.ChatColor.RED
                        kick.isBold = true

                        val ban = net.md_5.bungee.api.chat.TextComponent("Ban ")
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