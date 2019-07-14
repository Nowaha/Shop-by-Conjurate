package conj.Shop.auto;

import conj.Shop.base.Initiate;
import conj.Shop.control.Manager;
import conj.Shop.data.Page;
import conj.Shop.enums.Config;
import conj.Shop.tools.Debug;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Autosave {
    private static int id;

    public static void start() {
        int delay = Config.AUTOSAVE_DELAY.getNumeral();
        if (delay < 1) {
            delay = 20;
        }
        if (Config.AUTOSAVE.isActive()) {
            cancel();
            final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
            Autosave.id = scheduler.scheduleSyncRepeatingTask(Initiate.getPlugin((Class) Initiate.class), new Runnable() {
                @Override
                public void run() {
                    if (!Config.AUTOSAVE.isActive()) {
                        Autosave.cancel();
                        Debug.log("Autosave has been disabled.");
                        return;
                    }
                    Autosave.save();
                }
            }, 0L, (long) (delay * 1200));
        }
    }

    public static void cancel() {
        final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.cancelTask(Autosave.id);
    }

    public static void save() {
        if (Initiate.sf != null) {
            for (final Page p : Manager.pages) {
                Initiate.sf.savePageData(p.getID());
            }
            Initiate.sf.saveCitizensData();
            Initiate.sf.saveWorthData();
            Initiate.sf.saveMiscData();
        } else if (Debug.debug) {
            Bukkit.getLogger().info("Failed to save data.");
        }
    }
}
