package conj.Shop.cmd;

import conj.Shop.base.Initiate;
import conj.Shop.control.Manager;
import conj.Shop.data.Page;
import conj.Shop.enums.Config;
import conj.Shop.tools.Placeholder;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Console {
    public boolean run(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        final Manager manager = new Manager();
        final int length = args.length;
        if (args.length >= 2) {
            final String command = args[0];
            if (command.equalsIgnoreCase("open")) {
                if (!sender.hasPermission("shop.console.page")) {
                    sender.sendMessage(Config.PERMISSION_ERROR.toString());
                    return true;
                }
                if (args.length == 4) {
                    final String sub = args[1];
                    if (sub.equalsIgnoreCase("page")) {
                        final String playername = args[3];
                        final String pagename = args[2];
                        final Player player = Bukkit.getPlayer(playername);
                        final Page page = manager.getPage(pagename);
                        if (page != null && player != null) {
                            page.openPage(player);
                        }
                        return true;
                    }
                }
            } else if (command.equalsIgnoreCase("page")) {
                if (!sender.hasPermission("shop.console.page")) {
                    sender.sendMessage(Config.PERMISSION_ERROR.toString());
                    return true;
                }
                if (args.length == 4) {
                    final String sub = args[1];
                    if (sub.equalsIgnoreCase("open")) {
                        final String playername = args[3];
                        final String pagename = args[2];
                        final Player player = Bukkit.getPlayer(playername);
                        final Page page = manager.getPage(pagename);
                        if (page != null && player != null) {
                            page.openPage(player);
                        }
                        return true;
                    }
                } else if (args.length == 6) {
                    final String sub = args[1];
                    if (sub.equalsIgnoreCase("move")) {
                        final String pagename2 = args[2];
                        int from = 0;
                        int to = 0;
                        try {
                            from = Integer.parseInt(args[3]);
                            to = Integer.parseInt(args[4]);
                        } catch (NumberFormatException ne) {
                            return true;
                        }
                        final Page page = manager.getPage(pagename2);
                        if (page != null) {
                            if (args[5].equalsIgnoreCase("soft")) {
                                page.moveItemSoft(from, to);
                            } else if (args[5].equalsIgnoreCase("hard")) {
                                page.moveItem(from, to);
                            }
                            page.updateViewers(true);
                        }
                    }
                }
            } else if (command.equalsIgnoreCase("help")) {
                if (!(sender instanceof Player)) {
                    return true;
                }
            } else {
                if (command.equalsIgnoreCase("teleport")) {
                    if (args.length < 6) {
                        return false;
                    }
                    final String pl = args[1];
                    final String w = args[2];
                    final String xs = args[3];
                    final String ys = args[4];
                    final String zs = args[5];
                    if (Bukkit.getPlayer(pl) == null) {
                        return true;
                    }
                    final Player player2 = Bukkit.getPlayer(pl);
                    if (Bukkit.getWorld(w) == null) {
                        return true;
                    }
                    final World world = Bukkit.getWorld(w);
                    try {
                        final double x = Double.parseDouble(xs);
                        final double y = Double.parseDouble(ys);
                        final double z = Double.parseDouble(zs);
                        Float yaw = player2.getLocation().getYaw();
                        Float pitch = player2.getLocation().getPitch();
                        if (args.length >= 7) {
                            yaw = Float.parseFloat(args[6]);
                        }
                        if (args.length >= 8) {
                            pitch = Float.parseFloat(args[7]);
                        }
                        final Location location = new Location(world, x, y, z, yaw - 180.0f, (float) pitch);
                        player2.teleport(location);
                        return true;
                    } catch (NumberFormatException ex) {
                        return false;
                    }
                }
                if (command.equalsIgnoreCase("send")) {
                    if (args.length >= 3) {
                        final String arg2 = args[1];
                        final String arg3 = args[2];
                        if (arg2.equalsIgnoreCase("message")) {
                            if (args.length >= 4) {
                                final Player player3 = Bukkit.getPlayer(arg3);
                                final StringBuilder sb = new StringBuilder();
                                for (int x2 = 3; x2 < args.length; ++x2) {
                                    sb.append(args[x2]).append(" ");
                                }
                                final String text = sb.toString().trim();
                                if (player3 != null) {
                                    player3.sendMessage(Placeholder.placehold(player3, text));
                                }
                                return true;
                            }
                        } else if (arg2.equalsIgnoreCase("broadcast")) {
                            final StringBuilder sb2 = new StringBuilder();
                            for (int x3 = 2; x3 < args.length; ++x3) {
                                sb2.append(args[x3]).append(" ");
                            }
                            final String text2 = sb2.toString().trim();
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', text2));
                            return true;
                        }
                    }
                } else {
                    if (command.equalsIgnoreCase("take")) {
                        if (args.length < 4) {
                            return false;
                        }
                        final String arg2 = args[1];
                        final String arg3 = args[2];
                        if (!arg2.equalsIgnoreCase("money")) {
                            return false;
                        }
                        final String pl2 = args[3];
                        final Player player = Bukkit.getPlayer(pl2);
                        if (player == null) {
                            return false;
                        }
                        try {
                            final OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());
                            final double amount = Double.parseDouble(arg3);
                            Initiate.econ.withdrawPlayer(op, amount);
                            return true;
                        } catch (NumberFormatException ex2) {
                            return false;
                        }
                    }
                    if (command.equalsIgnoreCase("cooldown")) {
                        if (args.length == 3) {
                            final String arg2 = args[1];
                            final String arg3 = args[2];
                            if (arg2.equalsIgnoreCase("clear")) {
                                final Player player3 = Bukkit.getPlayer(arg3);
                                if (player3 != null) {
                                    for (final Page p : Manager.get().getPages()) {
                                        p.uncooldown(player3);
                                    }
                                    return true;
                                }
                            }
                        }
                    } else if (command.equalsIgnoreCase("close")) {
                        if (args.length >= 3) {
                            final String arg2 = args[1];
                            final String arg3 = args[2];
                            if (arg2.equalsIgnoreCase("inventory")) {
                                final Player player3 = Bukkit.getPlayer(arg3);
                                if (player3 != null) {
                                    player3.closeInventory();
                                    return true;
                                }
                            }
                        }
                    } else if (command.equalsIgnoreCase("reload") && !(sender instanceof Player)) {
                        Config.load();
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
