package software.lye.limitedlife;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

public final class LimitedLife extends JavaPlugin implements Listener {

    public SaveData saveData;
    public Team[] teams;
    public Scoreboard scoreboard;

    public static JavaPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        Config.init(this);
        Bukkit.getPluginManager().registerEvents(this, this);

        Bukkit.getLogger().info("Initialized LimitedLife");

        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        teams = new Team[4];

        for (int i = 0; i < 4; i++) {
            if (scoreboard.getTeam(String.valueOf(i)) == null) {
                Team temp = scoreboard.registerNewTeam(String.valueOf(i));
                temp.setColor(Config.COLORS[i]);
            }
            teams[i] = scoreboard.getTeam(String.valueOf(i));
        }

        saveData = SaveData.readData("save.limls", this, teams);
    }

    @Override
    public void onDisable() {
        saveData.commitData();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (command.getLabel().equalsIgnoreCase("time")) {
                if (saveData == null) {
                    sender.sendMessage(ChatColor.RED + "The game has not been started yet!");
                }
                LimitedLifePlayer player = saveData.getPlayer(((Player) sender).getUniqueId());

                long milliseconds = player.getTimeRemaining();

                int seconds = (int) (milliseconds / 1000) % 60 ;
                int minutes = (int) ((milliseconds / (1000*60)) % 60);
                int hours   = (int) ((milliseconds / (1000*60*60)) % 24);

                sender.sendMessage(ChatColor.YELLOW + "You have " + hours + " hours(s), " + minutes + " minute(s), and " + seconds + " second(s) left!");
                return true;
            }

            if (command.getLabel().equalsIgnoreCase("start")) {
                startingCountdown();

                new BukkitRunnable() {
                    public void run() {
                        saveData = SaveData.createData("save.limls", LimitedLife.plugin, teams);
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            LimitedLifePlayer llp = saveData.getPlayer(p.getUniqueId());
                            llp.join();
                        }
                    }
                }.runTaskLater(this, 60);
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (saveData == null) { return; }
        LimitedLifePlayer player = saveData.getPlayer(event.getPlayer().getUniqueId());
        player.getPlayer().setScoreboard(scoreboard);
        player.join();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (saveData == null) { return; }
        LimitedLifePlayer player = saveData.getPlayer(event.getPlayer().getUniqueId());
        player.disconnect();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (saveData == null) { return; }
        UUID playerUUID = event.getEntity().getUniqueId();
        saveData.getPlayer(playerUUID).died();

        EntityDamageEvent entityDamageEvent = event.getEntity().getLastDamageCause();
        if ((entityDamageEvent != null) && !entityDamageEvent.isCancelled() && (entityDamageEvent instanceof EntityDamageByEntityEvent)) {
            Entity damager = ((EntityDamageByEntityEvent) entityDamageEvent).getDamager();
            if (damager instanceof Player) {
                UUID killerUUID = damager.getUniqueId();
                saveData.getPlayer(killerUUID).murdered();
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
//        if (!(event.getEntity() instanceof Player && event.getDamager() instanceof Player)) {
//            return;
//        }
//
//        Player hurt0 = (Player) event.getEntity();
//        Player damager0 = (Player) event.getDamager();
//
//        if (hurt0 == damager0) {
//            return;
//        }
//
//        LimitedLifePlayer hurt = saveData.getPlayer(hurt0.getUniqueId());
//        LimitedLifePlayer damager = saveData.getPlayer(damager0.getUniqueId());
//
//        if (damager.getIndex() == 2) {
//            return;
//        }
//        if (damager.getIndex() > hurt.getIndex()) {
//            return;
//        }
//        event.setCancelled(true);
    }

    public void startingCountdown() {
        String[] messages = {
                ChatColor.GREEN + "3",
                ChatColor.YELLOW + "2",
                ChatColor.RED + "1",
                ChatColor.GREEN + "The timer has begun!"
        };
        for (int i = 0; i < 4; i++) {
            final String message = messages[i];
            new BukkitRunnable() {
                public void run() {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendTitle(message, "");
                    }
                }
            }.runTaskLater(this, i * 20);
        }
    }
}
