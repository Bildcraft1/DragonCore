package fur.kiyoshi.dragoncore.events

import fur.kiyoshi.dragoncore.api.DragonAPI
import fur.kiyoshi.dragoncore.format.Format
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
                        val mute = net.md_5.bungee.api.chat.TextComponent("[Muta] ")
                        mute.clickEvent = net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, "/mute ${p.name} 20m")
                        mute.color = net.md_5.bungee.api.ChatColor.RED
                        mute.isBold = true
                        mute.hoverEvent = net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, net.md_5.bungee.api.chat.ComponentBuilder("Mute ${p.name} for 20 minutes").create())

                        player.sendMessage(Format.rgb(153, 0, 70, "Player ${p.name} said " + "&a${msg}"))
                        player.spigot().sendMessage(mute)
                    }
                }
                event.setCancelled(true)
                p.sendMessage(defaultrgb("You are not allowed to say that!") + Format.rgb(178, 34, 34, " (Chat Filter)"))
            }
        }

    }
}