package com.whixard.dragoncore

import com.whixard.dragoncore.api.DragonAPI
import com.whixard.dragoncore.api.DragonCoreExpansion
import com.whixard.dragoncore.api.DragonDatabase
import com.whixard.dragoncore.api.manager.DragonManager
import com.whixard.dragoncore.commands.TimeTravelCommand
import com.whixard.dragoncore.commands.TimeTravelTabCompleter
import com.whixard.dragoncore.commands.chatfilter.ChatSettings
import com.whixard.dragoncore.commands.otherplugins.BloodMoonStatus
import com.whixard.dragoncore.commands.playercommands.Fly
import com.whixard.dragoncore.commands.playercommands.Friends
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
import com.whixard.dragoncore.events.menus.ParkourManager
import com.whixard.dragoncore.events.menus.TagsMenu
import com.whixard.dragoncore.events.resourcesMonitoring.RM_Events
import com.whixard.dragoncore.events.resourcesMonitoring.RM_Generic
import com.whixard.dragoncore.format.Format
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level


class Main : JavaPlugin() {

    companion object {
        @JvmStatic
        lateinit var instance: Main
    }

    var TPS_Alert_BossBar: BossBar? = null
    var RAM_Alert_BossBar: BossBar? = null
    var is_resources_alert_running: Boolean = false

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
        this.registerEvent(PlayerJoin(), "PlayerJoin", this)
        this.registerEvent(ScreenShareEvent(), "ScreenShareEvent", this)
        this.registerEvent(DeathMessage(), "DeathMessage", this)

        this.registerEvent(NewPlayer(), "NewPlayer", this)
        this.registerEvent(AdvancementsMessages(), "AdvancementsMessages", this)

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
            logger.log(
                Level.INFO,
                "Statistics are enabled with url: ${DragonAPI().getConfig().getString("statistics_api.url")}"
            )
        } else {
            logger.log(Level.INFO, "Statistics are disabled.")
        }

        if (Bukkit.getPluginManager().isPluginEnabled("Lands")) {
            logger.log(Level.INFO, "Enabling scoreboard checking for Lands plugin")
            CheckLands().runTaskTimer(this, 0, 12000)
        } else {
            logger.log(Level.INFO, "Lands not found, disabling tab checker.")
        }

        if (config.contains("parkour_world_name")) {
            server.pluginManager.registerEvents(ParkourManager(), this)
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
        getCommand("friends")?.setExecutor(Friends)
    }

    private fun betaFeatures() {
        if (DragonAPI().getConfig().getBoolean("beta_features")) {
            if (config.contains("alerts.tps-when-under") && config.contains("alerts.ram-when-over")) {

                server.pluginManager.registerEvents(RM_Events(), this)

            }

            if (config.contains("alerts.tps-when-under") && config.contains("alerts.ram-when-over")) {

                RM_Generic().runTaskTimerAsynchronously(this, 0, 600)
                //RAM_Alert_BossBar = Bukkit.createBossBar(Format.color("&fRAM USAGE: &c?%"), BarColor.RED, BarStyle.SOLID, BarFlag.DARKEN_SKY)
                TPS_Alert_BossBar =
                    Bukkit.createBossBar(Format.color("&fTPS: &c?"), BarColor.RED, BarStyle.SOLID, BarFlag.DARKEN_SKY)
                //RAM_Alert_BossBar!!.isVisible = false
                TPS_Alert_BossBar!!.isVisible = false

            } else {
                server.logger.warning("Alerts are disabled, to enable them insert the correct values in the config.yml")
            }

            logger.log(Level.INFO, "Beta features are enabled")
            getCommand("timetravel")?.setExecutor(TimeTravelCommand)
            getCommand("timetravel")?.tabCompleter = TimeTravelTabCompleter

        } else {
            logger.log(Level.INFO, "Beta features are disabled")
        }
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
        betaFeatures()
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