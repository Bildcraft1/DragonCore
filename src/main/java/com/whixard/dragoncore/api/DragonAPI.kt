@file:Suppress("KDocUnresolvedReference", "MemberVisibilityCanBePrivate")

package com.whixard.dragoncore.api

import com.whixard.dragoncore.Main
import com.whixard.dragoncore.api.manager.DragonManager
import com.whixard.dragoncore.events.NBTBlock
import com.whixard.dragoncore.format.Format
import jdk.jfr.Percentage
import me.lucko.spark.api.Spark
import me.lucko.spark.api.statistic.StatisticWindow
import me.lucko.spark.api.statistic.types.DoubleStatistic
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.RegisteredServiceProvider
import java.util.*
import kotlin.math.max


class DragonAPI {


    fun getBackEnd(): com.whixard.dragoncore.Main? {
        return DragonManager(com.whixard.dragoncore.Main.instance).getBackEnd()
    }

    fun getFormat(): Format {
        return Format
    }

    fun createGuiItem(material: Material?, name: String?, vararg lore: String?): ItemStack {
        val item = ItemStack(material!!, 1)
        val meta = item.itemMeta

        // Set the name of the item
        meta!!.setDisplayName(name)

        // Set the lore of the item
        meta.lore = Arrays.asList(*lore)
        item.itemMeta = meta
        return item
    }

    fun createGuiItemCustom(material: Material?, name: String?, vararg lore: String?): ItemStack {
        val item = ItemStack(material!!, 1)
        val meta = item.itemMeta

        // Set the name of the item
        meta!!.setDisplayName(name)

        // Set the lore of the item
        meta.lore = Arrays.asList(*lore)
        meta.setCustomModelData(Random().nextInt(100000))
        item.itemMeta = meta
        return item
    }

    fun getLangFile(): FileConfiguration {
        return com.whixard.dragoncore.Main.instance.messagefile
    }

    fun getConfig(): FileConfiguration {
        return com.whixard.dragoncore.Main.instance.config
    }

    fun getServerTPS() : DoubleStatistic<StatisticWindow.TicksPerSecond>? {
        return Bukkit.getServicesManager().getRegistration(Spark::class.java)?.provider?.tps()
    }


    fun getServerUsingRamPercentage() : Int { // from 0 to 100
        return (((Runtime.getRuntime().maxMemory()-Runtime.getRuntime().freeMemory()))/(Runtime.getRuntime().maxMemory())).toInt()
    }

    fun getPlugins(): List<String> {
        return com.whixard.dragoncore.Main.instance.server.pluginManager.plugins.map { it.name }
    }

    fun getPlayerPing(player: String): Int {
        return com.whixard.dragoncore.Main.instance.server.getPlayer(player)?.ping ?: 0
    }

    fun getServerVersion(): String {
        return com.whixard.dragoncore.Main.instance.server.version
    }

    fun getAuthor(): String {
        return com.whixard.dragoncore.Main.instance.description.authors[0]
    }

    fun getPluginVersion(): String {
        return com.whixard.dragoncore.Main.instance.description.version
    }

    fun getServerVersionName(): String {
        return com.whixard.dragoncore.Main.instance.server.version.split("MC: ")[1].split(")")[0]
    }

    fun getServerName(): String {
        return com.whixard.dragoncore.Main.instance.server.name
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