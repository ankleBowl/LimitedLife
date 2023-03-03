package software.lye.limitedlife;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

import java.util.Date;
import java.util.UUID;

public class LimitedLifePlayer {

    private UUID playerUUID;
    private long lastJoinTime;
    private long timeSpentOnline;
    private boolean fullDead;

    private BukkitTask killPlayerTask;
    private JavaPlugin plugin;
    private int nextEventId;
    private Team[] teams;


    public LimitedLifePlayer(UUID uuid, long timeSpentOnline, boolean fullDead, JavaPlugin plugin, Team[] teams) {
        this.playerUUID = uuid;
        this.plugin = plugin;
        this.fullDead = fullDead;
        this.timeSpentOnline = timeSpentOnline;
        this.teams = teams;
    }

    public void join() {
        if (fullDead) {
            setFullDead();
        }
        lastJoinTime = System.currentTimeMillis();
        setupNextEventTimer();
    }

    public void disconnect() {
        commitTimeAlive();
        killPlayerTask.cancel();
    }

    public void murdered() {
        killPlayerTask.cancel();
        commitTimeAlive();
        timeSpentOnline -= Config.KILL_REWARD;
        setupNextEventTimer();
    }

    public void died() {
        killPlayerTask.cancel();
        commitTimeAlive();
        Bukkit.broadcastMessage("Played died! Previous time " + timeSpentOnline);
        timeSpentOnline += Config.DEATH_PENALTY;
        setupNextEventTimer();
        Bukkit.broadcastMessage("New time " + timeSpentOnline);
    }

    public void setupNextEventTimer() {
        Bukkit.broadcastMessage("Setting next event timer");
        Bukkit.broadcastMessage("Online for: " + timeSpentOnline);
        nextEventId = 3;
        for (int i = Config.EVENTS.length - 1; i > -1; i--) {
            Bukkit.broadcastMessage("Running " + timeSpentOnline + " > " + Config.EVENTS[i]);
            if (timeSpentOnline > Config.EVENTS[i]) {
                break;
            }
            nextEventId = i;
        }

        switch (nextEventId) {
            case 3:
                setFullDead();
                setColor(nextEventId);
                return;
            default:
                setColor(nextEventId);
                break;

        }

        Bukkit.broadcastMessage("Next event ID is " + nextEventId);

        long nextEventTime = Config.EVENTS[nextEventId];

        long ticksUntilNextEvent = ((nextEventTime - timeSpentOnline) / 50) + 1;

        killPlayerTask = new BukkitRunnable() {
            public void run() {
                Bukkit.broadcastMessage("Triggering event " + nextEventId + " for player " + playerUUID.toString());
                commitTimeAlive();
                setupNextEventTimer();
            }
        }.runTaskLater(plugin, ticksUntilNextEvent);
        Bukkit.broadcastMessage("Creating timer for " + playerUUID.toString() + " with length " + ticksUntilNextEvent + " waiting for event ID " + nextEventId);
    }

    //  SHOULD BE CALLED ON EVERY EVENT THAT CHANGES THE AMOUNT OF TIME THE PLAYER HAS, AND ON LOGIN/LOGOUT
    public void commitTimeAlive() {
        long currentTime = System.currentTimeMillis();
        long durationPlayedSession = currentTime - lastJoinTime;

        timeSpentOnline += durationPlayedSession;

        lastJoinTime = System.currentTimeMillis();
    }

    public long getAliveDuration() {
        commitTimeAlive();
        return timeSpentOnline;
    }

    public void setFullDead() {
        fullDead = true;
        getPlayer().setGameMode(GameMode.SPECTATOR);
        getPlayer().sendMessage(ChatColor.GRAY + "You are dead! You can still spectate the game.");
    }

    public String getSaveString() {
        commitTimeAlive();
        String output = "";
        output += playerUUID.toString() + ",";
        output += timeSpentOnline + ",";
        output += Boolean.valueOf(fullDead);
        return output;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }

    public void setColor(int index) {
        teams[index].addPlayer(getPlayer());
    }
}
