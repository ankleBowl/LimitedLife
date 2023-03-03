package software.lye.limitedlife;

import org.bukkit.ChatColor;

public class Config {

    public static long[] EVENTS;
    public static long DEATH_PENALTY;
    public static long KILL_REWARD;
    public static ChatColor[] COLORS;

    public static void init() {
        EVENTS = new long[3];

        EVENTS[0] = 15000; //NAME TURNS YELLOW
        EVENTS[1] = 15000 * 2; //NAME TURNS RED
        EVENTS[2] = 15000 * 3; //DEATH

        DEATH_PENALTY = 3595000;
        KILL_REWARD = 1800000;

        COLORS = new ChatColor[4];
        COLORS[0] = ChatColor.GREEN;
        COLORS[1] = ChatColor.YELLOW;
        COLORS[2] = ChatColor.RED;
        COLORS[3] = ChatColor.BLACK;
    }
}
