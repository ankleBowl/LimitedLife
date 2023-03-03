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
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

public final class LimitedLife extends JavaPlugin implements Listener {

    public SaveData saveData;

    public Team[] teams;
    public Scoreboard scoreboard;

    @Override
    public void onEnable() {
        Config.init();
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

        saveData = SaveData.tryReadData("save.limls", this, teams);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        LimitedLifePlayer player = saveData.getPlayer(event.getPlayer().getUniqueId());
        player.getPlayer().setScoreboard(scoreboard);
        player.join();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        LimitedLifePlayer player = saveData.getPlayer(event.getPlayer().getUniqueId());
        player.disconnect();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
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
}
