package conj.shop.events.listeners;

import conj.shop.Initiate;
import conj.shop.data.Page;
import conj.shop.data.enums.PageData;
import conj.shop.events.custom.PageClickEvent;
import conj.shop.tools.InventoryCreator;
import conj.shop.tools.ItemCreator;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class PageProperties implements Listener {
    public static void open(final Player player, final Page page, final int id) {
        if (page == null || player == null) {
            return;
        }
        if (id == 1) {
            final InventoryCreator inv = new InventoryCreator(ChatColor.BLUE + page.getID() + ChatColor.DARK_GRAY + "\u2590 Page Properties", 1);
            final GUI gui = new GUI(Initiate.getPlugin(), PageData.PAGE_PROPERTIES, inv.getInventory(), page);
            if (!page.isGUI()) {
                inv.setItem(1, new ItemStack(Material.BEACON));
                inv.setDisplay(1, ChatColor.BLUE + "Close on transaction");
                inv.addLore(1, page.closesOnTransaction() ? (ChatColor.GREEN + "    True") : (ChatColor.RED + "    False"));
                inv.addLore(1, ChatColor.GRAY + "Click to toggle");
                inv.setItem(2, new ItemStack(Material.FEATHER));
                inv.setDisplay(2, ChatColor.BLUE + "Instant confirm transaction");
                inv.addLore(2, page.instantConfirms() ? (ChatColor.GREEN + "    True") : (ChatColor.RED + "    False"));
                inv.addLore(2, ChatColor.GRAY + "Click to toggle");
                inv.setItem(3, new ItemStack(Material.GOLD_INGOT));
                inv.setDisplay(3, ChatColor.BLUE + "Hide affordability");
                inv.addLore(3, page.hidesAffordability() ? (ChatColor.GREEN + "    True") : (ChatColor.RED + "    False"));
                inv.addLore(3, ChatColor.GRAY + "Click to toggle");
                inv.setItem(4, new ItemStack(Material.CLOCK));
                inv.setDisplay(4, ChatColor.BLUE + "Default quantity");
                inv.addLore(4, ChatColor.GREEN + "    " + page.getDefaultQuantity());
                inv.addLore(4, ChatColor.GRAY + "Click to change");
                inv.setItem(8, new ItemStack(Material.COMMAND_BLOCK));
                inv.setDisplay(8, ChatColor.BLUE + "GUI");
                inv.addLore(8, page.isGUI() ? (ChatColor.GREEN + "    True") : (ChatColor.RED + "    False"));
                inv.addLore(8, ChatColor.GRAY + "Click to toggle");
                inv.setItem(0, new ItemStack(Material.BUCKET));
                inv.setDisplay(0, ChatColor.BLUE + "Fill slots");
                inv.addLore(0, ChatColor.GRAY + "Set an item to fill empty slots");
                inv.addLore(0, " ");
                inv.addLore(0, ChatColor.GOLD + "Current item");
                String itemname = "";
                String material = "";
                if (page.getFill() != null) {
                    itemname = new ItemCreator(page.getFill()).getName();
                    material = page.getFill().getType().toString().toLowerCase().replaceAll("_", " ");
                    material = WordUtils.capitalizeFully(material);
                }
                inv.addLore(0, ChatColor.GRAY + "Name: " + ChatColor.RESET + itemname);
                inv.addLore(0, ChatColor.GRAY + "Material: " + ChatColor.WHITE + material);
            } else {
                inv.setItem(0, new ItemStack(Material.COMMAND_BLOCK));
                inv.setDisplay(0, ChatColor.BLUE + "GUI");
                inv.addLore(0, page.isGUI() ? (ChatColor.GREEN + "    True") : (ChatColor.RED + "    False"));
                inv.addLore(0, ChatColor.GRAY + "Click to toggle");
            }
            inv.setBlank(Material.BLUE_STAINED_GLASS_PANE, 11);
            gui.open(player);
        } else if (id == 2) {
            final InventoryCreator inv = new InventoryCreator(ChatColor.BLUE + page.getID() + ChatColor.DARK_GRAY + "\u2590 Fill Slots", 3);
            final GUI gui = new GUI(Initiate.getPlugin(), PageData.PAGE_PROPERTIES_FILL_SLOTS, inv.getInventory(), page);
            inv.setItem(4, new ItemStack(Material.COMPASS));
            inv.setDisplay(4, ChatColor.RED + "Back");
            inv.addLore(4, ChatColor.GRAY + "Return to properties");
            if (page.getFill() != null) {
                inv.setItem(13, page.getFill());
            }
            inv.setItem(22, new ItemStack(Material.OAK_SIGN));
            inv.setDisplay(22, ChatColor.GREEN + "Click an item in your inventory");
            inv.addLore(22, ChatColor.GREEN + "to set it as the fill item");
            inv.setBlank(Material.BLUE_STAINED_GLASS_PANE, 11);
            gui.open(player);
        }
    }

    @EventHandler
    public void PageviewClick(final PageClickEvent event) {
        final Player player = event.getPlayer();
        final int slot = event.getSlot();
        final Page page = event.getPage();
        final ItemStack item = event.getItem();
        if (event.getPageData().equals(PageData.PAGE_PROPERTIES)) {
            if (event.isTopInventory()) {
                if (!page.isGUI()) {
                    if (slot == 0) {
                        open(player, page, 2);
                    } else if (slot == 1) {
                        page.setCloses(!page.closesOnTransaction());
                        open(player, page, 1);
                    } else if (slot == 2) {
                        page.setInstantConfirm(!page.instantConfirms());
                        open(player, page, 1);
                    } else if (slot == 3) {
                        page.setHideAffordability(!page.hidesAffordability());
                        open(player, page, 1);
                    } else if (slot == 4) {
                        page.setDefaultQuantity((page.getDefaultQuantity() == 0) ? 1 : 0);
                        open(player, page, 1);
                    } else if (slot == 8) {
                        page.gui = !page.isGUI();
                        open(player, page, 1);
                    }
                } else if (slot == 0) {
                    page.gui = !page.isGUI();
                    open(player, page, 1);
                }
            }
        } else if (event.getPageData().equals(PageData.PAGE_PROPERTIES_FILL_SLOTS)) {
            if (!event.isTopInventory()) {
                page.setFill(item);
                open(player, page, 2);
            } else if (event.isTopInventory() && slot == 4) {
                open(player, page, 1);
            }
        }
    }
}
