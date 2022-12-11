package fur.kiyoshi.dragoncore

import fur.kiyoshi.dragoncore.api.manager.DragonManager
import fur.kiyoshi.dragoncore.commands.utility.Info
import fur.kiyoshi.dragoncore.commands.funcommands.Horse
import fur.kiyoshi.dragoncore.commands.playercommands.Fly
import fur.kiyoshi.dragoncore.commands.playercommands.Heal
import fur.kiyoshi.dragoncore.commands.staffmode.Staff
import fur.kiyoshi.dragoncore.commands.staffmode.StaffList
import fur.kiyoshi.dragoncore.commands.staffmode.StaffMode
import fur.kiyoshi.dragoncore.commands.teleport.Tp
import fur.kiyoshi.dragoncore.commands.utility.Help
import fur.kiyoshi.dragoncore.events.NBTBlock
import fur.kiyoshi.dragoncore.events.PlayerJoin
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin


class Main : JavaPlugin() {

    companion object {
        @JvmStatic
        lateinit var instance: Main
    }

    private var dragonManager: DragonManager? = null

    @Suppress("SameParameterValue")
    private fun instance(){
        instance = this
        this.dragonManager = DragonManager(this)
        saveDefaultConfig()
        config.options().parseComments(true)
        config.options().copyDefaults(true)
    }

    @Suppress("SameParameterValue")
    private fun registerEvent(pm: Listener, event: String, jp: JavaPlugin) {
        try {
            jp.server.pluginManager.registerEvents(pm, this)
        } catch (e: NullPointerException) {
            jp.logger.warning(String.format(event, e.message))
        }
    }

    private fun events() {
        this.registerEvent(NBTBlock(), "NBTBlock", this)
        this.registerEvent(PlayerJoin(), "PlayerJoin", this)
    }

    private fun commands() {
        getCommand("about")?.setExecutor(Info)
        getCommand("tp")?.setExecutor(Tp)
        getCommand("heal")?.setExecutor(Heal)
        getCommand("fly")?.setExecutor(Fly)
        getCommand("staff")?.setExecutor(Staff)
        getCommand("stafflist")?.setExecutor(StaffList)
        getCommand("staffmode")?.setExecutor(StaffMode)
        getCommand("horse")?.setExecutor(Horse)
        getCommand("info")?.setExecutor(Info)
        getCommand("help")?.setExecutor(Help)
    }

    override fun onEnable() {
        instance()
        commands()
        events()
    }

    override fun onDisable() {
        this.dragonManager = null
    }
}