package fur.kiyoshi.dragoncore.events.menus

import fur.kiyoshi.dragoncore.commands.tags.Tags
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent


class TagsMenu: Listener {

    // Check for clicks on items
    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.inventory != Tags.inv) return
        e.isCancelled = true
        val clickedItem = e.currentItem

        // verify current item is not null
        if (clickedItem == null || clickedItem.type.isAir) return
        val p = e.whoClicked as Player

        // Using slots click is a best option for your inventory click's
        p.sendMessage("You clicked at slot " + e.rawSlot)
    }

    // Cancel dragging in our inventory
    @EventHandler
    fun onInventoryClick(e: InventoryDragEvent) {
        if (e.inventory == Tags.inv) {
            e.isCancelled = true
        }
    }
}