package conj.UA.api.files;

import conj.Shop.control.Manager;
import conj.Shop.data.Page;
import conj.Shop.data.PageSlot;
import conj.Shop.enums.Function;
import conj.Shop.enums.GUIFunction;
import conj.Shop.enums.Hidemode;
import conj.Shop.tools.Debug;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopFile {
    private String directory;

    public ShopFile(final String directory) {
        this.directory = directory + "/data";
    }

    public SmartFile getPageFile(final String page) {
        return new SmartFile(this.directory + "/pages", page, ".yml");
    }

    public SmartFile getCitizensFile() {
        return new SmartFile(this.directory, "citizens_storage", ".yml");
    }

    public SmartFile getMiscFile() {
        return new SmartFile(this.directory, "misc_storage", ".yml");
    }

    public SmartFile getWorthFile() {
        return new SmartFile(this.directory, "worth_storage", ".yml");
    }

    public void loadCitizensData() {
        final SmartFile sf = this.getCitizensFile();
        final FileConfiguration data = sf.getConfig();
        if (data.getConfigurationSection("") != null) {
            for (final String citizen : data.getConfigurationSection("").getKeys(false)) {
                try {
                    final int cid = Integer.parseInt(citizen);
                    final String page = data.getString(citizen + ".page");
                    if (page == null) {
                        continue;
                    }
                    Manager.cnpcs.put(cid, page);
                } catch (NumberFormatException ex) {
                }
            }
            for (final String citizen : data.getConfigurationSection("").getKeys(false)) {
                try {
                    final int cid = Integer.parseInt(citizen);
                    final List<String> permissions = data.getStringList(citizen + ".permission");
                    if (permissions == null) {
                        continue;
                    }
                    Manager.cnpcpermissions.put(cid, permissions);
                } catch (NumberFormatException ex2) {
                }
            }
        }
    }

    public void loadMiscData() {
        final SmartFile sf = this.getMiscFile();
        final FileConfiguration data = sf.getConfig();
        final List<String> blacklist = data.getStringList("blacklist");
        if (blacklist != null) {
            for (final String s : blacklist) {
                if (Bukkit.getWorld(s) != null) {
                    Manager.blacklist.add(s);
                }
            }
        }
    }

    public void loadWorthData() {
        final SmartFile sf = this.getWorthFile();
        final FileConfiguration data = sf.getConfig();
        if (data.getConfigurationSection("Worth") != null) {
            for (final String get : data.getConfigurationSection("Worth").getKeys(false)) {
                Manager.worth.put(get, data.getDouble("Worth." + get));
            }
        }
    }

    public List<Page> loadPages() {
        final List<Page> pages = new ArrayList<Page>();
        final File folder = new File(this.directory + "/pages/");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        final File[] files = folder.listFiles();
        File[] array;
        for (int length = (array = files).length, i = 0; i < length; ++i) {
            final File f = array[i];
            if (f.isFile()) {
                final String pagename = f.getName().replace(".yml", "");
                final Page p = new Page(pagename);
                final SmartFile sf = this.getPageFile(pagename);
                if (sf != null) {
                    final FileConfiguration fc = sf.getConfig();
                    if (fc.getConfigurationSection("data.properties") != null) {
                        p.type = fc.getInt("data.properties.type");
                        p.title = fc.getString("data.properties.title");
                        p.size = fc.getInt("data.properties.size");
                        p.gui = fc.getBoolean("data.properties.gui");
                        final HashMap<String, Object> pagedata = new HashMap<String, Object>();
                        if (fc.getConfigurationSection("data.properties.pagedata") != null) {
                            for (final String key : fc.getConfigurationSection("data.properties.pagedata").getKeys(false)) {
                                if (fc.get("data.properties.pagedata." + key) != null) {
                                    pagedata.put(key, fc.get("data.properties.pagedata." + key));
                                }
                            }
                        }
                        p.pagedata = pagedata;
                        p.slots = (List<Integer>) fc.getList("data.inventory.slots");
                        p.items = (List<HashMap<Map<String, Object>, Map<String, Object>>>) fc.get("data.inventory.items");
                        final HashMap<Integer, PageSlot> pageslots = new HashMap<Integer, PageSlot>();
                        if (fc.getConfigurationSection("data.inventory.slotdata") != null) {
                            for (final String slotstring : fc.getConfigurationSection("data.inventory.slotdata").getKeys(false)) {
                                int slot = 0;
                                try {
                                    slot = Integer.parseInt(slotstring);
                                } catch (NumberFormatException nfe) {
                                    Debug.log(pagename + " slot " + slot + " failed to load.");
                                    continue;
                                }
                                final String page = fc.getString("data.inventory.slotdata." + slotstring + ".page");
                                final double cost = fc.getDouble("data.inventory.slotdata." + slotstring + ".cost");
                                final double sell = fc.getDouble("data.inventory.slotdata." + slotstring + ".sell");
                                final int cooldown = fc.getInt("data.inventory.slotdata." + slotstring + ".cooldown");
                                final String visiblity = fc.getString("data.inventory.slotdata." + slotstring + ".visibility");
                                final String function = fc.getString("data.inventory.slotdata." + slotstring + ".function");
                                final String guifunction = fc.getString("data.inventory.slotdata." + slotstring + ".guifunction");
                                final List<String> commands = (List<String>) fc.getList("data.inventory.slotdata." + slotstring + ".command");
                                final List<String> permissions = (List<String>) fc.getList("data.inventory.slotdata." + slotstring + ".permission");
                                final List<String> hidepermissions = (List<String>) fc.get("data.inventory.slotdata." + slotstring + ".hidepermission");
                                final List<String> pagelore = (List<String>) fc.get("data.inventory.slotdata." + slotstring + ".pagelore");
                                final List<HashMap<Map<String, Object>, Map<String, Object>>> pageitems = (List<HashMap<Map<String, Object>, Map<String, Object>>>) fc.get("data.inventory.slotdata." + slotstring + ".items");
                                final HashMap<String, Long> cds = new HashMap<String, Long>();
                                if (fc.getConfigurationSection("data.inventory.slotdata." + slotstring + ".cd") != null) {
                                    for (final String player : fc.getConfigurationSection("data.inventory.slotdata." + slotstring + ".cd").getKeys(false)) {
                                        if (fc.get("data.inventory.slotdata." + slotstring + ".cd." + player) == null) {
                                            continue;
                                        }
                                        cds.put(player, fc.getLong("data.inventory.slotdata." + slotstring + ".cd." + player));
                                    }
                                }
                                final HashMap<String, Object> slotdata = new HashMap<String, Object>();
                                if (fc.getConfigurationSection("data.inventory.slotdata." + slotstring + ".slotdata") != null) {
                                    for (final String k : fc.getConfigurationSection("data.inventory.slotdata." + slotstring + ".slotdata").getKeys(false)) {
                                        if (fc.get("data.inventory.slotdata." + slotstring + ".slotdata." + k) == null) {
                                            continue;
                                        }
                                        slotdata.put(k, fc.get("data.inventory.slotdata." + slotstring + ".slotdata." + k));
                                    }
                                }
                                final PageSlot ps = new PageSlot(page, slot, cost, sell, cooldown, cds, Hidemode.fromString(visiblity), Function.fromString(function), GUIFunction.fromString(guifunction), commands, permissions, hidepermissions, pagelore, pageitems, slotdata);
                                pageslots.put(slot, ps);
                            }
                        }
                        p.pageslots = pageslots;
                        pages.add(p);
                    }
                }
            }
        }
        for (final Page p2 : pages) {
            p2.create();
        }
        return pages;
    }

    public void savePageData(final String page) {
        final Page p = Manager.get().getPage(page);
        if (p != null) {
            final SmartFile sf = this.getPageFile(page);
            sf.reset();
            final long start = System.currentTimeMillis();
            final FileConfiguration data = sf.getConfig();
            data.set("data.properties.type", p.type);
            data.set("data.properties.title", p.title);
            data.set("data.properties.size", p.size);
            data.set("data.properties.gui", p.gui);
            for (final Map.Entry<String, Object> v : p.pagedata.entrySet()) {
                if (v.getValue() == null) {
                    break;
                }
                if (v.getValue() instanceof List && ((List) v.getValue()).isEmpty()) {
                    break;
                }
                data.set("data.properties.pagedata." + v.getKey(), v.getValue());
            }
            data.set("data.inventory.slots", p.slots);
            data.set("data.inventory.items", p.items);
            for (final PageSlot ps : p.pageslots.values()) {
                data.set("data.inventory.slotdata." + ps.getSlot() + ".page", ps.getPage());
                data.set("data.inventory.slotdata." + ps.getSlot() + ".cost", ps.getCost());
                data.set("data.inventory.slotdata." + ps.getSlot() + ".sell", ps.getSell());
                data.set("data.inventory.slotdata." + ps.getSlot() + ".cooldown", ps.getCooldown());
                data.set("data.inventory.slotdata." + ps.getSlot() + ".visibility", ps.getHidemode().toString());
                data.set("data.inventory.slotdata." + ps.getSlot() + ".function", ps.getFunction().toString());
                data.set("data.inventory.slotdata." + ps.getSlot() + ".guifunction", ps.getGUIFunction().toString());
                if (!ps.getCommands().isEmpty()) {
                    data.set("data.inventory.slotdata." + ps.getSlot() + ".command", ps.getCommands());
                }
                if (!ps.getPermissions().isEmpty()) {
                    data.set("data.inventory.slotdata." + ps.getSlot() + ".permission", ps.getPermissions());
                }
                if (!ps.getHidePermissions().isEmpty()) {
                    data.set("data.inventory.slotdata." + ps.getSlot() + ".hidepermission", ps.getHidePermissions());
                }
                if (!ps.getPageLore().isEmpty()) {
                    data.set("data.inventory.slotdata." + ps.getSlot() + ".pagelore", ps.getPageLore());
                }
                for (final Map.Entry<String, Long> d : ps.getCooldowns().entrySet()) {
                    if (ps.inCooldown(d.getValue())) {
                        data.set("data.inventory.slotdata." + ps.getSlot() + ".cd." + d.getKey(), d.getValue());
                    }
                }
                for (final Map.Entry<String, Object> d2 : ps.getSlotData().entrySet()) {
                    if (d2.getValue() == null) {
                        break;
                    }
                    if (d2.getValue() instanceof List && ((List) d2.getValue()).isEmpty()) {
                        break;
                    }
                    data.set("data.inventory.slotdata." + ps.getSlot() + ".slotdata." + d2.getKey(), d2.getValue());
                }
                if (!ps.items.isEmpty()) {
                    data.set("data.inventory.slotdata." + ps.getSlot() + ".items", ps.items);
                }
            }
            sf.save(data);
            Debug.log("Page " + page + " write took: " + Manager.getDuration(start));
        }
    }

    public void saveCitizensData() {
        final long start = System.currentTimeMillis();
        final SmartFile sf = this.getCitizensFile();
        sf.reset();
        final FileConfiguration data = sf.getConfig();
        for (final Map.Entry<Integer, String> v : Manager.cnpcs.entrySet()) {
            data.set(v.getKey() + ".page", v.getValue());
        }
        for (final Map.Entry<Integer, List<String>> v2 : Manager.cnpcpermissions.entrySet()) {
            data.set(v2.getKey() + ".permission", v2.getValue());
        }
        sf.save(data);
        Debug.log("Citizens write took: " + Manager.getDuration(start));
    }

    public void saveWorthData() {
        final SmartFile sf = this.getWorthFile();
        final FileConfiguration data = sf.getConfig();
        data.set("Worth", Manager.worth);
        sf.save(data);
    }

    public void saveMiscData() {
        final SmartFile sf = this.getMiscFile();
        final FileConfiguration data = sf.getConfig();
        data.set("blacklist", Manager.blacklist);
        sf.save(data);
    }
}
