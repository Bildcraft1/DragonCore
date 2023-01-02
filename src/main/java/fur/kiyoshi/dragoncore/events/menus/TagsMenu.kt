package fur.kiyoshi.dragoncore.events.menus

import fur.kiyoshi.dragoncore.api.DragonAPI
import fur.kiyoshi.dragoncore.api.DragonDatabase
import fur.kiyoshi.dragoncore.commands.tags.Tags
import fur.kiyoshi.dragoncore.commands.tags.Tags.tags
import fur.kiyoshi.dragoncore.format.Format.color
import org.bukkit.Material
import org.bukkit.Material.DIAMOND_SWORD
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
        var statement: PreparedStatement? = conn.prepareStatement(sql)
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

        if (clickedItem.type == DIAMOND_SWORD) {
            if (tags[p] == "Staff") {
                tags[p] = "None"
                Tags.inv!!.setItem(0, DragonAPI().createGuiItem(
                    Material.DIAMOND_SWORD,
                    "§bStaff Tag",
                    "§aFirst line of the lore",
                    "§bSecond line of the lore",
                    "§cNot Equipped"
                ))

                updateTags(p)
                p.sendMessage(color(DragonAPI().getLangFile().getString("tags.removed")?.replace("{tag}", "Staff")))
            } else {
                tags[p] = "Staff"
                Tags.inv!!.setItem(
                    0, DragonAPI().createGuiItem(
                    Material.DIAMOND_SWORD,
                    "§bStaff Tag",
                    "§aFirst line of the lore",
                    "§bSecond line of the lore",
                    "§aCurrently Equipped"
                ))
                updateTags(p)
                p.sendMessage(color(DragonAPI().getLangFile().getString("tags.added")?.replace("{tag}", "Staff")))
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