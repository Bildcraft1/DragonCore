package fur.kiyoshi.dragoncore

import fur.kiyoshi.dragoncore.api.DragonAPI
import fur.kiyoshi.dragoncore.api.DragonCoreExpansion
import fur.kiyoshi.dragoncore.api.DragonDatabase
import fur.kiyoshi.dragoncore.api.manager.DragonManager
import fur.kiyoshi.dragoncore.commands.Freeze
import fur.kiyoshi.dragoncore.commands.chatfilter.ChatSettings
import fur.kiyoshi.dragoncore.commands.otherplugins.BloodMoonStatus
import fur.kiyoshi.dragoncore.commands.playercommands.Fly
import fur.kiyoshi.dragoncore.commands.playercommands.Heal
import fur.kiyoshi.dragoncore.commands.staffutils.ScreenShare
import fur.kiyoshi.dragoncore.commands.staffutils.Staff
import fur.kiyoshi.dragoncore.commands.staffutils.StaffList
import fur.kiyoshi.dragoncore.commands.staffutils.StaffMode
import fur.kiyoshi.dragoncore.commands.tags.Tags
import fur.kiyoshi.dragoncore.commands.teleport.Tp
import fur.kiyoshi.dragoncore.commands.testcommands.Mute
import fur.kiyoshi.dragoncore.commands.utility.Help
import fur.kiyoshi.dragoncore.commands.utility.Info
import fur.kiyoshi.dragoncore.commands.utility.Version
import fur.kiyoshi.dragoncore.events.*
import fur.kiyoshi.dragoncore.events.menus.TagsMenu
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.sql.Connection
import java.util.logging.Level


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
        if (!dataFolder.resolve("messages.yml").exists()) {
            saveResource("messages.yml", false)
        }
        saveResource("messages.yml", false)
        config.options().parseComments(true)
        config.options().copyDefaults(true)
    }

    var messagefile = YamlConfiguration.loadConfiguration(dataFolder.resolve("messages.yml"))

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
        this.registerEvent(ScreenShareEvent(), "ScreenShareEvent", this)
        if (DragonAPI().getConfig().getBoolean("functions.chat_filter")) {
            logger.log(Level.INFO, "[DragonCore] " + "ChatFilter is enabled")
            this.registerEvent(ChatFilter(), "ChatSettings", this)
        }
        if(DragonAPI().getConfig().getBoolean("functions.tag_system")) {
            logger.log(Level.INFO, "[DragonCore] " + "TagSystem is enabled")
            this.registerEvent(TagsMenu(), "TagsMenu", this)
        }
    }

    private fun commands() {
        getCommand("about")?.setExecutor(Info)
        getCommand("tp")?.setExecutor(Tp)
        getCommand("heal")?.setExecutor(Heal)
        getCommand("fly")?.setExecutor(Fly)
        getCommand("staff")?.setExecutor(Staff)
        getCommand("stafflist")?.setExecutor(StaffList)
        getCommand("staffmode")?.setExecutor(StaffMode)
        getCommand("version")?.setExecutor(Version)
        getCommand("info")?.setExecutor(Info)
        getCommand("help")?.setExecutor(Help)
        getCommand("bloodmoonstatus")?.setExecutor(BloodMoonStatus)
        getCommand("ss")?.setExecutor(ScreenShare)
        getCommand("screenshare")?.setExecutor(ScreenShare)
        getCommand("controllo")?.setExecutor(ScreenShare)
        getCommand("mute")?.setExecutor(Mute)
        getCommand("freeze")?.setExecutor(Freeze)
        getCommand("chatfilter")?.setExecutor(ChatSettings)
        getCommand("freeze")?.tabCompleter = TabHelper()
        getCommand("tags")?.setExecutor(Tags)
    }

    private fun asciiArt() {
        logger.log(Level.INFO, "\n")
        logger.log(Level.INFO,        "      ____                               ______")
        logger.log(Level.INFO,        "     / __ \\_________ _____ _____  ____  / ____/___  ________")
        logger.log(Level.INFO,        "    / / / / ___/ __ `/ __ `/ __ \\/ __ \\/ /   / __ \\/ ___/ _ \\")
        logger.log(Level.INFO,        "   / /_/ / /  / /_/ / /_/ / /_/ / / / / /___/ /_/ / /  /  __/")
        logger.log(Level.INFO,        "  /_____/_/   \\__,_/\\__, /\\____/_/ /_/\\____/\\____/_/   \\___/")
        logger.log(Level.INFO,        "                   /____/")
        logger.log(Level.INFO, "\n")
    }

    private val dbUrl = "jdbc:h2${dataFolder.absolutePath}/database"

    private fun database() {
        Class.forName("org.h2.Driver")
        DragonDatabase().initialiteDatabase()
    }

    fun getDatabaseUrl(): String {
        return dbUrl
    }

    override fun onEnable() {
        asciiArt()
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            // this returns a boolean, true if your placeholder is successfully registered, false if it isn't
            val expansion = DragonCoreExpansion()
            expansion.register()
        }
        instance()
        commands()
        events()
        // database()
    }

    override fun onDisable() {
        // Plugin shutdown logic
        DragonCoreExpansion().unregister()
        this.dragonManager = null
    }
}