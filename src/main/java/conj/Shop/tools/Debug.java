package conj.Shop.tools;

import conj.Shop.base.Initiate;
import org.bukkit.plugin.java.JavaPlugin;

public class Debug {
    public static boolean debug;

    public static void log(final String message) {
        if (Debug.debug) {
            Initiate.getPlugin().getLogger().info(message);
        }
    }

    public static void log(final Object o) {
        if (Debug.debug) {
            Initiate.getPlugin().getLogger().info(o.toString());
        }
    }
}
