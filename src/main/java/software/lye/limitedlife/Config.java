package software.lye.limitedlife;

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {

    public static long[] EVENTS;
    public static long DEATH_PENALTY;
    public static long KILL_REWARD;
    public static ChatColor[] COLORS;

    public static void init(JavaPlugin plugin) {
        plugin.saveDefaultConfig();

        Configuration config = plugin.getConfig();
        config.options().copyDefaults();

        EVENTS = new long[3];

        EVENTS[0] = config.getLong("times.name_turns_yellow_ms");
        EVENTS[1] = config.getLong("times.name_turns_red_ms");
        EVENTS[2] = config.getLong("times.death_ms");

        KILL_REWARD = config.getLong("kill_reward");
        DEATH_PENALTY = config.getLong("death_penalty");

        COLORS = new ChatColor[4];
        COLORS[0] = ChatColor.valueOf(String.valueOf(config.getInt("colors.full_time")));
        COLORS[1] = ChatColor.valueOf(String.valueOf(config.getInt("colors.first")));
        COLORS[2] = ChatColor.valueOf(String.valueOf(config.getInt("colors.second")));
        COLORS[3] = ChatColor.valueOf(String.valueOf(config.getInt("colors.death")));
    }
}
