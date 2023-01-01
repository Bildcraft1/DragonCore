package fur.kiyoshi.dragoncore.events.menus

import fur.kiyoshi.dragoncore.commands.tags.Tags
import fur.kiyoshi.dragoncore.commands.tags.Tags.userTags
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack


class TagsMenu: Listener {

    // Check for clicks on items
    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.inventory != Tags.inv) return
        e.isCancelled = true
        val clickedItem = e.currentItem

        // verify current item is not null
        if ((clickedItem == null) || clickedItem.type.isAir) return
        val p = e.whoClicked as Player

        if (clickedItem == ItemStack(Material.DIAMOND_SWORD)) {
            if (userTags[p] == "Staff") {
                userTags[p] = "None"
                p.sendMessage("§aYou have unequipped your Staff Tag")
            } else {
                userTags[p] = "Staff"
                p.sendMessage("§aYou have equipped your Staff Tag")
            }
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