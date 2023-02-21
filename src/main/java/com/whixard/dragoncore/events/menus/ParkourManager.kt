package com.whixard.dragoncore.events.menus

import com.whixard.dragoncore.Main
import com.whixard.dragoncore.format.Format.color
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.*
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.event.player.*
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import java.time.LocalTime
import java.util.*

class ParkourManager : Listener {
    private val checkpoints = HashMap<Player, Location>()
    private val goingBackToCheckpoint = HashMap<Player, Boolean>()
    private val checkpointsCache = HashMap<Player, ArrayList<Location>>()
    private val winnerPosition = HashMap<Player, Int>()
    private val getWinner = HashMap<Int, Player>()
    private var winners = 0
    private var parkourWorld: World? = null
    var isParkourEventRunning = false
    var startingBossbar: BossBar?
    var parkourBossbar: BossBar

    init {
        if (Main.instance.config.contains("parkour_world_name")) parkourWorld = Objects.requireNonNull(
            Main.instance.config.getString("parkour_world_name")
        )?.let {
            Main.instance.server.getWorld(
                it
            )
        } else Main.instance.logger.info("Parkour world name not set up in the config.")
        for (p in Main.instance.server.onlinePlayers) {
            if (p.location.world == parkourWorld && !checkpointsCache.containsKey(p)) {
                checkpointsCache[p] = ArrayList()
            }
        }
        startingBossbar = Bukkit.createBossBar(
            color("&eEvento a Tempo &7- &d&lParkour &f- &fFai il comando &d/warp parkour&f per andarci!"),
            BarColor.PINK,
            BarStyle.SEGMENTED_10,
            BarFlag.DARKEN_SKY
        )
        parkourBossbar = Bukkit.createBossBar(
            color("&dParkour &f- &7Tempo rimanente"),
            BarColor.WHITE,
            BarStyle.SEGMENTED_20,
            BarFlag.CREATE_FOG
        )
        parkourBossbar.progress = 0.0
        parkourBossbar.isVisible = true
        startingBossbar!!.isVisible = false
        automaticParkourEventTimer()
    }

