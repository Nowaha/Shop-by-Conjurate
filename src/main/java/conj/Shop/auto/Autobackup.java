package conj.Shop.auto;

import conj.Shop.base.Initiate;
import conj.Shop.control.Manager;
import conj.Shop.enums.Config;
import conj.Shop.tools.Debug;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Autobackup {
    private static int id;

    public static void start() {
        if (Config.AUTOBACKUP.isActive()) {
            cancel();
            final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
            Autobackup.id = scheduler.scheduleSyncRepeatingTask(Initiate.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    if (!Config.AUTOBACKUP.isActive()) {
                        Autobackup.cancel();
                        Debug.log("Autobackup has been disabled.");
                        return;
                    }
                    Autobackup.create();
                }
            }, 0L, 72000L);
        }
    }

    public static void create() {
        final long start = System.currentTimeMillis();
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final Calendar cal = Calendar.getInstance();
        final String date = dateFormat.format(cal.getTime());
        final File file = new File(Initiate.getPlugin().getDataFolder().getPath() + "/data");
        final File backupfile = new File(Initiate.getPlugin().getDataFolder().getPath() + "/backup/" + date);
        if (!backupfile.exists()) {
            backupfile.mkdir();
        }
        if (file.exists()) {
            try {
                FileUtils.copyDirectory(file, backupfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Debug.log("Backup took: " + Manager.getDuration(start));
    }

    public static void delete() {
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm");
        final Calendar cal = Calendar.getInstance();
        final String date = dateFormat.format(cal.getTime());
        final File backupfile = new File(Initiate.getPlugin().getDataFolder().getPath() + "/backup/" + date);
        if (backupfile.exists()) {
            backupfile.delete();
        }
    }

    public static void cancel() {
        final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.cancelTask(Autobackup.id);
    }
}
