package com.whixard.dragoncore

import com.whixard.dragoncore.api.DragonAPI
import com.whixard.dragoncore.api.DragonCoreExpansion
import com.whixard.dragoncore.api.DragonDatabase
import com.whixard.dragoncore.api.manager.DragonManager
import com.whixard.dragoncore.commands.TimeTravelCommand
import com.whixard.dragoncore.commands.chatfilter.ChatSettings
import com.whixard.dragoncore.commands.otherplugins.BloodMoonStatus
import com.whixard.dragoncore.commands.playercommands.Fly
import com.whixard.dragoncore.commands.playercommands.Heal
import com.whixard.dragoncore.commands.report.ReportCommand
import com.whixard.dragoncore.commands.staffutils.*
import com.whixard.dragoncore.commands.tags.Tags
import com.whixard.dragoncore.commands.teleport.Tp
import com.whixard.dragoncore.commands.testcommands.Freeze
import com.whixard.dragoncore.commands.utility.Help
import com.whixard.dragoncore.commands.utility.Info
import com.whixard.dragoncore.commands.utility.ReloadConfig
import com.whixard.dragoncore.commands.utility.Version
import com.whixard.dragoncore.events.*
import com.whixard.dragoncore.events.externalplugins.CheckLands
import com.whixard.dragoncore.events.menus.TagsMenu
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level


class Main : JavaPlugin() {

    companion object {
        @JvmStatic
        lateinit var instance: Main
    }

    private var dragonManager: DragonManager? = null

    @Suppress("SameParameterValue")
    private fun instance() {
        instance = this
        this.dragonManager = DragonManager(this)
        saveDefaultConfig()
        if (!dataFolder.resolve("messages.yml").exists()) {
            saveResource("messages.yml", false)
        }
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
         // this.registerEvent(NBTBlock(), "NBTBlock", this)
        this.registerEvent(PlayerJoin(), "PlayerJoin", this)
        this.registerEvent(ScreenShareEvent(), "ScreenShareEvent", this)
        this.registerEvent(DeathMessage(), "DeathMessage", this)

        this.registerEvent(NewPlayer(), "NewPlayer", this)

        if (DragonAPI().getConfig().getBoolean("functions.chat_filter")) {
            logger.log(Level.INFO, "ChatFilter is enabled")
            this.registerEvent(ChatFilter(), "ChatSettings", this)
            this.registerEvent(AntiPluginsDumper(), "AntiPluginsDumper", this)
        }

        if (DragonAPI().getConfig().getBoolean("functions.tag_system")) {
            logger.log(Level.INFO, "TagSystem is enabled")
            this.registerEvent(TagsMenu(), "TagsMenu", this)
        }
        TopLeaderboard().runTaskTimer(this, 0, 12000)

        if (DragonAPI().getConfig().getBoolean("statistics_api.enabled")) {
            DragonStatistics().runTaskTimer(this, 0, 12000)
            logger.log(Level.INFO, "Statistics are enabled with url: ${DragonAPI().getConfig().getString("statistics_api.url")}")
        } else {
            logger.log(Level.INFO, "Statistics are disabled.")
        }

        if(Bukkit.getPluginManager().isPluginEnabled("Lands")) {
            logger.log(Level.INFO, "Enabling scoreboard checking for Lands plugin")
            CheckLands().runTaskTimer(this, 0, 12000)
        } else {
            logger.log(Level.INFO, "Lands not found, disabling tab checker.")
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
        getCommand("freeze")?.setExecutor(Freeze)
        getCommand("chatfilter")?.setExecutor(ChatSettings)
        getCommand("tags")?.setExecutor(Tags)
        getCommand("reload")?.setExecutor(ReloadConfig)
        getCommand("report")?.setExecutor(ReportCommand)
        getCommand("randomtp")?.setExecutor(RandomTp)
        getCommand("timetravel")?.setExecutor(TimeTravelCommand)
    }

    private fun tabCompleters() {
        getCommand("freeze")?.tabCompleter = TabHelper()
        getCommand("tags")?.tabCompleter = TabHelper()
    }

    private fun asciiArt() {
        logger.log(Level.INFO, "      ____                               ______")
        logger.log(Level.INFO, "     / __ \\_________ _____ _____  ____  / ____/___  ________")
        logger.log(Level.INFO, "    / / / / ___/ __ `/ __ `/ __ \\/ __ \\/ /   / __ \\/ ___/ _ \\")
        logger.log(Level.INFO, "   / /_/ / /  / /_/ / /_/ / /_/ / / / / /___/ /_/ / /  /  __/")
        logger.log(Level.INFO, "  /_____/_/   \\__,_/\\__, /\\____/_/ /_/\\____/\\____/_/   \\___/")
        logger.log(Level.INFO, "                   /____/")
    }

    fun database() {
        if (DragonAPI().getConfig().getBoolean("functions.database")) {
            logger.log(Level.INFO, "[DragonCore] " + "Database is enabled")
            DragonDatabase().getConnection()
            DragonDatabase().initializeDatabase()
            Tags.loadAllTags()
        }
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
        tabCompleters()
        events()
        database()
    }

    override fun onDisable() {
        // Plugin shutdown logic
        DragonCoreExpansion().unregister()
        this.dragonManager = null
    }
}