    private fun automaticParkourEventTimer() {
        object : BukkitRunnable() {
            override fun run() {
                if ((LocalTime.now().hour == 16 || LocalTime.now().hour == 18 || LocalTime.now().hour == 22) && !isParkourEventRunning && LocalTime.now().minute < 20) {
                    object : BukkitRunnable() {
                        override fun run() {
                            startParkourEvent()
                        }
                    }.runTaskAsynchronously(Main.instance)
                    isParkourEventRunning = true
                }
                if (isParkourEventRunning) {
                    val max = 20.0
                    val actual = LocalTime.now().minute.toDouble()
                    if (max - actual != 1.0) parkourBossbar.setTitle(color("&dParkour &f- &7Tempo rimanente - &b" + (max - actual).toInt() + "&f Minuti")) else parkourBossbar.setTitle(
                        color("&dParkour &f- &7Tempo rimanente - &b" + (max - actual).toInt() + "&f Minuto")
                    )
                    parkourBossbar.progress = 1 - actual / max
                    if (actual == max || ((LocalTime.now().hour == 16 || LocalTime.now().hour == 18 || LocalTime.now().hour == 22) && LocalTime.now().minute >= 20)) {
                        parkourBossbar.progress = 0.0
                        isParkourEventRunning = false
                        object : BukkitRunnable() {
                            override fun run() {
                                stopParkourEvent()
                            }
                        }.runTaskAsynchronously(Main.instance)
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.instance, 300, 1200) // Ogni minuto
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (event.player.location.world == parkourWorld && !checkpointsCache.containsKey(event.player)) {
            checkpointsCache[event.player] = ArrayList()
            if (event.player.allowFlight || event.player.isFlying) {
                event.player.allowFlight = false
                event.player.isFlying = false
            }
            if (!isParkourEventRunning && !event.player.hasPermission("dragoncore.parkour.cheatingprevention.bypass")) {
                Bukkit.dispatchCommand(event.player.server.consoleSender, "spawn " + event.player.name)
                event.player.sendTitle(
                    "",
                    color("&cEvento parkour terminato!"),
                    0,
                    100,
                    0
                ) // 5 Secondi | Considerare anche il tempo che il loro client faccia la transizione tra i mondi.
            }
        }
        if (event.player.location.world == parkourWorld && isParkourEventRunning) {
            parkourBossbar.addPlayer(event.player)
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        if (event.player.location.world == parkourWorld) {
            goingBackToCheckpoint.remove(event.player)
            parkourBossbar.removePlayer(event.player)
        }
    }

    fun startParkourEvent() {
        // MUST GIVE THEM THE PERMISSION TO GO TO THE WARP
        startingBossbar!!.isVisible = false
        parkourBossbar.progress = 1.0
        parkourBossbar.isVisible = true
        object : BukkitRunnable() {

            override fun run() {
                Bukkit.dispatchCommand(
                    Main.instance.server.consoleSender,
                    "lp group default permission set cmi.command.warp.parkour"
                )
            }

        }.runTask(Main.instance)

        for (p in Main.instance.server.onlinePlayers) {
            p.noDamageTicks = 260 // Diamogli tempo di leggere il titolo casomai sia in combattimento/pericolo.
            p.sendTitle(color("&d&lParkour"), color("&7Evento parkour avviato!"), 0, 200, 0)
            p.playSound(p, Sound.ENTITY_ENDER_DRAGON_AMBIENT, 60f, 1f)
            if (p.world == parkourWorld) {
                parkourBossbar.addPlayer(p)
            }
            startingBossbar!!.addPlayer(p)
        }
        startingBossbar!!.progress = 1.0
        startingBossbar!!.isVisible = true
        startStartingBossbar()
    }

    private fun startStartingBossbar() {
        object : BukkitRunnable() {
            override fun run() {
                if (startingBossbar == null) {
                    Main.instance.server.logger.warning("Cod. Errore #EF2G | Per qualche motivo la starting_bossbar non era stata instanziata durante l'evento Parkour")
                    cancel()
                }
                if (startingBossbar!!.progress >= 0.1) {
                    startingBossbar!!.progress = startingBossbar!!.progress - 0.05
                } else {
                    startingBossbar!!.progress = 0.0
                    object : BukkitRunnable() {
                        override fun run() {
                            startingBossbar!!.removeAll()
                            startingBossbar!!.isVisible = false
                        }
                    }.runTaskLater(Main.instance, 10)
                    cancel()
                }
            }
        }.runTaskTimerAsynchronously(Main.instance, 0, 20)
    }

    fun stopParkourEvent() {     // MUST REMOVE THEM THE PERMISSION

        object : BukkitRunnable(){

            override fun run() {
                Bukkit.dispatchCommand(
                    Main.instance.server.consoleSender,
                    "lp group default permission unset cmi.command.warp.parkour"
                )
            }

        }.runTask(Main.instance)



        //Bukkit.dispatchCommand(Main.instance.getServer().getConsoleSender(),"lp group default permission unset");
        for (p in parkourWorld!!.players) {

            //Bukkit.dispatchCommand(p.getServer().getConsoleSender(),"spawn "+p.getName());
            p.sendTitle(
                "",
                color("&cEvento parkour terminato!"),
                0,
                100,
                0
            ) // 5 Secondi | Considerare anche il tempo che il loro client faccia la transizione tra i mondi.

            object : BukkitRunnable(){

                override fun run() {
                    Bukkit.dispatchCommand(
                        Main.instance.server.consoleSender,
                        "spawn ${p.name}"
                    )
                }

            }.runTask(Main.instance)

        }
        if (winners != 0) {
            var title = color("&6&l1&6. &7no_first_winner")
            var subtitle = color("&e&l2&e. &7no_second_winner&f, &9&l3&9. &7no_third_winner")
            if (getWinner.containsKey(3)) {
                subtitle = subtitle.replace("no_third_winner".toRegex(), getWinner[3]!!.name)
            }
            if (getWinner.containsKey(2)) {
                subtitle = subtitle.replace("no_second_winner".toRegex(), getWinner[2]!!.name)
            }
            title = title.replace("no_first_winner".toRegex(), getWinner[1]!!.name)
            val finalTitle = title
            val finalSubtitle = subtitle
            object : BukkitRunnable() {
                var title = finalTitle
                var subtitle = finalSubtitle
                override fun run() {
                    for (p in Main.instance.server.onlinePlayers) {
                        p.noDamageTicks = 200
                        p.sendTitle(this.title, this.subtitle, 0, 160, 0)
                        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 100f, 1f)
                    }
                }
            }.runTaskLaterAsynchronously(Main.instance, 160)
        } else {
            object : BukkitRunnable() {
                override fun run() {
                    for (p in Main.instance.server.onlinePlayers) {
                        p.noDamageTicks = 200
                        p.sendTitle("", color("&cNessuno ha vinto durante l'evento &dParkour&c."), 0, 160, 0)
                    }
                }
            }.runTaskLaterAsynchronously(Main.instance, 160)
        }
        startingBossbar!!.isVisible = false
        parkourBossbar.removeAll()
        parkourBossbar.isVisible = false
        parkourBossbar.setTitle(color("&dParkour &f- &7Tempo rimanente"))
        winnerPosition.clear()
        getWinner.clear()
        checkpoints.clear()
        goingBackToCheckpoint.clear()
        checkpointsCache.clear()
        winners = 0
        winnerPosition.clear()
    }

    // PREVENTION FLY / ELYTRA / ENDERPEARL / CHORUS FRUIT
    @EventHandler
    fun playerCommand(event: PlayerCommandPreprocessEvent) {
        if (event.player.location.world == parkourWorld) {
            if (event.player.hasPermission("dragoncore.parkour.cheatingprevention.bypass")) return
            if (!event.message.contains("spawn")) {
                event.isCancelled = true
                event.player.sendTitle("", color("&cPuoi solo fare il comando &7/spawn&c qua!"), 0, 80, 0)
                event.player.playSound(event.player, Sound.ENTITY_WANDERING_TRADER_NO, 100f, 1f)
            }
        }
    }

    @EventHandler
    fun playerConsumePotionEvent(event: PlayerItemConsumeEvent){
        if(event.player.location.world == parkourWorld){
            event.isCancelled = true
        }
    }

    @EventHandler
    fun potionSplash(event: PotionSplashEvent){
        if(event.entity.location.world == parkourWorld){
            event.isCancelled = true
        }
    }

    @EventHandler
    fun WorldChangeEvent(event: PlayerChangedWorldEvent) {
        if (!checkpointsCache.containsKey(event.player)) {
            checkpointsCache[event.player] = ArrayList()
        } else {
            checkpointsCache.remove(event.player)
        }
        if (event.player.location.world == parkourWorld) {
            for(potion in event.player.activePotionEffects){
                event.player.removePotionEffect(potion.type)
            }
            parkourBossbar.addPlayer(event.player)
            if (event.player.hasPermission("dragoncore.parkour.flyprevention.bypass")) return
            if (event.player.allowFlight) {
                event.player.allowFlight = false
                event.player.isFlying = false
                event.player.playSound(event.player, Sound.ENTITY_VILLAGER_NO, 100f, 1f)
                event.player.sendTitle("", color("&cNon puoi volare qui!"), 0, 40, 0)
            }
        } else {
            parkourBossbar.removePlayer(event.player)
            checkpoints.remove(event.player)
            checkpointsCache.remove(event.player)
        }
    }

    @EventHandler
    fun PlayerTeleport(event: PlayerTeleportEvent) {
        if (event.player.location.world == parkourWorld) {
            if (event.player.hasPermission("dragoncore.parkour.flyprevention.bypass")) return
            if (event.cause == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT || event.cause == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
                event.isCancelled = true
                event.player.playSound(event.player, Sound.ENTITY_VILLAGER_NO, 100f, 1f)
                event.player.sendTitle("", color("&cNon puoi teletrasportarti qui!"), 0, 40, 0)
            }
        }
    }

    @EventHandler
    fun PlayerMove(event: PlayerMoveEvent) {
        if (!isParkourEventRunning) return
        if (event.player.location.world == parkourWorld) {
            if ((event.player.allowFlight || event.player.isFlying) && !event.player.hasPermission("dragoncore.parkour.flyprevention.bypass")) {
                event.player.allowFlight = false
                event.player.isFlying = false
                event.player.playSound(event.player, Sound.ENTITY_VILLAGER_NO, 100f, 1f)
                event.player.sendTitle("", color("&cNon puoi volare qui!"), 0, 40, 0)
            }
            val b = event.player.location.add(0.0, -1.0, 0.0).block
            if (b.type == Material.GOLD_BLOCK) {
                if (checkpoints.containsKey(event.player)) {
                    if (event.player.location != checkpoints[event.player]) {
                        val location_block_hipotetic = b.location
                        location_block_hipotetic.y = location_block_hipotetic.y + 1
                        if (!checkpointsCache[event.player]!!.contains(location_block_hipotetic)) {
                            event.player.sendTitle("", color("&2&lCheckpoint salvato!"), 0, 20, 0)
                            event.player.playSound(event.player, Sound.ENTITY_VILLAGER_YES, 100f, 1f)
                            val l = b.location
                            l.y = l.y + 1
                            checkpoints.replace(event.player, l)
                            checkpointsCache[event.player]!!.add(l)
                        }
                    }
                } else {
                    event.player.sendTitle("", color("&2&lCheckpoint salvato!"), 0, 20, 0)
                    event.player.playSound(event.player, Sound.ENTITY_VILLAGER_YES, 100f, 1f)
                    val l = b.location
                    l.y = l.y + 1
                    checkpoints[event.player] = l
                    checkpointsCache[event.player]!!.add(l)
                }
            }
            if (parkourWorld!!.getBlockAt(event.player.location).blockData.material == Material.WATER) {
                if (goingBackToCheckpoint.containsKey(event.player)) return
                if (!checkpoints.containsKey(event.player)) return
                goingBackToCheckpoint[event.player] = true
                event.player.playSound(event.player, Sound.ENTITY_GENERIC_EXPLODE, 60f, 1f)
                event.player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 10, 255, false))
                event.player.sendTitle("", color("&fTeletrasporto..."), 0, 13, 0)
                Main.instance.server.scheduler.scheduleSyncDelayedTask(
                    JavaPlugin.getPlugin(
                        Main::class.java
                    ), {
                        event.player.teleport(checkpoints[event.player]!!)
                        goingBackToCheckpoint.remove(event.player)
                        event.player.resetTitle()
                        event.player.sendTitle("", color("&2Tornato al checkpoint!"), 0, 10, 0)
                        event.player.spawnParticle(Particle.PORTAL, event.player.location, 100, 2.0, 2.0, 2.0)
                        event.player.playSound(event.player, Sound.ENTITY_ENDERMAN_TELEPORT, 100f, 1f)
                        event.player.removePotionEffect(PotionEffectType.SLOW)
                    }, 13L
                )
            }
            if (b.type == Material.EMERALD_BLOCK) {
                if (winnerPosition.containsKey(event.player)) {
                    return
                }
                val l = b.location
                l.y = l.y + 1
                checkpoints.replace(event.player, l)
                if (winners >= 3) {
                    event.player.sendTitle("", color("&2Bravo, hai completato il parkour!"), 0, 20, 0)
                    event.player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(color("&2+ &2&l500&2 Soldi")))
                    object : BukkitRunnable(){

                        override fun run() {
                            Bukkit.dispatchCommand(
                                event.player.server.consoleSender,
                                "cmi money give " + event.player.name + " 500"
                            )
                        }

                    }.runTaskLater(Main.instance, 40)

                    //cmi money give player tot
                    event.player.playSound(event.player, Sound.ENTITY_WANDERING_TRADER_YES, 100f, 1.3f)
                    winnerPosition[event.player] = -1 // -1 Non è un vincitore, ma non gli si manda il titolo a spam
                }
                when (winners) {
                    0 -> {
                        winners++
                        winnerPosition[event.player] = 1
                        getWinner[1] = event.player
                        event.player.sendTitle(color("&6&lPrimo"), color("&aCongratulazioni!"), 0, 40, 0)
                        for (p in parkourWorld!!.players) {
                            p.sendMessage(color("&7Il giocatore &b" + event.player.name + "&7 e' arrivato primo all'evento Parkour!"))
                        }
                        object: BukkitRunnable(){

                            override fun run() {
                                Bukkit.dispatchCommand(
                                    event.player.server.consoleSender,
                                    "cmi money give " + event.player.name + " 5000"
                                )
                            }

                        }.runTaskLater(Main.instance,40)

                        event.player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(color("&2+ &2&l5K&2 Soldi")))
                        event.player.playSound(event.player, Sound.ENTITY_PLAYER_LEVELUP, 100f, 2f)
                    }

                    1 -> {
                        winners++
                        winnerPosition[event.player] = 2
                        getWinner[2] = event.player
                        event.player.sendTitle(color("&6&lSecondo"), color("&aCongratulazioni!"), 0, 40, 0)
                        for (p in parkourWorld!!.players) {
                            p.sendMessage(color("&7Il giocatore &b" + event.player.name + "&7 e' arrivato secondo all'evento Parkour!"))
                        }
                        object : BukkitRunnable(){

                            override fun run() {
                                Bukkit.dispatchCommand(
                                    event.player.server.consoleSender,
                                    "cmi money give " + event.player.name + " 3000"
                                )
                            }

                        }.runTaskLater(Main.instance,40)

                        event.player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(color("&2+ &2&l3K&2 Soldi")))
                        event.player.playSound(event.player, Sound.ENTITY_PLAYER_LEVELUP, 100f, 1.3f)
                    }

                    2 -> {
                        winnerPosition[event.player] = 3
                        getWinner[3] = event.player
                        winners++
                        event.player.sendTitle(color("&6&lTerzo"), color("&aCongratulazioni!"), 0, 40, 0)
                        for (p in parkourWorld!!.players) {
                            p.sendMessage(color("&7Il giocatore &b" + event.player.name + "&7 e' arrivato terzo all'evento Parkour!"))
                        }
                        object : BukkitRunnable(){
                            override fun run() {
                                Bukkit.dispatchCommand(
                                    event.player.server.consoleSender,
                                    "cmi money give " + event.player.name + " 1000"
                                )
                            }
                        }.runTaskLater(Main.instance,40)
                        event.player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(color("&2+ &2&l1K&2 Soldi")))
                        event.player.playSound(event.player, Sound.ENTITY_PLAYER_LEVELUP, 100f, 0.7f)
                    }
                }
            }
        }
    }
}