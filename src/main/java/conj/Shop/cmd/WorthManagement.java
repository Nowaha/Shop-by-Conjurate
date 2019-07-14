package conj.Shop.cmd;

import conj.Shop.control.Control;
import conj.Shop.control.Manager;
import conj.Shop.enums.Config;
import conj.Shop.interaction.Editor;
import conj.Shop.tools.DoubleUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class WorthManagement {
    public void run(final Player player, final Command cmd, final String label, final String[] args) {
        if (args.length == 1) {
            if (!player.hasPermission("shop.worth.item")) {
                player.sendMessage(Config.PERMISSION_ERROR.toString());
                return;
            }
            if (player.getItemInHand() != null && !player.getItemInHand().getType().equals((Object) Material.AIR)) {
                final double worth = Manager.get().getWorth(player.getItemInHand());
                final double fworth = Manager.get().getFlatWorth(player.getItemInHand());
                player.sendMessage(ChatColor.BLUE + Editor.getItemName(player.getItemInHand()) + ChatColor.DARK_GRAY + ":" + ChatColor.AQUA + player.getItemInHand().getDurability() + ChatColor.GRAY + " : " + ChatColor.GREEN + worth + ChatColor.DARK_GRAY + " (" + ChatColor.GRAY + "x" + ChatColor.GOLD + player.getItemInHand().getAmount() + ChatColor.DARK_GRAY + ")" + ChatColor.GRAY + " : " + ChatColor.GREEN + fworth + ChatColor.DARK_GRAY + " (" + ChatColor.GRAY + "x" + ChatColor.GOLD + "1" + ChatColor.DARK_GRAY + ")");
                return;
            }
            player.sendMessage(ChatColor.RED + "You need an item in your hand to use this command");
        } else {
            if (args.length >= 2) {
                final String command = args[1];
                if (command.equalsIgnoreCase("set")) {
                    if (!player.hasPermission("shop.worth.set")) {
                        player.sendMessage(Config.PERMISSION_ERROR.toString());
                        return;
                    }
                    if (args.length == 3) {
                        if (player.getItemInHand() != null && !player.getItemInHand().getType().equals((Object) Material.AIR)) {
                            final String str = args[2];
                            double amount = 0.0;
                            try {
                                amount = Double.parseDouble(str);
                            } catch (NumberFormatException exception) {
                                player.sendMessage(ChatColor.RED + "Invalid amount");
                                return;
                            }
                            if (amount < 0.0) {
                                amount = 0.0;
                            }
                            Manager.get().setWorth(player.getItemInHand(), amount, true);
                            player.sendMessage(ChatColor.GREEN + "Worth of " + Editor.getItemName(player.getItemInHand()) + ChatColor.DARK_GRAY + ":" + ChatColor.AQUA + player.getItemInHand().getDurability() + ChatColor.GREEN + " has been set to " + DoubleUtil.toString(amount));
                            return;
                        }
                        player.sendMessage(ChatColor.RED + "You need an item in your hand to use this command");
                    } else {
                        player.sendMessage(ChatColor.GRAY + "/shop worth set <amount>");
                    }
                } else if (command.equalsIgnoreCase("list")) {
                    if (!player.hasPermission("shop.worth.list")) {
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
                    final String header = ChatColor.GRAY + "  === " + ChatColor.DARK_GREEN + "Shop Worth" + ChatColor.GRAY + " === " + ChatColor.DARK_GREEN + "Page " + ChatColor.GREEN + "%index%" + ChatColor.GRAY + "/" + ChatColor.GREEN + "%size%" + ChatColor.GRAY + " ===";
                    final List<String> help = new ArrayList<String>();
                    Material[] values;
                    for (int length = (values = Material.values()).length, i = 0; i < length; ++i) {
                        final Material m = values[i];
                        final ItemStack item = new ItemStack(m);
                        item.setAmount(1);
                        final double worth2 = Manager.get().getWorth(item);
                        if (worth2 > 0.0) {
                            help.add(ChatColor.BLUE + Editor.getItemName(item) + ChatColor.GRAY + " : " + ChatColor.GREEN + worth2);
                        }
                    }
                    Control.list(player, help, index, header, 9);
                } else if (command.equalsIgnoreCase("help")) {
                    final List<String> help2 = Manager.getAvailableCommands(player, "worth");
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
                    final String header2 = ChatColor.GRAY + "  === " + ChatColor.DARK_GREEN + "Shop Worth Help" + ChatColor.GRAY + " === " + ChatColor.DARK_GREEN + "Page " + ChatColor.GREEN + "%index%" + ChatColor.GRAY + "/" + ChatColor.GREEN + "%size%" + ChatColor.GRAY + " ===";
                    Control.list(player, help2, index2, header2, 7);
                } else if (!Manager.getAvailableCommands(player, "worth").isEmpty()) {
                    player.sendMessage(ChatColor.GRAY + "/shop worth help");
                } else {
                    player.sendMessage(Config.PERMISSION_ERROR.toString());
                }
                return;
            }
            if (!Manager.getAvailableCommands(player, "worth").isEmpty()) {
                player.sendMessage(ChatColor.GRAY + "/shop worth help");
            } else {
                player.sendMessage(Config.PERMISSION_ERROR.toString());
            }
        }
    }
}
