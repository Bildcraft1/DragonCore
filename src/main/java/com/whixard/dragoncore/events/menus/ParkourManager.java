package com.whixard.dragoncore.events.menus;
import com.whixard.dragoncore.Main;
import com.whixard.dragoncore.format.Format;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class ParkourManager implements Listener {


    private HashMap<Player,Location> checkpoints = new HashMap<>();
    private HashMap<Player,Boolean> going_back_to_checkpoint = new HashMap<>();
    private HashMap<Player, ArrayList<Location>> checkpoints_cache = new HashMap<>();
    private HashMap<Player,Integer> winner_position = new HashMap<>();

    private HashMap<Integer,Player> get_winner = new HashMap<>();

    private int winners = 0;

    World parkour_world;

    boolean is_parkour_event_running = false;

    public BossBar starting_bossBar;

    public BossBar parkour_bossBar;

    public ParkourManager(){

        if(Main.instance.getConfig().contains("parkour_world_name")) parkour_world = Main.instance.getServer().getWorld(Objects.requireNonNull(Main.instance.getConfig().getString("parkour_world_name")));
        else Main.instance.getLogger().info("Parkour world name not set up in the config.");

        for(Player p : Main.instance.getServer().getOnlinePlayers()){

            if(p.getLocation().getWorld()==parkour_world && !checkpoints_cache.containsKey(p)){

                checkpoints_cache.put(p,new ArrayList<>());

            }

        }

        starting_bossBar = Bukkit.createBossBar(Format.color("&eEvento a Tempo &7- &d&lParkour &f- &fFai il comando &d/warp parkour&f per andarci!"), BarColor.PINK, BarStyle.SEGMENTED_10, BarFlag.DARKEN_SKY);

        parkour_bossBar = Bukkit.createBossBar(Format.color("&dParkour &f- &7Tempo rimanente"), BarColor.WHITE, BarStyle.SEGMENTED_20,BarFlag.CREATE_FOG);

        parkour_bossBar.setProgress(0);
        parkour_bossBar.setVisible(true);

        starting_bossBar.setVisible(false);

        AutomaticParkourEventTimer();

    }

    public void AutomaticParkourEventTimer(){

        new BukkitRunnable(){


            @Override
            public void run(){


                if((LocalTime.now().getHour()==18 || LocalTime.now().getHour()==22) && !is_parkour_event_running && LocalTime.now().getMinute()<20 ){

                    is_parkour_event_running = true;
                    new BukkitRunnable(){


                        @Override
                        public void run(){

                            start_parkour_event();

                        }

                    }.runTaskAsynchronously(Main.instance);


                }

                if(is_parkour_event_running){

                    double max = 20;

                    double actual = LocalTime.now().getMinute();

                    if(max-actual!=1) parkour_bossBar.setTitle(Format.color("&dParkour &f- &7Tempo rimanente - &b"+(int)(max-actual)+"&f Minuti"));
                    else parkour_bossBar.setTitle(Format.color("&dParkour &f- &7Tempo rimanente - &b"+(int)(max-actual)+"&f Minuto"));

                    parkour_bossBar.setProgress(1-(actual/max));

                    if(actual==max || ((LocalTime.now().getHour()==18 || LocalTime.now().getHour()==22) && LocalTime.now().getMinute()>=20)){

                        parkour_bossBar.setProgress(0);
                        is_parkour_event_running = false;
                        new BukkitRunnable(){

                            @Override
                            public void run() {


                                stop_parkour_event();

                            }

                        }.runTaskAsynchronously(Main.instance);


                    }

                }


            }


        }.runTaskTimerAsynchronously(Main.instance,300,1200); // Ogni minuto


    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event){

        if(event.getPlayer().getLocation().getWorld()==parkour_world && !checkpoints_cache.containsKey(event.getPlayer())){

            checkpoints_cache.put(event.getPlayer(),new ArrayList<>());
            if(event.getPlayer().getAllowFlight() || event.getPlayer().isFlying()){
                event.getPlayer().setAllowFlight(false);
                event.getPlayer().setFlying(false);
            }


            if(!is_parkour_event_running && !event.getPlayer().hasPermission("dragoncore.parkour.cheatingprevention.bypass")){

                Bukkit.dispatchCommand(event.getPlayer().getServer().getConsoleSender(),"spawn "+event.getPlayer().getName());

                event.getPlayer().sendTitle("",Format.color("&cEvento parkour terminato!"),0,100,0); // 5 Secondi | Considerare anche il tempo che il loro client faccia la transizione tra i mondi.

            }

        }
        if(event.getPlayer().getLocation().getWorld() == parkour_world && is_parkour_event_running){

            parkour_bossBar.addPlayer(event.getPlayer());

        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){

        if(event.getPlayer().getLocation().getWorld()==parkour_world){

            going_back_to_checkpoint.remove(event.getPlayer());
            parkour_bossBar.removePlayer(event.getPlayer());

        }


    }


    public void start_parkour_event(){

        // MUST GIVE THEM THE PERMISSION TO GO TO THE WARP



        starting_bossBar.setVisible(false);

        parkour_bossBar.setProgress(1);
        parkour_bossBar.setVisible(true);

        for(Player p : Main.instance.getServer().getOnlinePlayers()){

            p.setNoDamageTicks(260); // Diamogli tempo di leggere il titolo casomai sia in combattimento/pericolo.
            p.sendTitle(Format.color("&d&lParkour"),Format.color("&7Evento parkour avviato!"),0,200,0);
            p.playSound(p,Sound.ENTITY_ENDER_DRAGON_AMBIENT,60f,1f);
            if(p.getWorld() == parkour_world){
                parkour_bossBar.addPlayer(p);
            }
            starting_bossBar.addPlayer(p);


        }

        starting_bossBar.setProgress(1);
        starting_bossBar.setVisible(true);
        start_starting_bossBar();





    }


    public void start_starting_bossBar(){

        new BukkitRunnable(){

            @Override
            public void run(){

                if(starting_bossBar==null){

                    Main.instance.getServer().getLogger().warning("Cod. Errore #EF2G | Per qualche motivo la starting_bossbar non era stata instanziata durante l'evento Parkour");
                    this.cancel();

                }

                if(starting_bossBar.getProgress()>=0.1){

                    starting_bossBar.setProgress(starting_bossBar.getProgress()-0.05);

                }else{

                    starting_bossBar.setProgress(0);
                    new BukkitRunnable(){

                        @Override
                        public void run(){

                            starting_bossBar.removeAll();
                            starting_bossBar.setVisible(false);

                        }

                    }.runTaskLater(Main.instance,10);

                    this.cancel();

                }

            }

        }.runTaskTimerAsynchronously(Main.instance,0,20);

    }


    public void stop_parkour_event(){     // MUST REMOVE THEM THE PERMISSION

        //Bukkit.dispatchCommand(Main.instance.getServer().getConsoleSender(),"lp group default permission unset");


        for(Player p : parkour_world.getPlayers()){

            //Bukkit.dispatchCommand(p.getServer().getConsoleSender(),"spawn "+p.getName());

            p.sendTitle("",Format.color("&cEvento parkour terminato!"),0,100,0); // 5 Secondi | Considerare anche il tempo che il loro client faccia la transizione tra i mondi.

        }

        if(winners!=0){

            String title = Format.color("&6&l1&6. &7no_first_winner");
            String subtitle = Format.color("&e&l2&e. &7no_second_winner&f, &9&l3&9. &7no_third_winner");

            if(get_winner.containsKey(3)){

                subtitle = subtitle.replaceAll("no_third_winner",get_winner.get(3).getName());

            }

            if(get_winner.containsKey(2)){

                subtitle = subtitle.replaceAll("no_second_winner",get_winner.get(2).getName());

            }

            title =  title.replaceAll("no_first_winner",get_winner.get(1).getName());

            String finalTitle = title;
            String finalSubtitle = subtitle;
            new BukkitRunnable(){


                String title = finalTitle;
                String subtitle = finalSubtitle;

                @Override
                public void run(){

                    for(Player p : Main.instance.getServer().getOnlinePlayers()){

                        p.setNoDamageTicks(200);
                        p.sendTitle(this.title,this.subtitle,0,160,0);
                        p.playSound(p,Sound.BLOCK_NOTE_BLOCK_PLING,100f,1f);

                    }

                }


            }.runTaskLaterAsynchronously(Main.instance,160);


        }else{

            new BukkitRunnable(){



                @Override
                public void run(){

                    for(Player p : Main.instance.getServer().getOnlinePlayers()){

                        p.setNoDamageTicks(200);
                        p.sendTitle("",Format.color("&cNessuno ha vinto durante l'evento &dParkour&c."),0,160,0);

                    }

                }


            }.runTaskLaterAsynchronously(Main.instance,160);

        }


        starting_bossBar.setVisible(false);
        parkour_bossBar.removeAll();
        parkour_bossBar.setVisible(false);
        parkour_bossBar.setTitle(Format.color("&dParkour &f- &7Tempo rimanente"));

        winner_position.clear();
        get_winner.clear();

        checkpoints.clear();
        going_back_to_checkpoint.clear();
        checkpoints_cache.clear();
        winners = 0;
        winner_position.clear();

    }


    // PREVENTION FLY / ELYTRA / ENDERPEARL / CHORUS FRUIT

    @EventHandler
    public void PlayerCommand(PlayerCommandPreprocessEvent event){

        if(event.getPlayer().getLocation().getWorld()==parkour_world){

            if(event.getPlayer().hasPermission("dragoncore.parkour.cheatingprevention.bypass")) return;

            if(!(event.getMessage().contains("spawn"))){

                event.setCancelled(true);
                event.getPlayer().sendTitle("",Format.color("&cPuoi solo fare il comando &7/spawn&c qua!"),0,80,0);
                event.getPlayer().playSound(event.getPlayer(),Sound.ENTITY_WANDERING_TRADER_NO,100f,1f);

            }

        }

    }


    @EventHandler
    public void WorldChangeEvent(PlayerChangedWorldEvent event){

        if(!checkpoints_cache.containsKey(event.getPlayer())){
            checkpoints_cache.put(event.getPlayer(),new ArrayList<>());
        }else{
            checkpoints_cache.remove(event.getPlayer());
        }

        if(event.getPlayer().getLocation().getWorld() == parkour_world){

            parkour_bossBar.addPlayer(event.getPlayer());

            if(event.getPlayer().hasPermission("dragoncore.parkour.flyprevention.bypass")) return;

            if(event.getPlayer().getAllowFlight()){
                event.getPlayer().setAllowFlight(false);
                event.getPlayer().setFlying(false);
                event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_VILLAGER_NO,100F,1F);
                event.getPlayer().sendTitle("",Format.color("&cNon puoi volare qui!"),0,40,0);
            }

        }else{

            parkour_bossBar.removePlayer(event.getPlayer());
            checkpoints.remove(event.getPlayer());
            checkpoints_cache.remove(event.getPlayer());

        }

    }

    @EventHandler
    public void PlayerFly(PlayerToggleFlightEvent event){

        if(event.getPlayer().getLocation().getWorld() == parkour_world){

            if(event.getPlayer().hasPermission("dragoncore.parkour.flyprevention.bypass")) return;

            event.getPlayer().setAllowFlight(false);
            event.getPlayer().setFlying(false);
            event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_VILLAGER_NO,100F,1F);
            event.getPlayer().sendTitle("",Format.color("&cNon puoi volare qui!"),0,40,0);

        }


    }

    @EventHandler
    public void PlayerElytra(EntityToggleGlideEvent event){

        if(event.getEntity().getLocation().getWorld() == parkour_world){

            if(!(event.getEntity() instanceof Player)) return;

            Player p = (Player) event.getEntity();

            if(p.hasPermission("dragoncore.parkour.flyprevention.bypass")) return;

            if(event.isGliding()) event.setCancelled(true);
            p.playSound(p, Sound.ENTITY_VILLAGER_NO,100F,1F);
            p.sendTitle("",Format.color("&cNon puoi volare qui!"),0,40,0);

        }

    }

    @EventHandler
    public void PlayerTeleport(PlayerTeleportEvent event){

        if(event.getPlayer().getLocation().getWorld() == parkour_world) {


            if(event.getPlayer().hasPermission("dragoncore.parkour.flyprevention.bypass")) return;

            if(event.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT || event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL){

                event.setCancelled(true);
                event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_VILLAGER_NO,100F,1F);
                event.getPlayer().sendTitle("",Format.color("&cNon puoi teletrasportarti qui!"),0,40,0);

            }



        }

    }

    @EventHandler
    public void PlayerMove(PlayerMoveEvent event){

        if(!is_parkour_event_running) return;


        if(event.getPlayer().getLocation().getWorld() == parkour_world) {



            if((event.getPlayer().getAllowFlight() || event.getPlayer().isFlying()) && !event.getPlayer().hasPermission("dragoncore.parkour.flyprevention.bypass")){
                event.getPlayer().setAllowFlight(false);
                event.getPlayer().setFlying(false);
                event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_VILLAGER_NO,100F,1F);
                event.getPlayer().sendTitle("",Format.color("&cNon puoi volare qui!"),0,40,0);
            }

            Block b = event.getPlayer().getLocation().add(0,-1,0).getBlock();

            if(b.getType() == Material.GOLD_BLOCK){

                if(checkpoints.containsKey(event.getPlayer())){

                    if(!(event.getPlayer().getLocation()==checkpoints.get(event.getPlayer()))){

                        Location location_block_hipotetic = b.getLocation();
                        location_block_hipotetic.setY(location_block_hipotetic.getY()+1);

                        if(!checkpoints_cache.get(event.getPlayer()).contains(location_block_hipotetic)) {

                            event.getPlayer().sendTitle("", Format.color("&2&lCheckpoint salvato!"), 0, 20, 0);

                            event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_VILLAGER_YES, 100F, 1F);

                            Location l = b.getLocation();

                            l.setY(l.getY() + 1);

                            checkpoints.replace(event.getPlayer(), l);

                            checkpoints_cache.get(event.getPlayer()).add(l);
                        }

                    }

                }else{

                    event.getPlayer().sendTitle("",Format.color("&2&lCheckpoint salvato!"),0,20,0);
                    event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_VILLAGER_YES,100F,1F);

                    Location l = b.getLocation();

                    l.setY(l.getY()+1);


                    checkpoints.put(event.getPlayer(),l);

                    checkpoints_cache.get(event.getPlayer()).add(l);

                }

            }

            if(parkour_world.getBlockAt(event.getPlayer().getLocation()).getBlockData().getMaterial() == Material.WATER){

                if(going_back_to_checkpoint.containsKey(event.getPlayer())) return;

                if(!checkpoints.containsKey(event.getPlayer())) return;

                going_back_to_checkpoint.put(event.getPlayer(),true);

                event.getPlayer().playSound(event.getPlayer(),Sound.ENTITY_GENERIC_EXPLODE,60f,1f);
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW,10,255,false));
                event.getPlayer().sendTitle("",Format.color("&fTeletrasporto..."),0,13,0);


                Main.instance.getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(Main.class), new Runnable() {
                    @Override
                    public void run() {
                        event.getPlayer().teleport(checkpoints.get(event.getPlayer()));
                        going_back_to_checkpoint.remove(event.getPlayer());
                        event.getPlayer().resetTitle();
                        event.getPlayer().sendTitle("",Format.color("&2Tornato al checkpoint!"),0,10,0);
                        event.getPlayer().spawnParticle(Particle.PORTAL,event.getPlayer().getLocation(),100,2,2,2);
                        event.getPlayer().playSound(event.getPlayer(),Sound.ENTITY_ENDERMAN_TELEPORT,100f,1f);
                        event.getPlayer().removePotionEffect(PotionEffectType.SLOW);
                    }
                }, 13L);


            }

            if(b.getType() == Material.EMERALD_BLOCK){

                if(winner_position.containsKey(event.getPlayer())){
                    return;
                }

                Location l = b.getLocation();

                l.setY(l.getY() + 1);

                checkpoints.replace(event.getPlayer(), l);

                if(winners>=3){

                    event.getPlayer().sendTitle("",Format.color("&2Bravo, hai completato il parkour!"), 0, 20, 0);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Format.color("&2+ &2&l500&2 Soldi")));
                    Bukkit.dispatchCommand(event.getPlayer().getServer().getConsoleSender(),"cmi money give "+event.getPlayer().getName()+" 500");
                    //cmi money give player tot
                    event.getPlayer().playSound(event.getPlayer(),Sound.ENTITY_WANDERING_TRADER_YES,100f,1.3f);
                    winner_position.put(event.getPlayer(),-1); // -1 Non è un vincitore, ma non gli si manda il titolo a spam


                }
                switch (winners) {
                    case 0 -> {
                        winners++;
                        winner_position.put(event.getPlayer(), 1);
                        get_winner.put(1, event.getPlayer());
                        event.getPlayer().sendTitle(Format.color("&6&lPrimo"), Format.color("&aCongratulazioni!"), 0, 40, 0);
                        for (Player p : parkour_world.getPlayers()) {
                            p.sendMessage(Format.color("&7Il giocatore &b" + event.getPlayer().getName() + "&7 e' arrivato primo all'evento Parkour!"));
                        }
                        Bukkit.dispatchCommand(event.getPlayer().getServer().getConsoleSender(),"cmi money give "+event.getPlayer().getName()+" 5000");
                        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Format.color("&2+ &2&l5K&2 Soldi")));
                        event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_PLAYER_LEVELUP, 100f, 2f);
                    }
                    case 1 -> {
                        winners++;
                        winner_position.put(event.getPlayer(), 2);
                        get_winner.put(2, event.getPlayer());
                        event.getPlayer().sendTitle(Format.color("&6&lSecondo"), Format.color("&aCongratulazioni!"), 0, 40, 0);
                        for (Player p : parkour_world.getPlayers()) {
                            p.sendMessage(Format.color("&7Il giocatore &b" + event.getPlayer().getName() + "&7 e' arrivato secondo all'evento Parkour!"));
                        }
                        Bukkit.dispatchCommand(event.getPlayer().getServer().getConsoleSender(),"cmi money give "+event.getPlayer().getName()+" 3000");
                        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Format.color("&2+ &2&l3K&2 Soldi")));
                        event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_PLAYER_LEVELUP, 100f, 1.3f);
                    }
                    case 2 -> {
                        winner_position.put(event.getPlayer(), 3);
                        get_winner.put(3, event.getPlayer());
                        winners++;
                        event.getPlayer().sendTitle(Format.color("&6&lTerzo"), Format.color("&aCongratulazioni!"), 0, 40, 0);
                        for (Player p : parkour_world.getPlayers()) {
                            p.sendMessage(Format.color("&7Il giocatore &b" + event.getPlayer().getName() + "&7 e' arrivato terzo all'evento Parkour!"));
                        }
                        Bukkit.dispatchCommand(event.getPlayer().getServer().getConsoleSender(),"cmi money give "+event.getPlayer().getName()+" 1000");
                        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Format.color("&2+ &2&l1K&2 Soldi")));
                        event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_PLAYER_LEVELUP, 100f, 0.7f);
                    }
                }


            }




        }

    }



}
