package conj.shop.addons;

import conj.shop.base.Initiate;
import conj.shop.commands.control.Manager;
import conj.shop.data.Page;
import conj.shop.data.PageSlot;
import conj.shop.data.enums.Config;
import conj.shop.events.listeners.Editor;
import conj.shop.events.listeners.Shop;
import conj.shop.tools.DoubleUtil;
import conj.shop.tools.InventoryCreator;
import conj.shop.tools.ItemCreator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Placeholder {
    public static List<String> placehold(final Player player, final List<String> messages, final Page page, final int slot) {
        final List<String> messageList = new ArrayList<String>();
        for (final String s : messages) {
            messageList.add(placehold(player, s, page, slot));
        }
        return messageList;
    }

    public static List<String> placehold(final Player player, final List<String> messages, final Page page, final Page gui, final int x, final int slot, final int amount, final String status, final boolean buy) {
        final List<String> messageList = new ArrayList<String>();
        for (final String s : messages) {
            messageList.add(placehold(player, s, page, gui, x, slot, amount, status, buy));
        }
        return messageList;
    }

    public static String placehold(final Player player, String message, final Page page, final Page gui, final int x, final int slot, final int amount, final String status, final boolean buy) {
        if (gui != null) {
            final PageSlot ps = page.getPageSlot(slot);
            if (status.equalsIgnoreCase("unconfirmed")) {
                if (ps.getDataString("gui_confirm1") != null && message.contains("%confirm%")) {
                    message = message.replaceAll("%confirm%", ps.getDataString("gui_confirm1"));
                }
            } else if (ps.getDataString("gui_confirm1") != null && message.contains("%confirm%")) {
                message = message.replaceAll("%confirm%", ps.getDataString("gui_confirm2"));
            }
            if (message.contains("%quantity%")) {
                message = message.replaceAll("%quantity%", ps.getDataString("gui_quantity"));
            }
        }
        return placehold(player, message, page, slot, amount, status, buy);
    }

    public static List<String> placehold(final Player player, final List<String> messages, final Page page) {
        final List<String> messageList = new ArrayList<String>();
        for (final String s : messages) {
            messageList.add(placehold(player, s, page));
        }
        return messageList;
    }

    public static List<String> placehold(final Player player, final List<String> messages) {
        final List<String> messageList = new ArrayList<String>();
        for (final String s : messages) {
            messageList.add(placehold(player, s));
        }
        return messageList;
    }

    public static String placehold(final Player player, String message) {
        if (Initiate.placeholderapi) {
            message = PlaceholderAddon.placehold(player, message);
        }
        if (message.contains("%shop_previous%")) {
            message = message.replaceAll("%shop_previous%", new Manager().getPreviousPage(player));
        }
        if (message.contains("%shop_current%")) {
            message = message.replaceAll("%shop_current%", new Manager().getOpenPage(player));
        }
        if (message.contains("%shop_main%")) {
            message = message.replaceAll("%shop_main%", Config.MAIN_PAGE.toString());
        }
        if (message.contains("%player%")) {
            message = message.replaceAll("%player%", player.getName());
        }
        if (message.contains("%balance%")) {
            message = message.replaceAll("%balance%", DoubleUtil.toString(Initiate.econ.getBalance(player)));
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String placehold(final Player player, String message, final Page page) {
        message = placehold(player, message);
        if (message.contains("%page%")) {
            message = message.replaceAll("%page%", page.getID());
        }
        if (message.contains("%page_title%")) {
            message = message.replaceAll("%page_title%", page.getTitle());
        }
        return message;
    }

    public static String placehold(final Player player, String message, final Page page, final int slot) {
        message = placehold(player, message, page);
        if (message.contains("%cooldown%")) {
            message = message.replaceAll("%cooldown%", new Manager().getCooldown(player, page, slot));
        }
        if (message.contains("%cooldown_day%")) {
            message = message.replaceAll("%cooldown_day%", String.valueOf(Manager.convertMilli(new Manager().getCooldownMilli(player, page, slot), "day")));
        }
        if (message.contains("%cooldown_hour%")) {
            message = message.replaceAll("%cooldown_hour%", String.valueOf(Manager.convertMilli(new Manager().getCooldownMilli(player, page, slot), "hour")));
        }
        if (message.contains("%cooldown_minute%")) {
            message = message.replaceAll("%cooldown_minute%", String.valueOf(Manager.convertMilli(new Manager().getCooldownMilli(player, page, slot), "minute")));
        }
        if (message.contains("%cooldown_second%")) {
            message = message.replaceAll("%cooldown_second%", String.valueOf(Manager.convertMilli(new Manager().getCooldownMilli(player, page, slot), "second")));
        }
        if (message.contains("%item%")) {
            message = message.replaceAll("%item%", Editor.getItemName(page.getInventory().getItem(slot)));
        }
        final PageSlot ps = page.getPageSlot(slot);
        if (ps != null) {
            if (message.contains("%item_cost%")) {
                message = message.replaceAll("%item_cost%", DoubleUtil.toString(page.getPageSlot(slot).getCost()));
            }
            if (message.contains("%item_sell%")) {
                message = message.replaceAll("%item_sell%", DoubleUtil.toString(page.getPageSlot(slot).getSell()));
            }
            if (message.contains("%item_function%")) {
                message = message.replaceAll("%item_function%", page.getPageSlot(slot).getFunction().toString());
            }
        }
        return message;
    }

    public static String placehold(final Player player, String message, final Page page, final int slot, final int amount, final String status, final boolean buy) {
        final PageSlot ps = page.getPageSlot(slot);
        message = placehold(player, message, page, slot);
        if (message.contains("%item%")) {
            message = message.replaceAll("%item%", String.valueOf(Editor.getItemName(page.getInventory().getItem(slot))));
        }
        if (message.contains("%earnings%")) {
            message = message.replaceAll("%earnings%", DoubleUtil.toString(ps.getSell() * amount));
        }
        if (message.contains("%price%")) {
            message = message.replaceAll("%price%", DoubleUtil.toString(ps.getCost() * amount));
        }
        if (message.contains("%amount%")) {
            message = message.replaceAll("%amount%", String.valueOf(amount));
        }
        if (message.contains("%confirm%")) {
            message = message.replaceAll("%confirm%", ChatColor.DARK_GREEN + (status.equalsIgnoreCase("unconfirmed") ? buy ? "Click to buy" : "Click to sell" : "Click to confirm"));
        }
        return message;
    }

    public static List<String> placehold(final Player player, final List<String> messages, final Page page, final int slot, final int amount, final String status, final boolean buy) {
        final List<String> messageList = new ArrayList<String>();
        for (final String s : messages) {
            messageList.add(placehold(player, s, page, slot, amount, status, buy));
        }
        return messageList;
    }

    public static Inventory placehold(final Player player, final Inventory inv, final Page page, final Page gui, final int slot, final int amount, final String status, final boolean buy) {
        for (int x = 0; inv.getSize() > x; ++x) {
            final ItemStack i = inv.getItem(x);
            if (i != null) {
                final ItemCreator ic = new ItemCreator(i);
                if (ic.getName().equalsIgnoreCase("%item_display%") && page.getInventory().getItem(slot) != null) {
                    final ItemCreator ic2 = new ItemCreator(page.getInventory().getItem(slot));
                    if (ic.hasLore()) {
                        for (final String l : ic.getLore()) {
                            ic2.addLore(l);
                        }
                    }
                    inv.setItem(x, ic2.getItem());
                }
            }
        }
        for (int x = 0; inv.getSize() > x; ++x) {
            final ItemStack i = inv.getItem(x);
            if (i != null) {
                final ItemCreator ic = new ItemCreator(i);
                final long startlore = System.currentTimeMillis();
                if (ic.hasLore()) {
                    ic.setLore(placehold(player, ic.getLore(), page, gui, x, slot, amount, status, buy));
                }
                Initiate.log(x + " slot lore took: " + Manager.getDuration(startlore));
                final long startname = System.currentTimeMillis();
                if (ic.hasDisplayName()) {
                    ic.setName(placehold(player, ic.getName(), page, gui, x, slot, amount, status, buy));
                }
                Initiate.log(x + " slot name took: " + Manager.getDuration(startname));
            }
        }
        return inv;
    }

    public static Inventory placehold(final Player player, final Inventory inv, final Page page) {
        for (int x = 0; inv.getSize() > x; ++x) {
            final ItemStack i = inv.getItem(x);
            if (i != null) {
                final ItemCreator ic = new ItemCreator(i);
                if (ic.hasLore()) {
                    ic.setLore(placehold(player, ic.getLore(), page));
                }
                if (ic.hasDisplayName()) {
                    ic.setName(placehold(player, ic.getName(), page));
                }
            }
        }
        return updateWorth(player, inv, page);
    }

    public static Inventory updateWorth(final Player player, final Inventory inv, final Page page) {
        final InventoryCreator ic = new InventoryCreator(inv);
        final String worth = DoubleUtil.toString(Shop.getInventoryWorth(player, inv, page));
        ic.replace("%worth%", worth);
        return ic.getInventory();
    }

    public static void sendMessage(final Player player, final String message) {
        player.sendMessage(placehold(player, message));
    }

    public static void sendMessage(final Player player, final List<String> messages) {
        for (final String s : messages) {
            player.sendMessage(placehold(player, s));
        }
    }

    public static String[] placehold(final Player player, final String[] args) {
        for (int x = 0; x < args.length; ++x) {
            args[x] = placehold(player, args[x]);
        }
        return args;
    }
}
