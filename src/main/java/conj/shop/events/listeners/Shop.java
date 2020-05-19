package conj.shop.events.listeners;

import conj.shop.Initiate;
import conj.shop.addons.Placeholder;
import conj.shop.addons.VaultAddon;
import conj.shop.commands.control.Manager;
import conj.shop.data.Page;
import conj.shop.data.PageSlot;
import conj.shop.data.enums.*;
import conj.shop.events.custom.PageClickEvent;
import conj.shop.events.custom.PageCloseEvent;
import conj.shop.tools.DoubleUtil;
import conj.shop.tools.InventoryCreator;
import conj.shop.tools.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Shop implements Listener {
    private static List<ItemStack> getAddedItems(final OfflinePlayer player, final Inventory inventory, final Page page) {
        final List<ItemStack> added = new ArrayList<ItemStack>();
        final Inventory copy = Bukkit.createInventory(null, inventory.getSize());
        copy.setContents(inventory.getContents());
        for (final int slot : page.getVisibleSlots(player.getPlayer())) {
            copy.setItem(slot, null);
        }
        ItemStack[] contents;
        for (int length = (contents = copy.getContents()).length, j = 0; j < length; ++j) {
            final ItemStack i = contents[j];
            if (i != null) {
                added.add(i);
            }
        }
        return added;
    }

    private static double sellInventory(final OfflinePlayer player, final Inventory inventory, final Page page) {
        if (inventory == null) {
            return 0.0;
        }
        final double earnings = getInventoryWorth(player, inventory, page);
        if (earnings > 0.0) {
            Initiate.econ.depositPlayer(player, earnings);
        }
        return earnings;
    }

    public static double getInventoryWorth(final OfflinePlayer player, final Inventory inventory, final Page page) {
        if (inventory == null) {
            return 0.0;
        }
        double worth = 0.0;
        for (final ItemStack i : getAddedItems(player, inventory, page)) {
            if (i != null) {
                worth += Manager.get().getWorth(i);
            }
        }
        return worth;
    }

    public static double getInventoryWorth(final Inventory inventory) {
        if (inventory == null) {
            return 0.0;
        }
        double worth = 0.0;
        ItemStack[] contents;
        for (int length = (contents = inventory.getContents()).length, j = 0; j < length; ++j) {
            final ItemStack i = contents[j];
            if (i != null) {
                worth += Manager.get().getWorth(i);
            }
        }
        return worth;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void pickupPrevention(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            Player player = (Player) entity;
            final Manager manager = Manager.get();
            final String pagename = manager.getOpenPage(player);
            final Page page = manager.getPage(pagename);
            if (page != null && page.getType() == 1) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void removeEntity(final PlayerInteractEntityEvent event) {
        if (Initiate.debug) {
            if (!event.getHand().equals(EquipmentSlot.HAND)) {
                return;
            }
            if (!event.getPlayer().isOp()) {
                return;
            }
            event.getPlayer().getInventory().getItemInMainHand();
            if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.STICK)) {
                final ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
                final ItemCreator ic = new ItemCreator(item);
                if (ic.getLore().contains(ChatColor.RED + "Right-click an entity to remove it") && ic.getName().equals(ChatColor.DARK_RED + "Entity Remover")) {
                    final Entity e = event.getRightClicked();
                    event.getRightClicked().remove();
                    Initiate.log(e.getName() + " : " + e.getCustomName() + " : " + ((e instanceof LivingEntity) ? "LivingEntity" : "Entity") + " : has been removed from " + e.getLocation().getWorld().getName());
                }
            }
        }
    }

    @EventHandler
    public void ItemviewClose(final PageCloseEvent event) {
        if (event.getPageData().equals(PageData.SHOP)) {
            if (!event.isCancelled()) {
                final Manager manage = new Manager();
                manage.removeOpenPage(event.getPlayer());
            }
            if (event.getPage().getType() == 1) {
                final Inventory inv = event.getInventory();
                final Inventory pinv = event.getPage().getInventory(event.getPlayer());
                final List<Integer> slots = new ArrayList<Integer>();
                for (int x = 0; pinv.getSize() > x; ++x) {
                    if (pinv.getItem(x) != null) {
                        slots.add(x);
                    }
                }
                for (final int slot : slots) {
                    inv.setItem(slot, null);
                }
                ItemStack[] contents;
                for (int length = (contents = inv.getContents()).length, i = 0; i < length; ++i) {
                    final ItemStack item = contents[i];
                    if (item != null) {
                        final HashMap<Integer, ItemStack> map = event.getPlayer().getInventory().addItem(item);
                        if (!map.isEmpty()) {
                            final HashMap<Integer, ItemStack> emap = event.getPlayer().getEnderChest().addItem(map.get(0));
                            if (!emap.isEmpty()) {
                                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), emap.get(0));
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void ItemviewClick(final PageClickEvent event) {
        final Player player = event.getPlayer();
        final int slot = event.getSlot();
        final Page page = event.getPage();
        final PageSlot ps = page.getPageSlot(slot);
        if (event.getPageData().equals(PageData.SHOP)) {
            if (event.isTopInventory()) {
                if (!event.getItem().getType().equals(Material.AIR)) {
                    if (!ps.canSee(player)) {
                        return;
                    }
                    if (!page.getSlots().contains(slot)) {
                        return;
                    }
                    Initiate.log(page.getID());
                    if (page.getType() == 1) {
                        if (!event.getPage().getVisibleSlots(player).contains(event.getSlot())) {
                            final ItemStack item = event.getInventory().getItem(event.getSlot());
                            if (item != null) {
                                final int first = player.getInventory().firstEmpty();
                                if (first != -1) {
                                    player.getInventory().setItem(first, item);
                                    event.getInventory().setItem(event.getSlot(), null);
                                    page.updateView(player, false);
                                }
                            }
                            return;
                        }
                        if (ps.getFunction().equals(Function.CONFIRM)) {
                            final Inventory inv = Bukkit.createInventory(null, event.getInventory().getSize());
                            inv.setContents(event.getInventory().getContents());
                            final double earning = sellInventory(player, inv, event.getPage());
                            if (earning > 0.0) {
                                String complete = Config.SELL_COMPLETE.toString();
                                if (complete.length() > 0) {
                                    complete = complete.replaceAll("%worth%", DoubleUtil.toString(earning));
                                    player.sendMessage(complete);
                                }
                                event.getTopInventory().setContents(page.getInventory(player).getContents());
                            }
                            return;
                        }
                    }
                    if (ps.getFunction().equals(Function.NONE)) {
                        return;
                    }
                    if (ps.hasPermissions()) {
                        for (final String p : ps.getPermissions()) {
                            if (!player.hasPermission(p)) {
                                ps.sendMessage(player, MessageType.PERMISSION.toString());
                                player.closeInventory();
                                return;
                            }
                        }
                    }
                    if (ps.hasCooldown() && ps.inCooldown(player)) {
                        ps.sendMessage(player, MessageType.COOLDOWN.toString());
                        player.closeInventory();
                        return;
                    }
                    if (ps.getFunction().equals(Function.BUY)) {
                        if (event.getClick().equals(ClickType.RIGHT)) {
                            final ItemStack item = page.getInventory().getItem(slot);
                            if (ps.getSell() > 0.0 && player.getInventory().containsAtLeast(item, 1)) {
                                this.sellItem(player, page, slot, 0, "unconfirmed", event.getInventoryView());
                            }
                        } else if (event.getClick().equals(ClickType.LEFT) && ps.getCost() <= Initiate.econ.getBalance(player)) {
                            this.buyItem(player, page, slot, 0, "unconfirmed", event.getInventoryView());
                        }
                    } else if (ps.getFunction().equals(Function.SELL)) {
                        final ItemStack item = page.getInventory().getItem(slot);

                        if (item == null) return;

                        if (item.getAmount() <= getContainsAmount(player, item)) {
                            this.sellItem(player, page, slot, 0, "unconfirmed", event.getInventoryView());
                        }
                    } else if (ps.getFunction().equals(Function.TRADE)) {
                        this.tradeItem(player, page, slot, event.getInventoryView());
                    } else if (ps.getFunction().equals(Function.COMMAND)) {
                        final VaultAddon addon = new VaultAddon(Initiate.econ);
                        if (!addon.canAfford(player, ps.getCost())) {
                            return;
                        }

                        // Do we close inventory after purchase success?
                        if (page.closesOnTransaction()) {
                            player.closeInventory();
                        }

                        for (final String c : ps.getCommands()) {
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Placeholder.placehold(player, c, page, slot));
                        }

                        // Leave for now
                        Initiate.econ.withdrawPlayer(player, ps.getCost());

                        if (ps.getCost() != 0) {
                            player.sendMessage(ChatColor.YELLOW + "Your new balance is " + ChatColor.GREEN + Initiate.econ.getBalance(player));
                        }

                        if (ps.hasCooldown() && !ps.inCooldown(player)) {
                            ps.cooldown(player);
                        }
                    }
                }
            } else if (page.getType() == 1) {
                final ItemStack item = player.getInventory().getItem(event.getSlot());
                if (item != null) {
                    final Inventory top = event.getTopInventory();
                    final int first2 = top.firstEmpty();
                    final double worth = Manager.get().getWorth(item);
                    if (first2 != -1 && worth > 0.0) {
                        top.setItem(first2, item);
                        player.getInventory().setItem(event.getSlot(), null);
                        Manager.get().setOpenPage(player, page.getID());
                        page.updateView(player, false);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void tradeItemClick(final PageClickEvent event) {
        final Player player = event.getPlayer();
        final int slot = event.getSlot();
        final Page page = event.getPage();
        if (event.getPageData().equals(PageData.TRADE_ITEM)) {
            if (event.getItem().getType().equals(Material.AIR)) {
                return;
            }
            if (event.isTopInventory()) {
                final int itemslot = (int) event.getGUI().getPass().get("slot");
                final String guipagename = (String) event.getGUI().getPass().get("guipage");
                final Page guipage = Manager.get().getPage(guipagename);
                final PageSlot ips = page.getPageSlot(itemslot);
                final boolean no_gui = guipage == null || !guipage.isGUI();
                if (no_gui) {
                    if (ips == null) {
                        return;
                    }
                    if (slot == 21) {
                        page.openPage(player);
                    } else if (slot == 23) {
                        ips.attemptTrade(player);
                    }
                } else {
                    final PageSlot ps = guipage.getPageSlot(slot);
                    if (ps == null) {
                        return;
                    }
                    if (ps.getGUIFunction().equals(GUIFunction.BACK)) {
                        page.openPage(player);
                    }
                    if (ps.getGUIFunction().equals(GUIFunction.CONFIRM)) {
                        ips.attemptTrade(player);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void purchaseItemClick(final PageClickEvent event) {
        final Player player = event.getPlayer();
        final int slot = event.getSlot();
        final Page page = event.getPage();
        if (event.getPageData().equals(PageData.PURCHASE_ITEM) || event.getPageData().equals(PageData.SELL_ITEM)) {
            if (event.getItem().getType().equals(Material.AIR)) {
                return;
            }
            if (event.isTopInventory()) {
                final int itemslot = (int) event.getGUI().getPass().get("slot");
                int amount = (int) event.getGUI().getPass().get("amount");
                final String status = (String) event.getGUI().getPass().get("status");
                final String guipagename = (String) event.getGUI().getPass().get("guipage");
                final Page guipage = Manager.get().getPage(guipagename);
                if (guipage != null) {
                    final PageSlot ps = guipage.getPageSlot(slot);
                    if (guipage.isGUI()) {
                        Initiate.log(player.getName() + " clicked on GUI page " + guipagename);
                        final GUIFunction function = ps.getGUIFunction();
                        Initiate.log("GUIFunction: " + function);
                        if (function.equals(GUIFunction.CONFIRM)) {
                            Initiate.log(player.getName() + " clicked on confirm GUI page " + guipagename);
                            if (status.equalsIgnoreCase("unconfirmed")) {
                                if (amount >= 1) {
                                    if (page.instantConfirms()) {
                                        this.completeTransaction(event.getPageData(), page, player, itemslot, amount);
                                        return;
                                    }
                                    if (event.getPageData().equals(PageData.PURCHASE_ITEM)) {
                                        this.buyItem(player, page, itemslot, amount, "confirmed", event.getInventoryView());
                                    }
                                    if (event.getPageData().equals(PageData.SELL_ITEM)) {
                                        this.sellItem(player, page, itemslot, amount, "confirmed", event.getInventoryView());
                                    }
                                }
                            } else {
                                this.completeTransaction(event.getPageData(), page, player, itemslot, amount);
                            }
                        } else if (function.equals(GUIFunction.QUANTITY)) {
                            Initiate.log(player.getName() + " clicked on quantity GUI page " + guipagename);
                            int add = 0;
                            add = ps.getDataInt("gui_quantity");
                            if (event.getClick().equals(ClickType.LEFT)) {
                                amount += add;
                            }
                            if (event.getClick().equals(ClickType.RIGHT)) {
                                amount -= add;
                            }
                            if (event.getPageData().equals(PageData.PURCHASE_ITEM)) {
                                this.buyItem(player, page, itemslot, amount, "unconfirmed", event.getInventoryView());
                            }
                            if (event.getPageData().equals(PageData.SELL_ITEM)) {
                                this.sellItem(player, page, itemslot, amount, "unconfirmed", event.getInventoryView());
                            }
                        } else if (function.equals(GUIFunction.BACK)) {
                            Initiate.log(player.getName() + " clicked on back GUI page " + guipagename);
                            page.openPage(player);
                        }
                        return;
                    }
                }
                if (slot == 4) {
                    page.openPage(player);
                }
                if (slot == 49) {
                    if (status.equalsIgnoreCase("unconfirmed")) {
                        if (amount >= 1) {
                            if (page.instantConfirms()) {
                                this.completeTransaction(event.getPageData(), page, player, itemslot, amount);
                                return;
                            }
                            if (event.getPageData().equals(PageData.PURCHASE_ITEM)) {
                                this.buyItem(player, page, itemslot, amount, "confirmed", event.getInventoryView());
                            }
                            if (event.getPageData().equals(PageData.SELL_ITEM)) {
                                this.sellItem(player, page, itemslot, amount, "confirmed", event.getInventoryView());
                            }
                        }
                    } else {
                        this.completeTransaction(event.getPageData(), page, player, itemslot, amount);
                    }
                }
                if (slot == 20) {
                    int add2 = 0;
                    if (event.getClick().equals(ClickType.LEFT)) {
                        ++add2;
                    }
                    if (event.getClick().equals(ClickType.RIGHT)) {
                        --add2;
                    }
                    if (event.getPageData().equals(PageData.PURCHASE_ITEM)) {
                        this.buyItem(player, page, itemslot, amount + add2, "unconfirmed", event.getInventoryView());
                    }
                    if (event.getPageData().equals(PageData.SELL_ITEM)) {
                        this.sellItem(player, page, itemslot, amount + add2, "unconfirmed", event.getInventoryView());
                    }
                }
                if (slot == 21) {
                    int add2 = 0;
                    if (event.getClick().equals(ClickType.LEFT)) {
                        add2 += 8;
                    }
                    if (event.getClick().equals(ClickType.RIGHT)) {
                        add2 -= 8;
                    }
                    if (event.getPageData().equals(PageData.PURCHASE_ITEM)) {
                        this.buyItem(player, page, itemslot, amount + add2, "unconfirmed", event.getInventoryView());
                    }
                    if (event.getPageData().equals(PageData.SELL_ITEM)) {
                        this.sellItem(player, page, itemslot, amount + add2, "unconfirmed", event.getInventoryView());
                    }
                }
                if (slot == 22) {
                    int add2 = 0;
                    if (event.getClick().equals(ClickType.LEFT)) {
                        add2 += 16;
                    }
                    if (event.getClick().equals(ClickType.RIGHT)) {
                        add2 -= 16;
                    }
                    if (event.getPageData().equals(PageData.PURCHASE_ITEM)) {
                        this.buyItem(player, page, itemslot, amount + add2, "unconfirmed", event.getInventoryView());
                    }
                    if (event.getPageData().equals(PageData.SELL_ITEM)) {
                        this.sellItem(player, page, itemslot, amount + add2, "unconfirmed", event.getInventoryView());
                    }
                }
                if (slot == 23) {
                    int add2 = 0;
                    if (event.getClick().equals(ClickType.LEFT)) {
                        add2 += 32;
                    }
                    if (event.getClick().equals(ClickType.RIGHT)) {
                        add2 -= 32;
                    }
                    if (event.getPageData().equals(PageData.PURCHASE_ITEM)) {
                        this.buyItem(player, page, itemslot, amount + add2, "unconfirmed", event.getInventoryView());
                    }
                    if (event.getPageData().equals(PageData.SELL_ITEM)) {
                        this.sellItem(player, page, itemslot, amount + add2, "unconfirmed", event.getInventoryView());
                    }
                }
                if (slot == 24) {
                    int add2 = 0;
                    if (event.getClick().equals(ClickType.LEFT)) {
                        add2 += 64;
                    }
                    if (event.getClick().equals(ClickType.RIGHT)) {
                        add2 -= 64;
                    }
                    if (event.getPageData().equals(PageData.PURCHASE_ITEM)) {
                        this.buyItem(player, page, itemslot, amount + add2, "unconfirmed", event.getInventoryView());
                    }
                    if (event.getPageData().equals(PageData.SELL_ITEM)) {
                        this.sellItem(player, page, itemslot, amount + add2, "unconfirmed", event.getInventoryView());
                    }
                }
                if (slot == 31) {
                    int add2 = 0;
                    if (event.getClick().equals(ClickType.LEFT)) {
                        add2 += 128;
                    }
                    if (event.getClick().equals(ClickType.RIGHT)) {
                        add2 -= 128;
                    }
                    if (event.getPageData().equals(PageData.PURCHASE_ITEM)) {
                        this.buyItem(player, page, itemslot, amount + add2, "unconfirmed", event.getInventoryView());
                    }
                    if (event.getPageData().equals(PageData.SELL_ITEM)) {
                        this.sellItem(player, page, itemslot, amount + add2, "unconfirmed", event.getInventoryView());
                    }
                }
            }
        }
    }

    public void completeTransaction(final PageData pd, final Page page, final Player player, final int itemslot, final int amount) {
        if (pd.equals(PageData.PURCHASE_ITEM)) {
            page.buyItem(player, itemslot, amount);
        }
        if (pd.equals(PageData.SELL_ITEM)) {
            page.sellItem(player, itemslot, amount);
        }
        if (page.closesOnTransaction()) {
            player.closeInventory();
        } else {
            page.openPage(player);
        }
    }

    public void buyItem(final Player player, final Page page, final int slot, int amount, final String status, InventoryView inventoryView) {
        final PageSlot ps = page.getPageSlot(slot);
        final long start = System.currentTimeMillis();
        if (amount < page.getDefaultQuantity()) {
            amount = page.getDefaultQuantity();
        }
        final int affordable = new VaultAddon(Initiate.econ).getAffordable(player, ps.getCost(), amount);
        if (amount > affordable) {
            amount = affordable;
        }
        Initiate.log("Setup BUY GUI affordable took: " + Manager.getDuration(start));
        final Page buypage = Manager.get().getPage(Config.PURCHASE_GUI.toString());
        final Inventory mainInv = this.getBuyInventory().getInventory();
        final String title = Placeholder.placehold(player, Placeholder.placehold(player, inventoryView.getTitle()), page, slot, amount, status, true);
        Initiate.log("Get BUY GUI inventory took: " + Manager.getDuration(start));
        final Inventory viewInv = Placeholder.placehold(player, mainInv, page, buypage, slot, amount, status, true);
        Initiate.log("Placehold BUY GUI inventory took: " + Manager.getDuration(start));
        final GUI gui = new GUI(Initiate.getPlugin(), PageData.PURCHASE_ITEM, viewInv, page);
        gui.setTitle(title);
        gui.addPass("guipage", Config.PURCHASE_GUI.toString());
        gui.addPass("status", status);
        gui.addPass("slot", slot);
        gui.addPass("amount", amount);
        Initiate.log("Load BUY GUI took: " + Manager.getDuration(start));
        gui.open(player);
        Initiate.log("Open BUY GUI took: " + Manager.getDuration(start));
    }

    public void tradeItem(final Player player, final Page page, final int slot, InventoryView view) {
        final long start = System.currentTimeMillis();
        Initiate.log("Setup TRADE GUI affordable took: " + Manager.getDuration(start));
        final Page tradepage = Manager.get().getPage(Config.TRADE_GUI.toString());
        final Inventory mainInv = this.getTradeInventory().getInventory();
        final String title = Placeholder.placehold(player, Placeholder.placehold(player, view.getTitle(), page, slot));
        final PageSlot ps = page.getPageSlot(slot);
        if (ps != null) {
            for (final ItemStack i : ps.getItems()) {
                if (mainInv.firstEmpty() == -1) {
                    break;
                }
                mainInv.setItem(mainInv.firstEmpty(), i);
            }
        }
        Initiate.log("Get TRADE GUI inventory took: " + Manager.getDuration(start));
        final Inventory viewInv = Placeholder.placehold(player, mainInv, page, tradepage, slot, 0, "confirmed", true);
        Initiate.log("Placehold TRADE GUI inventory took: " + Manager.getDuration(start));
        final GUI gui = new GUI(Initiate.getPlugin(), PageData.TRADE_ITEM, viewInv, page);
        gui.setTitle(title);
        gui.addPass("guipage", Config.TRADE_GUI.toString());
        gui.addPass("slot", slot);
        Initiate.log("Load TRADE GUI took: " + Manager.getDuration(start));
        gui.open(player);
        Initiate.log("Open TRADE GUI took: " + Manager.getDuration(start));
    }

    public void sellItem(final Player player, final Page page, final int slot, int amount, final String status, InventoryView inventoryView) {
        final InventoryCreator pi = new InventoryCreator(player.getInventory());
        if (amount < page.getDefaultQuantity()) {
            amount = page.getDefaultQuantity();
        }
        final Page sellpage = Manager.get().getPage(Config.SELL_GUI.toString());
        final Inventory mainInv = this.getSellInventory().getInventory();
        final String title = Placeholder.placehold(player, Placeholder.placehold(player, inventoryView.getTitle(), page, slot, amount, status, false));
        final Inventory viewInv = Placeholder.placehold(player, mainInv, page, sellpage, slot, amount, status, false);
        final GUI gui = new GUI(Initiate.getPlugin(), PageData.SELL_ITEM, viewInv, page);
        gui.setTitle(title);
        gui.addPass("guipage", Config.SELL_GUI.toString());
        gui.addPass("status", status);
        gui.addPass("slot", slot);
        gui.addPass("amount", amount);
        gui.open(player);
    }

    public InventoryCreator getTradeInventory() {
        final Page pgui = Manager.get().getPage(Config.TRADE_GUI.toString());
        if (pgui != null && pgui.isGUI()) {
            return new InventoryCreator(pgui.getInventory());
        }
        final InventoryCreator inv = new InventoryCreator(ChatColor.DARK_GREEN + "Trade Item", 3);
        final int[] blank = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 22, 24, 25, 26};
        inv.setBlank(blank, Material.BLACK_STAINED_GLASS_PANE, 15);
        inv.setItem(23, Material.LIME_STAINED_GLASS_PANE, 5, "&aTrade");
        inv.setItem(21, Material.RED_STAINED_GLASS_PANE, 14, "&4Cancel");
        inv.setItem(22, Material.BEDROCK, "%item_display%");
        return inv;
    }

    public InventoryCreator getSellInventory() {
        final Page pgui = Manager.get().getPage(Config.SELL_GUI.toString());
        if (pgui != null && pgui.isGUI()) {
            return new InventoryCreator(pgui.getInventory());
        }
        final InventoryCreator inv = new InventoryCreator(ChatColor.DARK_GREEN + "Sell Item", 6);
        inv.setItem(4, Material.RED_STAINED_GLASS_PANE, 14, "&4Cancel");
        final int[] blank = {3, 5, 11, 12, 13, 14, 15, 18, 19, 25, 26, 29, 30, 32, 33, 39, 40, 41, 48, 50};
        inv.setBlank(blank, Material.BLACK_STAINED_GLASS_PANE, 15);
        inv.setItem(20, Material.LIME_STAINED_GLASS_PANE, 5, "&eLeft-click&7: &a+1");
        inv.addLore(20, "&eRight-click&7: &c-1");
        inv.setItem(21, Material.LIME_STAINED_GLASS_PANE, 5, "&eLeft-click&7: &a+8");
        inv.addLore(21, "&eRight-click&7: &c-8");
        inv.setItem(22, Material.LIME_STAINED_GLASS_PANE, 5, "&eLeft-click&7: &a+16");
        inv.addLore(22, "&eRight-click&7: &c-16");
        inv.setItem(23, Material.LIME_STAINED_GLASS_PANE, 5, "&eLeft-click&7: &a+32");
        inv.addLore(23, "&eRight-click&7: &c-32");
        inv.setItem(24, Material.LIME_STAINED_GLASS_PANE, 5, "&eLeft-click&7: &a+64");
        inv.addLore(24, "&eRight-click&7: &c-64");
        inv.setItem(31, Material.LIME_STAINED_GLASS_PANE, 5, "&eLeft-click&7: &a+128");
        inv.addLore(31, "&eRight-click&7: &c-128");
        final int[] quantity = {20, 21, 22, 23, 24, 31};
        inv.addLore(quantity, " ");
        inv.addLore(quantity, "&9Amount&7: &9%amount%");
        inv.addLore(quantity, "&aEarnings&7: &a%earnings%");
        inv.setItem(49, Material.BEDROCK, "%item_display%");
        inv.addLore(49, " ");
        inv.addLore(49, "&9Amount&7: &9%amount%");
        inv.addLore(49, "&aEarnings&7: &a%earnings%");
        inv.addLore(49, " ");
        inv.addLore(49, "&2%confirm%");
        return inv;
    }

    public InventoryCreator getBuyInventory() {
        final Page pgui = Manager.get().getPage(Config.PURCHASE_GUI.toString());
        if (pgui != null && pgui.isGUI()) {
            return new InventoryCreator(pgui.getInventory());
        }
        final InventoryCreator inv = new InventoryCreator(ChatColor.DARK_GREEN + "Buy Item", 6);
        inv.setItem(4, Material.RED_STAINED_GLASS, 14, "&4Cancel");
        final int[] blank = {3, 5, 11, 12, 13, 14, 15, 18, 19, 25, 26, 29, 30, 32, 33, 39, 40, 41, 48, 50};
        inv.setBlank(blank, Material.BLACK_STAINED_GLASS_PANE, 15);
        inv.setItem(20, Material.LIME_STAINED_GLASS_PANE, 5, "&eLeft-click&7: &a+1");
        inv.addLore(20, "&eRight-click&7: &c-1");
        inv.setItem(21, Material.LIME_STAINED_GLASS_PANE, 5, "&eLeft-click&7: &a+8");
        inv.addLore(21, "&eRight-click&7: &c-8");
        inv.setItem(22, Material.LIME_STAINED_GLASS_PANE, 5, "&eLeft-click&7: &a+16");
        inv.addLore(22, "&eRight-click&7: &c-16");
        inv.setItem(23, Material.LIME_STAINED_GLASS_PANE, 5, "&eLeft-click&7: &a+32");
        inv.addLore(23, "&eRight-click&7: &c-32");
        inv.setItem(24, Material.LIME_STAINED_GLASS_PANE, 5, "&eLeft-click&7: &a+64");
        inv.addLore(24, "&eRight-click&7: &c-64");
        inv.setItem(31, Material.LIME_STAINED_GLASS_PANE, 5, "&eLeft-click&7: &a+128");
        inv.addLore(31, "&eRight-click&7: &c-128");
        final int[] quantity = {20, 21, 22, 23, 24, 31};
        inv.addLore(quantity, " ");
        inv.addLore(quantity, "&9Amount&7: &9%amount%");
        inv.addLore(quantity, "&6Balance&7: &6%balance%");
        inv.addLore(quantity, "&aPrice&7: &a%price%");
        inv.setItem(49, Material.BEDROCK, "%item_display%");
        inv.addLore(49, " ");
        inv.addLore(49, "&9Amount&7: &9%amount%");
        inv.addLore(49, "&6Balance&7: &6%balance%");
        inv.addLore(49, "&aPrice&7: &a%price%");
        inv.addLore(49, " ");
        inv.addLore(49, "&2%confirm%");
        return inv;
    }

    public static int getContainsAmount(Player player, ItemStack itemStack) {
        int count = 0;

        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item != null && item.getType().equals(itemStack.getType())) {
                if (itemStack.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName() != null) {
                    String itemName = itemStack.getItemMeta().getDisplayName();
                    String playerInvItemName = item.getItemMeta().getDisplayName();

                    if (itemName.equals(playerInvItemName)) {
                        count += item.getAmount();
                    }
                }
            }
        }

        return count;
    }
}
