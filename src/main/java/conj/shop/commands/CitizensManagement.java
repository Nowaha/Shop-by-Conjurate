package conj.shop.commands;

import conj.shop.commands.control.Control;
import conj.shop.commands.control.Manager;
import conj.shop.data.enums.Config;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.List;

public class CitizensManagement {
    public void run(final Player player, final Command cmd, final String label, final String[] args) {
        final Manager manager = new Manager();
        if (args.length < 2) {
            if (!Manager.getAvailableCommands(player, "citizen").isEmpty()) {
                player.sendMessage(ChatColor.GRAY + "/shop citizen help");
            } else {
                player.sendMessage(Config.PERMISSION_ERROR.toString());
            }
            return;
        }
        final String command = args[1];
        if (!command.equalsIgnoreCase("page")) {
            if (command.equalsIgnoreCase("permission")) {
                if (!player.hasPermission("shop.citizen.permission.add") && !player.hasPermission("shop.citizen.permission.remove") && !player.hasPermission("shop.citizen.permission.clear")) {
                    player.sendMessage(Config.PERMISSION_ERROR.toString());
                    return;
                }
                final NPC npc = CitizensAPI.getDefaultNPCSelector().getSelected(player);
                if (npc != null) {
                    if (args.length > 3) {
                        final String sub = args[2];
                        if (sub.equalsIgnoreCase("add")) {
                            if (!player.hasPermission("shop.citizen.permission.add")) {
                                player.sendMessage(Config.PERMISSION_ERROR.toString());
                                return;
                            }
                            final StringBuilder sb = new StringBuilder();
                            for (int i = 3; i < args.length; ++i) {
                                sb.append(args[i]).append(" ");
                            }
                            final String text = sb.toString().trim();
                            final boolean added = manager.addCitizenPermission(npc.getId(), text);
                            player.sendMessage(added ? (ChatColor.GREEN + "Added permission " + text + " to " + npc.getFullName()) : (ChatColor.RED + "Failed to add permission " + text + " to " + npc.getFullName()));
                            return;
                        } else if (sub.equalsIgnoreCase("remove")) {
                            if (!player.hasPermission("shop.citizen.permission.remove")) {
                                player.sendMessage(Config.PERMISSION_ERROR.toString());
                                return;
                            }
                            final StringBuilder sb = new StringBuilder();
                            for (int i = 3; i < args.length; ++i) {
                                sb.append(args[i]).append(" ");
                            }
                            final String text = sb.toString().trim();
                            final boolean removed = manager.removeCitizenPermission(npc.getId(), text);
                            player.sendMessage(removed ? (ChatColor.GREEN + "Removed permission " + text + " from " + npc.getFullName()) : (ChatColor.RED + "Failed to remove permission " + text + " from " + npc.getFullName()));
                            return;
                        }
                    } else if (args.length == 3) {
                        final String sub = args[2];
                        if (sub.equalsIgnoreCase("clear")) {
                            if (!player.hasPermission("shop.citizen.permission.clear")) {
                                player.sendMessage(Config.PERMISSION_ERROR.toString());
                                return;
                            }
                            manager.clearCitizenPermissions(npc.getId());
                            player.sendMessage(ChatColor.GREEN + "Permissions of " + npc.getFullName() + " have been cleared");
                            return;
                        }
                    }
                    final List<String> permissions = manager.getCitizenPermissions(npc.getId());
                    player.sendMessage(ChatColor.DARK_GREEN + npc.getFullName() + "'s permissions");
                    for (final String p : permissions) {
                        player.sendMessage(ChatColor.GRAY + "- " + ChatColor.GREEN + p);
                    }
                    return;
                }
                player.sendMessage(ChatColor.RED + "You need to select an NPC");
            } else if (command.equalsIgnoreCase("help")) {
                final List<String> help = Manager.getAvailableCommands(player, "citizen");
                if (help.isEmpty()) {
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
                final String header = ChatColor.GRAY + "  === " + ChatColor.DARK_GREEN + "shop Citizen Help" + ChatColor.GRAY + " === " + ChatColor.DARK_GREEN + "Page " + ChatColor.GREEN + "%index%" + ChatColor.GRAY + "/" + ChatColor.GREEN + "%size%" + ChatColor.GRAY + " ===";
                Control.list(player, help, index, header, 7);
                return;
            }
            if (!Manager.getAvailableCommands(player, "citizen").isEmpty()) {
                player.sendMessage(ChatColor.GRAY + "/shop citizen help");
            } else {
                player.sendMessage(Config.PERMISSION_ERROR.toString());
            }
            return;
        }
        if (!player.hasPermission("shop.citizen.page")) {
            player.sendMessage(Config.PERMISSION_ERROR.toString());
            return;
        }
        final NPC npc = CitizensAPI.getDefaultNPCSelector().getSelected(player);
        if (npc == null) {
            player.sendMessage(ChatColor.RED + "You need to select an NPC");
            return;
        }
        if (args.length != 3) {
            player.sendMessage(ChatColor.GRAY + "/shop citizen page <page>");
            return;
        }
        final String pagename = args[2];
        if (manager.getPage(pagename) != null) {
            manager.setCitizenPage(npc.getId(), pagename);
            player.sendMessage(ChatColor.GREEN + "Page of " + npc.getName() + ChatColor.GREEN + " has been set to " + manager.getPage(pagename).getID());
            return;
        }
        player.sendMessage(ChatColor.RED + pagename + " does not exist");
    }
}
