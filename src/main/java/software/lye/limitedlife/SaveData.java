package software.lye.limitedlife;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SaveData {

    private HashMap<UUID, LimitedLifePlayer> players;
    private JavaPlugin plugin;
    private Team[] teams;
    private Path filePath;

    private static final int SAVE_VERSION = 0;

    private SaveData(JavaPlugin plugin, Team[] teams, Path filePath) {
        this.plugin = plugin;
        this.teams = teams;
        this.filePath = filePath;

        players = new HashMap<UUID, LimitedLifePlayer>();
    }

    public void commitData() {
        String output = "";
        output += SAVE_VERSION + "\n";
        for (Map.Entry<UUID, LimitedLifePlayer> entry : players.entrySet()) {
            output += entry.getValue().getSaveString() + "\n";
        }
        output = output.trim();

        filePath.toFile().delete();

        try {
            FileWriter myWriter = new FileWriter(filePath.toAbsolutePath().toString());
            myWriter.write(output);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


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
        Path path = Paths.get(Bukkit.getServer().getWorldContainer().getAbsolutePath(), filePath);

        SaveData sData = new SaveData(plugin, teams, path);

        if (!path.toFile().exists()) {
            return sData;
        }

        String data = "";
        try {
            data = new String(Files.readAllBytes(path));
        } catch (Exception e) {
            e.printStackTrace();
            return sData;
        }

        String[] parts = data.split("\n");
        int saveFormat = Integer.valueOf(parts[0]);
        switch (saveFormat) {
            case 0:
                for (int i = 1; i < parts.length; i++) {
                    String[] userParts = parts[i].split(",");
                    UUID uuid = UUID.fromString(userParts[0]);
                    long timeSpentOnline = Long.valueOf(userParts[1]);
                    boolean fullDead = Boolean.valueOf(userParts[2]);
                    sData.players.put(uuid, new LimitedLifePlayer(uuid, timeSpentOnline, fullDead, plugin, teams));
                }
                break;
        }
        return sData;
    }
}