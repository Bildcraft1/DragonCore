package fur.kiyoshi.dragoncore.events.menus

import fur.kiyoshi.dragoncore.api.DragonAPI
import fur.kiyoshi.dragoncore.api.DragonDatabase
import fur.kiyoshi.dragoncore.commands.tags.Tags
import fur.kiyoshi.dragoncore.commands.tags.Tags.tags
import fur.kiyoshi.dragoncore.format.Format.color
import org.bukkit.Material
import org.bukkit.Material.DIAMOND_SWORD
import org.bukkit.Material.NETHERITE_INGOT
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import java.sql.Connection
import java.sql.PreparedStatement


class TagsMenu: Listener {

    fun updateTags(player: Player) {
        val sql = "UPDATE dragoncore SET tags = ? WHERE name = ?"
        val conn: Connection = DragonDatabase().getConnection()
        val statement: PreparedStatement? = conn.prepareStatement(sql)
        statement?.setString(1, tags[player])
        statement?.setString(2, player.name)
        statement?.executeUpdate()
    }

    // Check for clicks on items
    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.inventory != Tags.inv) return
        e.isCancelled = true
        val clickedItem = e.currentItem

        // verify current item is not null
        if ((clickedItem == null) || clickedItem.type.isAir) return
        val p = e.whoClicked as Player

        for (item in DragonAPI().getConfig().getConfigurationSection("tags")!!.getKeys(false)) {
            if (clickedItem.itemMeta!!.displayName == color(DragonAPI().getConfig().getString("tags.$item.name"))) {
                if (p.hasPermission("dragoncore.tags.${DragonAPI().getConfig().getString("tags.$item")}")) {
                    if (tags[p] == item) {
                        p.sendMessage(color("&cYou already have this tag"))
                        return
                    }
                    tags[p] = item
                    updateTags(p)
                    p.sendMessage(color("&aYou have successfully changed your tag to &b$item"))
                    p.closeInventory()
                } else {
                    tags[p] = "None"
                    p.sendMessage(color("&cYou don't have the permission to use this tag"))
                }
            }
        }

        if (clickedItem.type == Material.BARRIER) {
            tags[e.whoClicked as Player] = "None"
            updateTags(e.whoClicked as Player)
            p.closeInventory()
        }

    }

    // Cancel dragging in our inventory
    @EventHandler
    fun onInventoryClick(e: InventoryDragEvent) {
        if (e.inventory == Tags.inv) {
            e.isCancelled = true
        }
    }
}