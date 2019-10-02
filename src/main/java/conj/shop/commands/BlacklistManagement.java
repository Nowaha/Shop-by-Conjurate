package conj.shop.commands;

import conj.shop.commands.control.Control;
import conj.shop.commands.control.Manager;
import conj.shop.data.enums.Config;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BlacklistManagement {
    public void run(final Player player, final Command cmd, final String label, final String[] args) {
        final Manager manager = new Manager();
        if (args.length < 2) {
            if (!Manager.getAvailableCommands(player, "blacklist").isEmpty()) {
                player.sendMessage(ChatColor.GRAY + "/shop blacklist help");
            } else {
                player.sendMessage(Config.PERMISSION_ERROR.toString());
            }
            return;
        }
        final String command = args[1];
        if (command.equalsIgnoreCase("add")) {
            if (!player.hasPermission("shop.blacklist.add")) {
                player.sendMessage(Config.PERMISSION_ERROR.toString());
                return;
            }
            final World world = player.getLocation().getWorld();
            if (manager.blacklistContains(world.getName())) {
                player.sendMessage(ChatColor.RED + world.getName() + " is already on the blacklist");
                return;
            }
            manager.blacklistAdd(world.getName());
            player.sendMessage(ChatColor.GREEN + world.getName() + " has been added to the blacklist");
        } else {
            if (!command.equalsIgnoreCase("remove")) {
                if (command.equalsIgnoreCase("list")) {
                    if (!player.hasPermission("shop.blacklist.list")) {
                        player.sendMessage(Config.PERMISSION_ERROR.toString());
                        return;
                    }
                    int index = 1;
                    if (args.length == 3) {
                        try {
                            index = Integer.parseInt(args[2]);
                        } catch (NumberFormatException ex) {
                        }
                    }
                    final String header = ChatColor.GRAY + "  === " + ChatColor.DARK_GREEN + "shop Blacklist" + ChatColor.GRAY + " === " + ChatColor.DARK_GREEN + "Page " + ChatColor.GREEN + "%index%" + ChatColor.GRAY + "/" + ChatColor.GREEN + "%size%" + ChatColor.GRAY + " ===";
                    final List<String> help = new ArrayList<String>();
                    for (final String world2 : Manager.get().getBlacklist()) {
                        help.add(ChatColor.GRAY + "- " + ChatColor.GOLD + world2);
                    }
                    Control.list(player, help, index, header, 9);
                } else if (command.equalsIgnoreCase("help")) {
                    final List<String> help2 = Manager.getAvailableCommands(player, "blacklist");
                    if (help2.isEmpty()) {
                        player.sendMessage(Config.PERMISSION_ERROR.toString());
                        return;
                    }
                    int index2 = 1;
                    if (args.length == 3) {
                        try {
                            index2 = Integer.parseInt(args[2]);
                        } catch (NumberFormatException ex2) {
                        }
                    }
                    final String header2 = ChatColor.GRAY + "  === " + ChatColor.DARK_GREEN + "shop Blacklist Help" + ChatColor.GRAY + " === " + ChatColor.DARK_GREEN + "Page " + ChatColor.GREEN + "%index%" + ChatColor.GRAY + "/" + ChatColor.GREEN + "%size%" + ChatColor.GRAY + " ===";
                    Control.list(player, help2, index2, header2, 7);
                }
                return;
            }
            if (!player.hasPermission("shop.blacklist.remove")) {
                player.sendMessage(Config.PERMISSION_ERROR.toString());
                return;
            }
            final World world = player.getLocation().getWorld();
            if (!manager.blacklistContains(world.getName())) {
                player.sendMessage(ChatColor.RED + world.getName() + " is not on the blacklist");
                return;
            }
            manager.blacklistRemove(world.getName());
            player.sendMessage(ChatColor.GREEN + world.getName() + " has been removed from the blacklist");
        }
    }
}
