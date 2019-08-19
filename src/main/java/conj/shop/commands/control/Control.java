package conj.shop.commands.control;

import com.google.common.collect.Lists;
import conj.shop.addons.Placeholder;
import conj.shop.tools.auto.Autobackup;
import conj.shop.tools.auto.Autosave;
import conj.shop.base.Initiate;
import conj.shop.commands.*;
import conj.shop.data.Page;
import conj.shop.data.enums.Config;
import conj.shop.tools.InventoryCreator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Control implements CommandExecutor {
    public static void help(final Player player, final int index) {
        final String header = ChatColor.GRAY + "  === " + ChatColor.DARK_GREEN + "shop" + ChatColor.GRAY + " === " + ChatColor.DARK_GREEN + "Page " + ChatColor.GREEN + "%index%" + ChatColor.GRAY + "/" + ChatColor.GREEN + "%size%" + ChatColor.GRAY + " ===";
        final List<String> help = new ArrayList<String>();
        if (player.hasPermission("shop.use")) {
            help.add(ChatColor.GREEN + "/shop");
        }
        if (!Manager.getAvailableCommands(player, "page").isEmpty()) {
            help.add(ChatColor.GREEN + "/shop page help");
        }
        if (!Manager.getAvailableCommands(player, "worth").isEmpty()) {
            help.add(ChatColor.GREEN + "/shop worth help");
        }
        if (!Manager.getAvailableCommands(player, "blacklist").isEmpty()) {
            help.add(ChatColor.GREEN + "/shop blacklist help");
        }
        if (Initiate.citizens && !Manager.getAvailableCommands(player, "citizen").isEmpty()) {
            help.add(ChatColor.GREEN + "/shop citizen help");
        }
        if (!Manager.getAvailableCommands(player, "console").isEmpty()) {
            help.add(ChatColor.GREEN + "/shop console help");
        }
        if (player.hasPermission("shop.tools")) {
            help.add(ChatColor.GREEN + "/shop tools");
        }
        if (player.hasPermission("shop.save")) {
            help.add(ChatColor.GREEN + "/shop save");
        }
        if (player.hasPermission("shop.backup")) {
            help.add(ChatColor.GREEN + "/shop backup");
        }
        if (player.hasPermission("shop.reload")) {
            help.add(ChatColor.GREEN + "/shop reload");
        }
        if (player.hasPermission("shop.debug")) {
            help.add(ChatColor.GREEN + "/shop debug");
        }
        if (help.isEmpty()) {
            player.sendMessage(Config.PERMISSION_ERROR.toString());
            return;
        }
        list(player, help, index, header, 7);
    }

    public static void list(final Player player, final List<String> help, int index, String header, final int max) {
        if (index < 1) {
            index = 1;
        }
        final List<List<String>> split = (List<List<String>>) Lists.partition((List) help, max);
        if (split.isEmpty()) {
            index = 0;
        }
        header = header.replaceAll("%size%", new StringBuilder().append(split.size()).toString()).replaceAll("%index%", new StringBuilder().append(index).toString());
        player.sendMessage(header);
        for (int x = 0; split.size() > x; ++x) {
            if (x == index - 1) {
                for (final String s : split.get(x)) {
                    player.sendMessage(s);
                }
            }
        }
    }

    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, String[] args) {
        if (sender instanceof Player && Config.COMMAND_PLACEHOLD.isActive()) {
            args = Placeholder.placehold((Player) sender, args);
        }
        if (cmd.getName().equalsIgnoreCase("shop")) {
            if (sender.hasPermission("shop.console")) {
                final boolean console = new Console().run(sender, cmd, label, args);
                if (console) {
                    return false;
                }
            }
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                if (args.length == 0) {
                    if (!player.hasPermission("shop.use")) {
                        player.sendMessage(Config.PERMISSION_ERROR.toString());
                        return false;
                    }
                    if (Manager.get().getBlacklist().contains(player.getWorld().getName()) && !player.hasPermission("shop.blacklist.bypass." + player.getWorld().getName())) {
                        player.sendMessage(Config.BLACKLIST_ERROR.toString());
                        return false;
                    }
                    final Page p = new Manager().getPage(Config.MAIN_PAGE.toString());
                    if (p != null) {
                        p.openPage(player);
                    } else if (player.isOp()) {
                        player.sendMessage(ChatColor.RED + "Change MAIN_PAGE in the config to the page you would like to be opened when using this command");
                    }
                    return false;
                } else {
                    if (args.length >= 1) {
                        final String command = args[0];
                        if (command.equals("debug")) {
                            if (!player.hasPermission("shop.debug")) {
                                player.sendMessage(Config.PERMISSION_ERROR.toString());
                                return false;
                            }
                            Initiate.debug = !Initiate.debug;
                            player.sendMessage((Initiate.debug ? new StringBuilder().append(ChatColor.GREEN).toString() : new StringBuilder().append(ChatColor.RED).toString()) + "Debug has been " + (Initiate.debug ? "enabled" : "disabled"));
                            return false;
                        } else if (command.equals("reload")) {
                            if (!player.hasPermission("shop.reload")) {
                                player.sendMessage(Config.PERMISSION_ERROR.toString());
                                return false;
                            }
                            Config.load();
                            player.sendMessage(ChatColor.GREEN + "shop's config has been reloaded");
                            return false;
                        } else if (command.equals("save")) {
                            if (!player.hasPermission("shop.save")) {
                                player.sendMessage(Config.PERMISSION_ERROR.toString());
                                return false;
                            }
                            Autosave.save();
                            player.sendMessage(ChatColor.GREEN + "shop's data has been saved");
                            return false;
                        } else if (command.equals("backup")) {
                            if (!player.hasPermission("shop.backup")) {
                                player.sendMessage(Config.PERMISSION_ERROR.toString());
                                return false;
                            }
                            Autobackup.delete();
                            Autobackup.create();
                            player.sendMessage(ChatColor.GREEN + "shop backup created");
                            return false;
                        } else if (command.equals("console")) {
                            if (args.length == 2 && args[1].equalsIgnoreCase("help") && player.hasPermission("shop.console")) {
                                final List<String> commands = Manager.getAvailableCommands(player, "console");
                                final String header = ChatColor.GRAY + "  === " + ChatColor.DARK_GREEN + "shop Console Help" + ChatColor.GRAY + " === " + ChatColor.DARK_GREEN + "Page " + ChatColor.GREEN + "%index%" + ChatColor.GRAY + "/" + ChatColor.GREEN + "%size%" + ChatColor.GRAY + " ===";
                                list(player, commands, 1, header, 10);
                                return false;
                            }
                        } else {
                            if (command.equals("page")) {
                                new PageManagement().run(player, cmd, label, args);
                                return false;
                            }
                            if (command.equals("blacklist")) {
                                new BlacklistManagement().run(player, cmd, label, args);
                                return false;
                            }
                            if (command.equals("worth")) {
                                new WorthManagement().run(player, cmd, label, args);
                                return false;
                            }
                            if (command.equals("citizen")) {
                                if (Initiate.citizens) {
                                    new CitizensManagement().run(player, cmd, label, args);
                                    return false;
                                }
                                if (!player.hasPermission("shop.citizen.page")) {
                                    player.sendMessage(Config.PERMISSION_ERROR.toString());
                                    return false;
                                }
                                player.sendMessage(ChatColor.RED + "Citizens is not active");
                                return false;
                            } else {
                                if (command.equals("help")) {
                                    int index = 1;
                                    if (args.length == 2) {
                                        try {
                                            index = Integer.parseInt(args[1]);
                                        } catch (NumberFormatException ex) {
                                        }
                                    }
                                    help(player, index);
                                    return false;
                                }
                                if (!command.equals("tools")) {
                                    help(player, 1);
                                    return false;
                                }
                                if (player.hasPermission("shop.tools")) {
                                    final InventoryCreator inv = new InventoryCreator(ChatColor.BLUE + "shop Tools", 1);
                                    inv.setItem(0, Material.STICK, ChatColor.DARK_RED + "Entity Remover");
                                    inv.addLore(0, ChatColor.RED + "Right-click an entity to remove it");
                                    player.openInventory(inv.getInventory());
                                    return false;
                                }
                            }
                        }
                    }
                    help(player, 1);
                }
            }
        }
        return false;
    }
}
