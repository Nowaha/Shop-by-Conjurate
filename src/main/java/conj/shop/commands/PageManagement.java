package conj.shop.commands;

import conj.shop.commands.control.Control;
import conj.shop.commands.control.Manager;
import conj.shop.data.Page;
import conj.shop.data.PageSlot;
import conj.shop.data.enums.Config;
import conj.shop.data.enums.Function;
import conj.shop.events.listeners.Editor;
import conj.shop.events.listeners.PageProperties;
import conj.shop.tools.DoubleUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PageManagement {
    public static boolean isManaging(final Player player, final boolean silent) {
        if (Manager.edit.containsKey(player.getName())) {
            final Manager manager = new Manager();
            final String id = Manager.edit.get(player.getName());
            if (manager.getPage(id) != null) {
                return true;
            }
            player.sendMessage(ChatColor.RED + "You need to select a page to manage. /shop page manage <page>");
            Manager.edit.remove(player.getName());
        } else {
            player.sendMessage(ChatColor.RED + "You need to select a page to manage. /shop page manage <page>");
        }
        return false;
    }

    public static Page getPage(final Player player) {
        if (Manager.edit.containsKey(player.getName())) {
            final Manager manager = new Manager();
            final String id = Manager.edit.get(player.getName());
            return manager.getPage(id);
        }
        return null;
    }

    public void run(final Player player, final Command cmd, final String label, final String[] args) {
        final Manager manager = new Manager();
        if (args.length == 1) {
            if (!player.hasPermission("shop.page.manage")) {
                player.sendMessage(Config.PERMISSION_ERROR.toString());
                return;
            }
            if (isManaging(player, false)) {
                player.sendMessage(ChatColor.GREEN + "You are managing " + manager.getEditorPage(player));
            }
        } else {
            if (args.length >= 2) {
                final String command = args[1];
                if (command.equalsIgnoreCase("edit")) {
                    if (!player.hasPermission("shop.page.edit")) {
                        player.sendMessage(Config.PERMISSION_ERROR.toString());
                        return;
                    }
                    String spage = null;
                    if (args.length == 3) {
                        spage = args[2];
                    }
                    if (spage != null) {
                        final Page page = manager.getPage(spage);
                        if (page != null) {
                            page.openEditor(player);
                            return;
                        }
                    }
                    if (isManaging(player, false)) {
                        final Page page = manager.getPage(manager.getEditorPage(player));
                        if (page != null) {
                            page.openEditor(player);
                        }
                    }
                } else if (command.equalsIgnoreCase("add")) {
                    if (!player.hasPermission("shop.page.add")) {
                        player.sendMessage(Config.PERMISSION_ERROR.toString());
                        return;
                    }
                    if (args.length >= 3) {
                        if (!isManaging(player, false)) {
                            return;
                        }
                        final Page page2 = manager.getPage(manager.getEditorPage(player));
                        final int freeslot = page2.getInventory().firstEmpty();
                        if (freeslot == -1) {
                            player.sendMessage(ChatColor.RED + "The page is full");
                            return;
                        }
                        if (!this.hasItemInHand(player)) {
                            player.sendMessage(ChatColor.RED + "You need an item in your hand to use this command");
                            return;
                        }
                        final String amountstring = args[2];
                        double amount = 0.0;
                        double sell = 0.0;
                        try {
                            amount = Double.parseDouble(amountstring);
                        } catch (NumberFormatException ex) {
                        }
                        if (args.length >= 4) {
                            try {
                                sell = Double.parseDouble(args[3]);
                            } catch (NumberFormatException ex2) {
                            }
                        }
                        if (page2 != null) {
                            page2.setItem(freeslot, player.getInventory().getItemInMainHand());
                            final PageSlot ps = page2.getPageSlot(freeslot);
                            if (amount > 0.0) {
                                ps.setFunction(Function.BUY);
                            }
                            if (amount == 0.0 && sell > 0.0) {
                                ps.setFunction(Function.SELL);
                            }
                            if (amount == 0.0 && sell == 0.0) {
                                ps.setFunction(Function.NONE);
                            }
                            if (amount > 0.0) {
                                ps.setCost(amount);
                            }
                            if (sell > 0.0) {
                                ps.setSell(sell);
                            }
                            player.sendMessage(ChatColor.GREEN + Editor.getItemName(player.getInventory().getItemInMainHand()) + " has been added to " + page2.getID() + " with the cost as " + DoubleUtil.toString(amount) + ", sell as " + DoubleUtil.toString(sell) + ", and function as " + ps.getFunction().toString());
                        }
                    } else {
                        player.sendMessage(ChatColor.GRAY + "/shop page add <cost> <sell>");
                    }
                } else if (command.equalsIgnoreCase("title")) {
                    if (!player.hasPermission("shop.page.title")) {
                        player.sendMessage(Config.PERMISSION_ERROR.toString());
                        return;
                    }
                    if (isManaging(player, false)) {
                        if (args.length >= 3) {
                            final Page page2 = manager.getPage(manager.getEditorPage(player));
                            final StringBuilder sb = new StringBuilder();
                            for (int i = 2; i < args.length; ++i) {
                                sb.append(args[i]).append(" ");
                            }
                            final String text = ChatColor.translateAlternateColorCodes('&', sb.toString().trim());
                            page2.setTitle(text);
                            player.sendMessage(ChatColor.GREEN + "Title of " + page2.getID() + " has been set to " + text);
                            return;
                        }
                        player.sendMessage(ChatColor.GRAY + "/shop page title <title>");
                    }
                } else if (command.equalsIgnoreCase("copy")) {
                    if (!player.hasPermission("shop.page.copy")) {
                        player.sendMessage(Config.PERMISSION_ERROR.toString());
                        return;
                    }
                    if (isManaging(player, false)) {
                        if (args.length == 3) {
                            final Page page2 = manager.getPage(manager.getEditorPage(player));
                            final String pagename2 = args[2].toUpperCase();
                            if (manager.getPage(pagename2) != null) {
                                page2.copy(manager.getPage(pagename2));
                                player.sendMessage(ChatColor.GREEN + pagename2 + " has been copied to " + page2.getID());
                            } else {
                                player.sendMessage(ChatColor.RED + pagename2 + " does not exist");
                            }
                            return;
                        }
                        player.sendMessage(ChatColor.GRAY + "/shop page copy <page>");
                    }
                } else if (command.equalsIgnoreCase("recover")) {
                    if (!player.hasPermission("shop.page.recover")) {
                        player.sendMessage(Config.PERMISSION_ERROR.toString());
                        return;
                    }
                    if (manager.hasRecoveryPage(player)) {
                        final Page recover = manager.getRecoveryPage(player);
                        if (manager.getPage(recover.getID()) == null) {
                            Manager.pages.add(recover);
                            player.sendMessage(ChatColor.GREEN + recover.getID() + " has been recovered");
                            return;
                        }
                        player.sendMessage(ChatColor.RED + "Failed to recover because the page already exists");
                    } else {
                        player.sendMessage(ChatColor.RED + "No page found to recover");
                    }
                } else if (command.equalsIgnoreCase("size")) {
                    if (!player.hasPermission("shop.page.size")) {
                        player.sendMessage(Config.PERMISSION_ERROR.toString());
                        return;
                    }
                    if (args.length == 3) {
                        if (isManaging(player, false)) {
                            final Page page2 = manager.getPage(manager.getEditorPage(player));
                            int size;
                            try {
                                size = Integer.parseInt(args[2]);
                            } catch (NumberFormatException nfe) {
                                player.sendMessage(ChatColor.RED + "Invalid size");
                                return;
                            }
                            if (size <= 0) {
                                size = 1;
                            }
                            if (size > 6) {
                                size = 6;
                            }
                            page2.setSize(size);
                            player.sendMessage(ChatColor.GREEN + "Size of " + page2.getID() + " has been set to " + size);
                        }
                        return;
                    }
                    player.sendMessage(ChatColor.GRAY + "/shop page size <1-6>");
                } else if (command.equalsIgnoreCase("manage")) {
                    if (!player.hasPermission("shop.page.manage")) {
                        player.sendMessage(Config.PERMISSION_ERROR.toString());
                        return;
                    }
                    if (args.length == 3) {
                        final String pagename3 = args[2].toUpperCase();
                        if (manager.getPage(pagename3) != null) {
                            Manager.edit.put(player.getName(), pagename3);
                            player.sendMessage(ChatColor.GREEN + "You are now managing " + pagename3 + ChatColor.GREEN);
                        } else {
                            player.sendMessage(ChatColor.RED + pagename3 + " does not exist");
                        }
                        return;
                    }
                    player.sendMessage(ChatColor.GRAY + "/shop page manage <page>");
                } else if (command.equalsIgnoreCase("delete")) {
                    if (!player.hasPermission("shop.page.delete")) {
                        player.sendMessage(Config.PERMISSION_ERROR.toString());
                        return;
                    }
                    if (args.length == 3) {
                        final String pagename3 = args[2].toUpperCase();
                        if (manager.getPage(pagename3) != null) {
                            final Page page = manager.getPage(pagename3);
                            Manager.pagerecovery.put(player.getUniqueId().toString(), new Page(page));
                            page.delete();
                            player.sendMessage(ChatColor.GREEN + pagename3 + " has been deleted");
                        } else {
                            player.sendMessage(ChatColor.RED + pagename3 + " does not exist");
                        }
                        return;
                    }
                    player.sendMessage(ChatColor.RED + "/shop page delete <page>");
                } else if (command.equalsIgnoreCase("create")) {
                    if (!player.hasPermission("shop.page.create")) {
                        player.sendMessage(Config.PERMISSION_ERROR.toString());
                        return;
                    }
                    if (args.length == 3) {
                        final String pagename3 = args[2].toUpperCase();
                        if (manager.getPage(pagename3) == null) {
                            final Page page = new Page(pagename3);
                            page.create();
                            player.sendMessage(ChatColor.GREEN + pagename3 + " has been created");
                            Manager.edit.put(player.getName(), pagename3);
                        } else {
                            player.sendMessage(ChatColor.RED + pagename3 + " already exists");
                        }
                        return;
                    }
                    player.sendMessage(ChatColor.RED + "/shop page create <entry>");
                } else if (command.equalsIgnoreCase("clear")) {
                    if (!player.hasPermission("shop.page.clear")) {
                        player.sendMessage(Config.PERMISSION_ERROR.toString());
                        return;
                    }
                    if (isManaging(player, false)) {
                        final Page page2 = getPage(player);
                        if (page2 != null) {
                            page2.clearItems();
                            player.sendMessage(ChatColor.GREEN + "All items have been cleared from " + page2.getID());
                        }
                    }
                } else if (command.equalsIgnoreCase("type")) {
                    if (!player.hasPermission("shop.page.type")) {
                        player.sendMessage(Config.PERMISSION_ERROR.toString());
                        return;
                    }
                    if (isManaging(player, false)) {
                        final Page page2 = getPage(player);
                        if (page2 != null) {
                            if (args.length == 3) {
                                final String type = args[2];
                                if (type.equalsIgnoreCase("sell")) {
                                    page2.setType(1);
                                } else {
                                    page2.setType(0);
                                }
                                player.sendMessage(ChatColor.GREEN + "Type of " + page2.getID() + " has been set to " + ((page2.getType() == 1) ? "sell" : "normal"));
                                return;
                            }
                            player.sendMessage(ChatColor.RED + "/shop page type <normal/sell>");
                        }
                    }
                } else if (command.equalsIgnoreCase("properties")) {
                    if (!player.hasPermission("shop.page.properties")) {
                        player.sendMessage(Config.PERMISSION_ERROR.toString());
                        return;
                    }
                    if (isManaging(player, false)) {
                        final Page page2 = getPage(player);
                        if (page2 != null) {
                            PageProperties.open(player, page2, 1);
                        }
                    }
                } else if (command.equalsIgnoreCase("list")) {
                    if (!player.hasPermission("shop.page.list")) {
                        player.sendMessage(Config.PERMISSION_ERROR.toString());
                        return;
                    }
                    int index = 1;
                    if (args.length == 3) {
                        try {
                            index = Integer.parseInt(args[2]);
                        } catch (NumberFormatException ex3) {
                        }
                    }
                    final String header = ChatColor.GRAY + "  === " + ChatColor.DARK_GREEN + "shop Pages" + ChatColor.GRAY + " === " + ChatColor.DARK_GREEN + "Page " + ChatColor.GREEN + "%index%" + ChatColor.GRAY + "/" + ChatColor.GREEN + "%size%" + ChatColor.GRAY + " ===";
                    final List<String> help = new ArrayList<String>();
                    for (final Page page3 : new Manager().getPages()) {
                        help.add(ChatColor.GRAY + "- " + ChatColor.YELLOW + page3.getID() + ChatColor.GRAY + "  Title: " + ChatColor.RESET + page3.getTitle() + ChatColor.GRAY + "  Type: " + ChatColor.RESET + ((page3.getType() == 1) ? "Sell" : "Normal"));
                    }
                    Control.list(player, help, index, header, 9);
                } else if (command.equalsIgnoreCase("open")) {
                    if (args.length == 3) {
                        final String pagename3 = args[2].toUpperCase();
                        if (!player.hasPermission("shop.page.open." + pagename3) && !player.hasPermission("shop.page.open")) {
                            player.sendMessage(Config.PERMISSION_ERROR.toString());
                            return;
                        }
                        if (manager.getPage(pagename3) != null) {
                            final Page page = manager.getPage(pagename3);
                            page.openPage(player);
                        } else {
                            player.sendMessage(ChatColor.RED + pagename3 + " does not exist");
                        }
                    } else {
                        this.help(player);
                    }
                } else if (command.equalsIgnoreCase("help")) {
                    final List<String> help2 = Manager.getAvailableCommands(player, "page");
                    if (help2.isEmpty()) {
                        player.sendMessage(Config.PERMISSION_ERROR.toString());
                        return;
                    }
                    int index2 = 1;
                    if (args.length == 3) {
                        try {
                            index2 = Integer.parseInt(args[2]);
                        } catch (NumberFormatException ex4) {
                        }
                    }
                    final String header2 = ChatColor.GRAY + "  === " + ChatColor.DARK_GREEN + "shop Page Help" + ChatColor.GRAY + " === " + ChatColor.DARK_GREEN + "Page " + ChatColor.GREEN + "%index%" + ChatColor.GRAY + "/" + ChatColor.GREEN + "%size%" + ChatColor.GRAY + " ===";
                    Control.list(player, help2, index2, header2, 7);
                } else {
                    this.help(player);
                }
                return;
            }
            this.help(player);
        }
    }

    public void help(final Player player) {
        if (!Manager.getAvailableCommands(player, "page").isEmpty()) {
            player.sendMessage(ChatColor.GRAY + "/shop page help");
        } else {
            player.sendMessage(Config.PERMISSION_ERROR.toString());
        }
    }

    public void cancelEdit(final Player player) {
        Manager.edit.remove(player.getName());
    }

    public boolean isEditing(final Player player, final String id) {
        return Manager.edit.containsKey(player.getName()) && Manager.edit.get(player.getName()).equals(id);
    }

    public boolean hasItemInHand(final Player player) {
        return player.getItemInHand() != null && !player.getItemInHand().getType().equals(Material.AIR);
    }
}
