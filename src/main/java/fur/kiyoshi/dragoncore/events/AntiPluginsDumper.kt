package fur.kiyoshi.dragoncore.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;

import static fur.kiyoshi.dragoncore.format.Format.color;

public class AntiPluginsDumper implements Listener {
    String prefix = color("&c&lDragonCore >> &7");

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onPluginsDumper(PlayerCommandPreprocessEvent e) {
        String message = e.getMessage().toLowerCase();
        Player player = e.getPlayer();
        if (message.contains("bukkit:ver") || message.contains("pl") || message.contains("plugins") || message.contains("bukkit:pl") || message.contains("bukkit:plugins") || message.contains("bukkit:version") || message.contains("ver") || message.contains("version") && !player.hasPermission("dragoncore.staff")) {
            player.getServer().broadcast(prefix + color("&c" + player.getName() + " has been kicked for using a plugins dumper!"), "dragoncore.notify");
            player.kickPlayer(color("&cYou have been kicked for using a plugins dumper!"));
            e.setCancelled(true);
            e.getPlayer().sendMessage(prefix + color("&cPlugins dumpers are disabled!"));
        }
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onTabComplete(TabCompleteEvent e) {
        String message = e.getBuffer().toLowerCase();
        Player player = e.getSender().getServer().matchPlayer(e.getSender().getName()).get(0);
        if (message.contains("bukkit:ver") && !player.hasPermission("dragoncore.staff")) {
            player.getServer().broadcast(prefix + color("&c" + player.getName() + " has been kicked for using a plugins dumper!"), "dragoncore.notify");
            player.kickPlayer(color("&cYou have been kicked for using a plugins dumper!"));
            e.setCancelled(true);
            e.getSender().sendMessage(prefix + color("&cPlugins dumpers are disabled!"));
        }
    }
}
