package conj.Shop.data;

import conj.Shop.base.Initiate;
import conj.Shop.enums.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Update {
    public static void runUpdate(final int id) {
        if (id == 1) {
            final File file = new File(((Initiate) Initiate.getPlugin((Class) Initiate.class)).getDataFolder() + "/data/page_storage.yml");
            if (file.exists()) {
                final FileConfiguration data = (FileConfiguration) YamlConfiguration.loadConfiguration(file);
                for (final String page : data.getConfigurationSection("").getKeys(false)) {
                    Bukkit.getLogger().info("Converting page file " + page);
                    final Page p = new Page(page);
                    p.title = data.getString(String.valueOf(p.getID()) + ".title");
                    p.size = data.getInt(String.valueOf(p.getID()) + ".size");
                    p.type = data.getInt(String.valueOf(p.getID()) + ".type");
                    p.gui = data.getBoolean(String.valueOf(p.getID()) + ".gui");
                    p.slots = (List<Integer>) data.get(String.valueOf(p.getID()) + ".slots");
                    p.items = (List<HashMap<Map<String, Object>, Map<String, Object>>>) data.get(String.valueOf(p.getID()) + ".items");
                    final HashMap<Integer, Double> cost = loadDouble(p, data, "cost");
                    final HashMap<Integer, Double> sell = loadDouble(p, data, "sell");
                    final HashMap<Integer, List<String>> command = loadStringMap(p, data, "command");
                    final HashMap<Integer, Integer> cooldown = loadInt(p, data, "cooldown");
                    final HashMap<Integer, List<String>> hidepermission = loadStringMap(p, data, "hidepermission");
                    final HashMap<Integer, List<String>> permission = loadStringMap(p, data, "permission");
                    final HashMap<Integer, HashMap<String, Long>> cd = new HashMap<Integer, HashMap<String, Long>>();
                    final HashMap<Integer, Function> functions = new HashMap<Integer, Function>();
                    final HashMap<Integer, GUIFunction> guifunctions = new HashMap<Integer, GUIFunction>();
                    final HashMap<Integer, HashMap<String, List<String>>> messages = new HashMap<Integer, HashMap<String, List<String>>>();
                    final HashMap<Integer, Hidemode> visibility = new HashMap<Integer, Hidemode>();
                    if (data.getConfigurationSection(String.valueOf(p.getID()) + ".cd") != null) {
                        for (final String slot : data.getConfigurationSection(String.valueOf(p.getID()) + ".cd").getKeys(false)) {
                            if (data.getConfigurationSection(String.valueOf(p.getID()) + ".cd." + slot) == null) {
                                continue;
                            }
                            for (final String player : data.getConfigurationSection(String.valueOf(p.getID()) + ".cd." + slot).getKeys(false)) {
                                if (data.get(String.valueOf(p.getID()) + ".cd." + slot + "." + player) == null) {
                                    continue;
                                }
                                final HashMap<String, Long> hash = new HashMap<String, Long>();
                                hash.put(player, data.getLong(String.valueOf(p.getID()) + ".cd." + slot + "." + player));
                                cd.put(Integer.parseInt(slot), hash);
                            }
                        }
                    }
                    if (data.getConfigurationSection(String.valueOf(p.getID()) + ".function") != null) {
                        for (final String get : data.getConfigurationSection(String.valueOf(p.getID()) + ".function").getKeys(false)) {
                            if (data.get(String.valueOf(p.getID()) + ".function." + get) != null) {
                                functions.put(Integer.parseInt(get), Function.fromString(data.getString(String.valueOf(p.getID()) + ".function." + get)));
                            }
                        }
                    }
                    if (data.getConfigurationSection(String.valueOf(p.getID()) + ".guifunction") != null) {
                        for (final String get : data.getConfigurationSection(String.valueOf(p.getID()) + ".guifunction").getKeys(false)) {
                            if (data.get(String.valueOf(p.getID()) + ".guifunction." + get) != null) {
                                guifunctions.put(Integer.parseInt(get), GUIFunction.fromString(data.getString(String.valueOf(p.getID()) + ".guifunction." + get)));
                            }
                        }
                    }
                    if (data.getConfigurationSection(String.valueOf(p.getID()) + ".messages") != null) {
                        for (final String numeral : data.getConfigurationSection(String.valueOf(p.getID()) + ".messages").getKeys(false)) {
                            if (data.getConfigurationSection(String.valueOf(p.getID()) + ".messages." + numeral) == null) {
                                continue;
                            }
                            for (final String t : data.getConfigurationSection(String.valueOf(p.getID()) + ".messages." + numeral).getKeys(false)) {
                                if (data.getStringList(String.valueOf(p.getID()) + ".messages." + numeral + "." + t) == null) {
                                    continue;
                                }
                                final HashMap<String, List<String>> hash2 = new HashMap<String, List<String>>();
                                hash2.put(MessageType.fromString(t).toString(), data.getStringList(String.valueOf(p.getID()) + ".messages." + numeral + "." + t));
                                messages.put(Integer.parseInt(numeral), hash2);
                            }
                        }
                    }
                    if (data.getConfigurationSection(String.valueOf(p.getID()) + ".visibility") != null) {
                        for (final String get : data.getConfigurationSection(String.valueOf(p.getID()) + ".visibility").getKeys(false)) {
                            if (data.get(String.valueOf(p.getID()) + ".visibility." + get) != null) {
                                visibility.put(Integer.parseInt(get), Hidemode.fromString(data.getString(String.valueOf(p.getID()) + ".visibility." + get)));
                            }
                        }
                    }
                    for (int i = 0; i < 54; ++i) {
                        final PageSlot ps = new PageSlot(page, i);
                        if (cost.get(i) != null) {
                            ps.setCost(cost.get(i));
                        }
                        if (sell.get(i) != null) {
                            ps.setSell(sell.get(i));
                        }
                        if (command.get(i) != null) {
                            ps.setCommands(command.get(i));
                        }
                        if (cooldown.get(i) != null) {
                            ps.setCooldown(cooldown.get(i));
                        }
                        if (hidepermission.get(i) != null) {
                            for (final String s : hidepermission.get(i)) {
                                ps.addHidePermission(s);
                            }
                        }
                        if (permission.get(i) != null) {
                            for (final String s : permission.get(i)) {
                                ps.addPermission(s);
                            }
                        }
                        if (cd.get(i) != null) {
                            for (final Map.Entry<String, Long> v : cd.get(i).entrySet()) {
                                ps.getCooldowns().put(v.getKey(), v.getValue());
                            }
                        }
                        if (functions.get(i) != null) {
                            ps.setFunction(functions.get(i));
                        }
                        if (guifunctions.get(i) != null) {
                            ps.setGUIFunction(guifunctions.get(i));
                        }
                        if (visibility.get(i) != null) {
                            ps.setHidemode(visibility.get(i));
                        }
                        if (messages.get(i) != null) {
                            for (final Map.Entry<String, List<String>> v2 : messages.get(i).entrySet()) {
                                ps.setMessage(v2.getKey(), v2.getValue());
                            }
                        }
                        p.addPageSlot(ps);
                    }
                    p.create();
                    p.saveData();
                    Bukkit.getLogger().info("Page " + page + " conversion complete");
                }
                final File backup = new File(((Initiate) Initiate.getPlugin((Class) Initiate.class)).getDataFolder() + "/backup/page_storage-2.0.8.yml");
                if (!backup.exists()) {
                    try {
                        backup.createNewFile();
                    } catch (IOException ex) {
                    }
                }
                FileUtil.copy(file, backup);
                file.delete();
            }
        } else if (id == 2) {
            final Plugin plugin = (Plugin) Initiate.getPlugin((Class) Initiate.class);
            boolean changes = false;
            for (final String v3 : plugin.getConfig().getConfigurationSection("").getKeys(false)) {
                boolean b = false;
                Config[] values;
                for (int length = (values = Config.values()).length, j = 0; j < length; ++j) {
                    final Config c = values[j];
                    if (v3.equalsIgnoreCase(c.getBase())) {
                        b = true;
                        break;
                    }
                }
                if (b) {
                    continue;
                }
                changes = true;
                plugin.getConfig().set(v3, (Object) null);
            }
            plugin.saveConfig();
            if (changes) {
                Config.load();
            }
        }
    }

    public static HashMap<Integer, List<String>> loadStringMap(final Page page, final FileConfiguration data, final String base) {
        final HashMap<Integer, List<String>> value = new HashMap<Integer, List<String>>();
        List<String> list = new ArrayList<String>();
        if (data.getConfigurationSection(String.valueOf(page.getID()) + "." + base) != null) {
            for (final String get : data.getConfigurationSection(String.valueOf(page.getID()) + "." + base).getKeys(false)) {
                final int id = Integer.parseInt(get);
                if (data.getList(String.valueOf(page.getID()) + "." + base + "." + get) != null) {
                    list = (List<String>) data.getList(String.valueOf(page.getID()) + "." + base + "." + get);
                    value.put(id, list);
                }
            }
        }
        return value;
    }

    public static HashMap<Integer, Double> loadDouble(final Page page, final FileConfiguration data, final String base) {
        final HashMap<Integer, Double> value = new HashMap<Integer, Double>();
        if (data.get(String.valueOf(page.getID()) + "." + base) != null) {
            for (final String get : data.getConfigurationSection(String.valueOf(page.getID()) + "." + base).getKeys(false)) {
                if (data.get(String.valueOf(page.getID()) + "." + base + "." + get) != null) {
                    value.put(Integer.parseInt(get), data.getDouble(String.valueOf(page.getID()) + "." + base + "." + get));
                }
            }
        }
        return value;
    }

    public static HashMap<Integer, Integer> loadInt(final Page page, final FileConfiguration data, final String base) {
        final HashMap<Integer, Integer> value = new HashMap<Integer, Integer>();
        if (data.get(String.valueOf(page.getID()) + "." + base) != null) {
            for (final String get : data.getConfigurationSection(String.valueOf(page.getID()) + "." + base).getKeys(false)) {
                if (data.get(String.valueOf(page.getID()) + "." + base + "." + get) != null) {
                    value.put(Integer.parseInt(get), data.getInt(String.valueOf(page.getID()) + "." + base + "." + get));
                }
            }
        }
        return value;
    }
}
