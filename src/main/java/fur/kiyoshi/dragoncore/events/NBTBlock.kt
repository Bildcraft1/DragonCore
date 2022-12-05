package fur.kiyoshi.dragoncore.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareSmithingEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.SmithingInventory
import org.bukkit.persistence.PersistentDataContainer


@Suppress("MemberVisibilityCanBePrivate")
class NBTBlock : Listener {

    var result: ItemStack? = null
    var inventory: Inventory? = null
    var firstItem: ItemStack? = null
    var secondItem: ItemStack? = null
    var firstItemNBT: PersistentDataContainer? = null
    var secondItemNBT: PersistentDataContainer? = null
    var resultNBT: PersistentDataContainer? = null


    @EventHandler
    fun onSmithingPrepare(event: PrepareSmithingEvent) {

        result = event.result
        inventory = event.inventory
        firstItem = (inventory as SmithingInventory).getItem(0)
        secondItem = (inventory as SmithingInventory).getItem(1)
        firstItemNBT = firstItem?.itemMeta?.persistentDataContainer
        secondItemNBT = secondItem?.itemMeta?.persistentDataContainer
        resultNBT = result?.itemMeta?.persistentDataContainer

        if (firstItem?.itemMeta?.customModelData != null) {
            event.result = null
        }

    }

}