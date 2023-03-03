package software.lye.limitedlife;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.UUID;

public class SaveData {

    private HashMap<UUID, LimitedLifePlayer> players;
    private JavaPlugin plugin;
    private Team[] teams;

    private SaveData(JavaPlugin plugin, Team[] teams) {
        this.plugin = plugin;
        this.teams = teams;

        players = new HashMap<UUID, LimitedLifePlayer>();
    }

    public void commitData() {
//        TODO SAVE DATA
        return;
    }

    public LimitedLifePlayer getPlayer(UUID uuid) {
        LimitedLifePlayer player = players.get(uuid);
        if (player == null) {
            player = new LimitedLifePlayer(uuid, 0, false, plugin, teams);
            players.put(uuid, player);
        }
        return player;
    }

    public static SaveData tryReadData(String filePath, JavaPlugin plugin, Team[] teams) {
//        TODO READ DATA
        return new SaveData(plugin, teams);
    }

}