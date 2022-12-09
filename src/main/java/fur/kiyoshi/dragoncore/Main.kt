package fur.kiyoshi.dragoncore

import fur.kiyoshi.dragoncore.api.manager.DragonManager
import fur.kiyoshi.dragoncore.commands.Info
import fur.kiyoshi.dragoncore.commands.playercommands.Fly
import fur.kiyoshi.dragoncore.commands.playercommands.Heal
import fur.kiyoshi.dragoncore.events.NBTBlock
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
        getCommand("about")?.setExecutor(Info)
        getCommand("heal")?.setExecutor(Heal)
        getCommand("fly")?.setExecutor(Fly)
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
    }

    override fun onEnable() {
        instance()
        events()
    }

    override fun onDisable() {
        this.dragonManager = null
    }
}