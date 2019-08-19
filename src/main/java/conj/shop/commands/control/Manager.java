package conj.shop.commands.control;

import conj.shop.addons.NPCAddon;
import conj.shop.base.Initiate;
import conj.shop.tools.ItemSerialize;
import conj.shop.data.Page;
import conj.shop.data.PageSlot;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Manager {
    public static HashMap<String, String> openpage;
    public static HashMap<String, String> previouspage;
    public static HashMap<String, String> edit;
    public static ArrayList<String> blacklist;
    public static ArrayList<Page> pages;
    public static HashMap<Integer, String> cnpcs;
    public static HashMap<Integer, List<String>> cnpcpermissions;
    public static HashMap<String, Double> worth;
    public static HashMap<String, Page> pagerecovery;

    static {
        Manager.openpage = new HashMap<String, String>();
        Manager.previouspage = new HashMap<String, String>();
        Manager.edit = new HashMap<String, String>();
        Manager.blacklist = new ArrayList<String>();
        Manager.pages = new ArrayList<Page>();
        Manager.cnpcs = new HashMap<Integer, String>();
        Manager.cnpcpermissions = new HashMap<Integer, List<String>>();
        Manager.worth = new HashMap<String, Double>();
        Manager.pagerecovery = new HashMap<String, Page>();
    }

    public static String convertMilli(final long millis) {
        final long hours = TimeUnit.MILLISECONDS.toHours(millis);
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1L);
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1L);
        String time = "";
        if (seconds > 0L) {
            time = seconds + ((seconds > 1L) ? " seconds" : " second");
        }
        if (minutes > 0L) {
            time = minutes + ((minutes > 1L) ? " minutes" : " minute") + ((seconds > 0L) ? " " : "") + time;
        }
        if (hours > 0L) {
            time = hours + ((hours > 1L) ? " hours" : " hour") + ((minutes > 0L || seconds > 0L) ? " " : "") + time;
        }
        return (time == "") ? "0 seconds" : time;
    }

    public static long convertMilli(final long millis, final String type) {
        if (type.equalsIgnoreCase("hour")) {
            return TimeUnit.MILLISECONDS.toHours(millis);
        }
        if (type.equalsIgnoreCase("day")) {
            return TimeUnit.MILLISECONDS.toDays(millis);
        }
        if (type.equalsIgnoreCase("minute")) {
            return TimeUnit.MILLISECONDS.toMinutes(millis);
        }
        if (type.equalsIgnoreCase("second")) {
            return TimeUnit.MILLISECONDS.toSeconds(millis);
        }
        return 0L;
    }

    public static String getDuration(final long start) {
        final long seconds = System.currentTimeMillis() / 1000L - start / 1000L;
        final String remaining = convertMilli(TimeUnit.SECONDS.toMillis(seconds));
        return remaining;
    }

    public static List<String> getAvailableCommands(final Player player, final String type) {
        final List<String> commands = new ArrayList<String>();
        if (type.equalsIgnoreCase("page")) {
            if (player.hasPermission("shop.page.create")) {
                commands.add(ChatColor.GREEN + "/shop page create " + ChatColor.GRAY + "<entry>");
            }
            if (player.hasPermission("shop.page.delete")) {
                commands.add(ChatColor.GREEN + "/shop page delete " + ChatColor.GRAY + "<page>");
            }
            if (player.hasPermission("shop.page.manage")) {
                commands.add(ChatColor.GREEN + "/shop page manage " + ChatColor.GRAY + "<page>");
            }
            if (player.hasPermission("shop.page.edit")) {
                commands.add(ChatColor.GREEN + "/shop page edit " + ChatColor.GRAY + "[<page>]");
            }
            if (player.hasPermission("shop.page.size")) {
                commands.add(ChatColor.GREEN + "/shop page size " + ChatColor.GRAY + "<1-6>");
            }
            if (player.hasPermission("shop.page.add")) {
                commands.add(ChatColor.GREEN + "/shop page add " + ChatColor.GRAY + "<cost> <sell>");
            }
            if (player.hasPermission("shop.page.copy")) {
                commands.add(ChatColor.GREEN + "/shop page copy " + ChatColor.GRAY + "<page>");
            }
            if (player.hasPermission("shop.page.title")) {
                commands.add(ChatColor.GREEN + "/shop page title " + ChatColor.GRAY + "<title>");
            }
            if (player.hasPermission("shop.page.type")) {
                commands.add(ChatColor.GREEN + "/shop page type " + ChatColor.GRAY + "<normal/sell>");
            }
            if (player.hasPermission("shop.page.open")) {
                commands.add(ChatColor.GREEN + "/shop page open " + ChatColor.GRAY + "<page>");
            }
            if (player.hasPermission("shop.page.properties")) {
                commands.add(ChatColor.GREEN + "/shop page properties");
            }
            if (player.hasPermission("shop.page.recover")) {
                commands.add(ChatColor.GREEN + "/shop page recover");
            }
            if (player.hasPermission("shop.page.clear")) {
                commands.add(ChatColor.GREEN + "/shop page clear");
            }
            if (player.hasPermission("shop.page.list")) {
                commands.add(ChatColor.GREEN + "/shop page list");
            }
        } else if (type.equalsIgnoreCase("citizen")) {
            if (player.hasPermission("shop.citizen.page")) {
                commands.add(ChatColor.GREEN + "/shop citizen page " + ChatColor.GRAY + "<page>");
            }
            if (player.hasPermission("shop.citizen.permission.add")) {
                commands.add(ChatColor.GREEN + "/shop citizen permission add " + ChatColor.GRAY + "<permission>");
            }
            if (player.hasPermission("shop.citizen.permission.remove")) {
                commands.add(ChatColor.GREEN + "/shop citizen permission remove " + ChatColor.GRAY + "<permission>");
            }
            if (player.hasPermission("shop.citizen.permission.clear")) {
                commands.add(ChatColor.GREEN + "/shop citizen permission clear");
            }
            if (player.hasPermission("shop.citizen.permission.clear") || player.hasPermission("shop.citizen.permission.add") || player.hasPermission("shop.citizen.permission.remove")) {
                commands.add(ChatColor.GREEN + "/shop citizen permission");
            }
        } else if (type.equalsIgnoreCase("console")) {
            if (player.hasPermission("shop.console")) {
                commands.add(ChatColor.GREEN + "/shop open page " + ChatColor.GRAY + "<page> <player>");
                commands.add(ChatColor.GREEN + "/shop page open " + ChatColor.GRAY + "<page> <player>");
                commands.add(ChatColor.GREEN + "/shop page move " + ChatColor.GRAY + "<page> <from> <to> [<soft> or <hard>]");
                commands.add(ChatColor.GREEN + "/shop close inventory " + ChatColor.GRAY + "<player>");
                commands.add(ChatColor.GREEN + "/shop send broadcast " + ChatColor.GRAY + "<message>");
                commands.add(ChatColor.GREEN + "/shop send message " + ChatColor.GRAY + "<player> <message>");
                commands.add(ChatColor.GREEN + "/shop take money " + ChatColor.GRAY + "<amount> <player>");
                commands.add(ChatColor.GREEN + "/shop cooldown clear " + ChatColor.GRAY + "<player>");
                commands.add(ChatColor.GREEN + "/shop teleport " + ChatColor.GRAY + "<player> <world> <x> <y> <z> [<yaw> <pitch>]");
            }
        } else if (type.equalsIgnoreCase("blacklist")) {
            if (player.hasPermission("shop.blacklist.add")) {
                commands.add(ChatColor.GREEN + "/shop blacklist add");
            }
            if (player.hasPermission("shop.blacklist.remove")) {
                commands.add(ChatColor.GREEN + "/shop blacklist remove");
            }
            if (player.hasPermission("shop.blacklist.list")) {
                commands.add(ChatColor.GREEN + "/shop blacklist list");
            }
        } else if (type.equalsIgnoreCase("worth")) {
            if (player.hasPermission("shop.worth.set")) {
                commands.add(ChatColor.GREEN + "/shop worth set " + ChatColor.GRAY + "<amount>");
            }
            if (player.hasPermission("shop.worth.list")) {
                commands.add(ChatColor.GREEN + "/shop worth list");
            }
            if (player.hasPermission("shop.worth.item")) {
                commands.add(ChatColor.GREEN + "/shop worth");
            }
        }
        return commands;
    }

    public static Manager get() {
        return new Manager();
    }

    public double getWorth(final ItemStack item) {
        return this.getFlatWorth(item) * item.getAmount();
    }

    public double getFlatWorth(final ItemStack item) {
        final String serialize = ItemSerialize.serializeSoft(item);
        double value = (Manager.worth.get(serialize) != null) ? Manager.worth.get(serialize) : 0.0;
        if (value == 0.0) {
            final ItemStack i = new ItemStack(item.getType());
            final String serialize2 = ItemSerialize.serializeSoft(i);
            value = ((Manager.worth.get(serialize2) != null) ? Manager.worth.get(serialize2) : 0.0);
        }
        return value;
    }

    public void setWorth(final ItemStack item, final double w, final boolean save) {
        final String serialize = ItemSerialize.serializeSoft(item);
        Manager.worth.put(serialize, w);
        if (save) {
            Initiate.sf.saveWorthData();
        }
    }

    public List<String> getBlacklist() {
        return Manager.blacklist;
    }

    public boolean blacklistContains(final String world) {
        return Manager.blacklist.contains(world);
    }

    public void blacklistAdd(final String world) {
        if (!Manager.blacklist.contains(world)) {
            Manager.blacklist.add(world);
        }
    }

    public void blacklistRemove(final String world) {
        Manager.blacklist.remove(world);
    }

    public Page getRecoveryPage(final Player player) {
        return Manager.pagerecovery.get(player.getUniqueId().toString());
    }

    public boolean hasRecoveryPage(final Player player) {
        return Manager.pagerecovery.get(player.getUniqueId().toString()) != null;
    }

    public List<Page> getPages() {
        return Manager.pages;
    }

    public Page getPage(final String page) {
        if (page == null) {
            return null;
        }
        for (final Page p : Manager.pages) {
            if (p.getID().equalsIgnoreCase(page)) {
                return p;
            }
        }
        return null;
    }

    public Page getPage(final NPC npc) {
        if (npc.hasTrait(NPCAddon.class)) {
            return this.getPage(Manager.cnpcs.get(npc.getId()));
        }
        return null;
    }

    public String getCooldown(final Player player, final Page page, final int slot) {
        final PageSlot ps = page.getPageSlot(slot);
        final long seconds = ps.getCooldown() - (System.currentTimeMillis() / 1000L - ps.getCooldown(player) / 1000L);
        final String remaining = convertMilli(TimeUnit.SECONDS.toMillis(seconds));
        return remaining;
    }

    public long getCooldownMilli(final Player player, final Page page, final int slot) {
        final PageSlot ps = page.getPageSlot(slot);
        final long milli = ps.getCooldown() - (System.currentTimeMillis() - ps.getCooldown(player));
        return milli;
    }

    public List<String> getViewers(final Page page) {
        final List<String> viewers = new ArrayList<String>();
        for (final Map.Entry<String, String> value : Manager.openpage.entrySet()) {
            if (value.getValue().equalsIgnoreCase(page.getID())) {
                viewers.add(value.getKey());
            }
        }
        return viewers;
    }

    public void setOpenPage(final Player player, final String page) {
        Manager.openpage.put(player.getName(), page);
    }

    public void setPreviousPage(final Player player, final String page) {
        Manager.previouspage.put(player.getName(), page);
    }

    public void removePreviousPage(final Player player) {
        Manager.previouspage.remove(player.getName());
    }

    public void removeOpenPage(final Player player) {
        Manager.openpage.remove(player.getName());
    }

    public String getOpenPage(final Player player) {
        if (Manager.openpage == null) {
            return "";
        }
        return Manager.openpage.containsKey(player.getName()) ? Manager.openpage.get(player.getName()) : "";
    }

    public String getPreviousPage(final Player player) {
        if (Manager.previouspage == null) {
            return "";
        }
        return Manager.previouspage.containsKey(player.getName()) ? Manager.previouspage.get(player.getName()) : "";
    }

    public String getEditorPage(final Player player) {
        return Manager.edit.containsKey(player.getName()) ? Manager.edit.get(player.getName()) : "";
    }

    public void setCitizenPage(final int id, final String page) {
        if (page == null) {
            Manager.cnpcs.remove(id);
            return;
        }
        Manager.cnpcs.put(id, page);
        Initiate.sf.saveCitizensData();
    }

    public boolean addCitizenPermission(final int id, final String permission) {
        List<String> permissions = new ArrayList<String>();
        if (Manager.cnpcpermissions.get(id) != null) {
            permissions = Manager.cnpcpermissions.get(id);
        }
        final boolean add = permissions.add(permission);
        Manager.cnpcpermissions.put(id, permissions);
        Initiate.sf.saveCitizensData();
        return add;
    }

    public boolean removeCitizenPermission(final int id, final String permission) {
        List<String> permissions = new ArrayList<String>();
        if (Manager.cnpcpermissions.get(id) != null) {
            permissions = Manager.cnpcpermissions.get(id);
        }
        final boolean remove = permissions.remove(permission);
        Manager.cnpcpermissions.put(id, permissions);
        Initiate.sf.saveCitizensData();
        return remove;
    }

    public void clearCitizenPermissions(final int id) {
        if (Manager.cnpcpermissions.get(id) != null) {
            Manager.cnpcpermissions.remove(id);
        }
    }

    public List<String> getCitizenPermissions(final int id) {
        if (Manager.cnpcpermissions.get(id) != null) {
            return Manager.cnpcpermissions.get(id);
        }
        return new ArrayList<String>();
    }
}
