@file:Suppress("KDocUnresolvedReference", "MemberVisibilityCanBePrivate")

package fur.kiyoshi.dragoncore.api

import fur.kiyoshi.dragoncore.Main
import fur.kiyoshi.dragoncore.api.manager.DragonManager
import fur.kiyoshi.dragoncore.events.NBTBlock
import fur.kiyoshi.dragoncore.format.Format
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

class DragonAPI {

    fun getBackEnd(): Main? {
        return DragonManager(Main.instance).getBackEnd()
    }

    fun getFormat(): Format {
        return Format
    }

    fun createGuiItem(material: Material?, name: String?, vararg lore: String?): ItemStack? {
        val item = ItemStack(material!!, 1)
        val meta = item.itemMeta

        // Set the name of the item
        meta!!.setDisplayName(name)

        // Set the lore of the item
        meta.lore = Arrays.asList(*lore)
        item.itemMeta = meta
        return item
    }

    fun getLangFile(): FileConfiguration {
        return Main.instance.messagefile
    }

    fun getConfig(): FileConfiguration {
        return Main.instance.config
    }

    fun getPlugins(): List<String> {
        return Main.instance.server.pluginManager.plugins.map { it.name }
    }

    fun getPlayerPing(player: String): Int {
        return Main.instance.server.getPlayer(player)?.ping ?: 0
    }

    fun getServerVersion(): String {
        return Main.instance.server.version
    }

    fun getAuthor(): String {
        return Main.instance.description.authors[0]
    }

    fun getPluginVersion(): String {
        return Main.instance.description.version
    }

    fun getServerVersionName(): String {
        return Main.instance.server.version.split("MC: ")[1].split(")")[0]
    }

    fun getServerName(): String {
        return Main.instance.server.name
    }

    /**
     * @author Kiyoshi
     * @param itemStack
     * @return
     * @since 1.0.0
     * @deprecated
     * @see getNBT
     */
    fun getResult(): ItemStack? {
        return NBTBlock().result
    }

    /**
     * @author Kiyoshi
     * @param inventory
     * @return
     * @since 1.0.0
     * @deprecated
     * @see getInventory
     */
    fun getInventory(): Inventory? {
        return NBTBlock().inventory
    }

    /**
     * @author Kiyoshi
     * @param itemStack
     * @return
     * @since 1.0.0
     * @deprecated
     * @see getFirstItem
     */
    fun getfirstItem(): ItemStack? {
        return NBTBlock().firstItem
    }

    /**
     * @author Kiyoshi
     * @param itemStack
     * @return
     * @since 1.0.0
     * @deprecated
     * @see getSecondItem
     */
    fun getSecondItem(): ItemStack? {
        return NBTBlock().secondItem
    }

    /**
     * @author Kiyoshi
     * @param itemStack
     * @return
     * @since 1.0.0
     * @deprecated
     * @see getFirstItemNBT
     */
    fun getfirstItemNBT(): ItemStack? {
        return NBTBlock().firstItem
    }

    /**
     * @author Kiyoshi
     * @param itemStack
     * @return
     * @since 1.0.0
     * @deprecated
     * @see getSecondItemNBT
     */
    fun getsecondItemNBT(): ItemStack? {
        return NBTBlock().secondItem
    }

    /**
     * @author Kiyoshi
     * @param itemStack
     * @return
     * @since 1.0.0
     * @deprecated
     * @see getResultNBT
     */
    fun getresultNBT(): ItemStack? {
        return NBTBlock().result
    }


}