package conj.shop.data;

import conj.shop.addons.Placeholder;
import conj.shop.addons.VaultAddon;
import conj.shop.Initiate;
import conj.shop.tools.ItemSerialize;
import conj.shop.commands.control.Manager;
import conj.shop.data.enums.*;
import conj.shop.events.custom.PageCreateEvent;
import conj.shop.events.custom.PageDeleteEvent;
import conj.shop.events.listeners.Editor;
import conj.shop.events.listeners.GUI;
import conj.shop.events.listeners.Shop;
import conj.shop.tools.DoubleUtil;
import conj.shop.tools.InventoryCreator;
import conj.shop.tools.ItemCreator;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Page {
    public String title;
    public int size;
    public int type;
    public boolean gui;
    public List<HashMap<Map<String, Object>, Map<String, Object>>> items;
    public List<Integer> slots;
    public HashMap<String, Object> pagedata;
    public HashMap<Integer, PageSlot> pageslots;
    private String id;

    public Page(final String id) {
        this.items = new ArrayList<HashMap<Map<String, Object>, Map<String, Object>>>();
        this.slots = new ArrayList<Integer>();
        this.pagedata = new HashMap<String, Object>();
        this.pageslots = new HashMap<Integer, PageSlot>();
        this.id = id;
    }

    public Page(final Page page) {
        this.items = new ArrayList<HashMap<Map<String, Object>, Map<String, Object>>>();
        this.slots = new ArrayList<Integer>();
        this.pagedata = new HashMap<String, Object>();
        this.pageslots = new HashMap<Integer, PageSlot>();
        this.id = page.getID();
        this.title = page.getTitle();
        this.size = page.getSize();
        this.type = page.getType();
        this.gui = page.isGUI();
        this.items = new ArrayList<HashMap<Map<String, Object>, Map<String, Object>>>(page.getItemsMap());
        this.slots = new ArrayList<Integer>(page.getSlots());
        this.pagedata = new HashMap<String, Object>(page.getData());
        this.pageslots = new HashMap<Integer, PageSlot>(page.pageslots);
    }

    public boolean createOverride() {
        final PageCreateEvent event = new PageCreateEvent(this);
        Bukkit.getServer().getPluginManager().callEvent(event);
        final Manager manager = new Manager();
        if (!event.isCancelled()) {
            final Page p = manager.getPage(this.getID());
            if (p != null) {
                p.delete();
            }
            return this.create();
        }
        return false;
    }

    public boolean create() {
        final PageCreateEvent event = new PageCreateEvent(this);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled() && new Manager().getPage(this.getID()) == null) {
            Manager.pages.add(this);
            return true;
        }
        return false;
    }

    public boolean delete() {
        final PageDeleteEvent event = new PageDeleteEvent(this);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            final Manager manager = new Manager();
            final Page p = manager.getPage(this.id);
            Manager.pages.remove(p);
            if (Initiate.sf.getPageFile(p.getID()).exists()) {
                Initiate.sf.getPageFile(p.getID()).getFile().delete();
            }
            return true;
        }
        return false;
    }

    public void copy(final Page page) {
        Manager.pages.remove(this);
        final Page copypage = new Page(page);
        copypage.setID(this.id);
        Manager.pages.add(copypage);
    }

    public void clearItems() {
        this.setItems(new ArrayList<ItemStack>(), new ArrayList<Integer>());
    }

    public void setItems(final List<ItemStack> items, final List<Integer> slots) {
        this.items = ItemSerialize.serialize(items);
        this.slots = slots;
    }

    public void setItem(final int slot, final ItemStack item) {
        final Inventory i = this.getInventory();
        i.setItem(slot, item);
        this.setItems(i);
    }

    public boolean moveItemSoft(final int from, final int to) {
        final ItemStack fromItem = this.getInventory().getItem(from);
        final ItemStack toItem = this.getInventory().getItem(to);
        this.setItem(to, fromItem);
        this.setItem(from, toItem);
        return true;
    }

    public boolean moveItem(final int from, final int to) {
        final ItemStack fromItem = this.getInventory().getItem(from);
        final ItemStack toItem = this.getInventory().getItem(to);
        this.setItem(to, fromItem);
        this.setItem(from, toItem);
        this.swapProperties(from, to);
        return true;
    }

    public boolean swapProperties(final int from, final int to) {
        if (this.getInventory().getSize() > from && this.getInventory().getSize() > to) {
            final PageSlot fromslot = this.getPageSlot(from);
            final PageSlot toslot = this.getPageSlot(to);
            this.removePageSlot(fromslot);
            this.removePageSlot(toslot);
            fromslot.setSlot(to);
            toslot.setSlot(from);
            this.addPageSlot(fromslot);
            this.addPageSlot(toslot);
            return true;
        }
        return false;
    }

    public PageSlot addPageSlot(final PageSlot ps) {
        return this.pageslots.put(ps.getSlot(), ps);
    }

    public PageSlot removePageSlot(final int slot) {
        return this.pageslots.remove(slot);
    }

    public PageSlot removePageSlot(final PageSlot ps) {
        return this.pageslots.remove(ps.getSlot());
    }

    public int getType() {
        return this.type;
    }

    public void setType(final int type) {
        this.type = type;
    }

    public String getID() {
        return this.id;
    }

    public void setID(final String id) {
        this.id = id;
    }

    public String getTitle() {
        return (this.title != null) ? StringUtils.left(this.title, 32) : "shop";
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public int getSize() {
        return (this.size > 0 && this.size < 7) ? (this.size * 9) : 54;
    }

    public void setSize(final int size) {
        this.size = size;
    }

    public PageSlot getPageSlot(final int slot) {
        if (this.pageslots.get(slot) == null) {
            final PageSlot newdata = new PageSlot(this.getID(), slot);
            this.addPageSlot(newdata);
        }
        return this.pageslots.get(slot);
    }

    public HashMap<Integer, PageSlot> getPageSlots() {
        return this.pageslots;
    }

    public List<Integer> getVisibleSlots(final Player player) {
        final ArrayList<Integer> slots = new ArrayList<Integer>();
        for (final int slot : this.getSlots()) {
            final PageSlot ps = this.getPageSlot(slot);
            if (ps.canSee(player)) {
                slots.add(slot);
            }
        }
        return slots;
    }

    public List<Integer> getSlots() {
        return this.slots;
    }

    public List<ItemStack> getItems() {
        return ItemSerialize.deserialize(this.items);
    }

    public void setItems(final Inventory inventory) {
        final List<ItemStack> items = new ArrayList<ItemStack>();
        final List<Integer> slots = new ArrayList<Integer>();
        for (int x = 0; inventory.getSize() > x; ++x) {
            final ItemStack item = inventory.getItem(x);
            if (item != null) {
                items.add(item);
                slots.add(x);
            }
        }
        this.items = ItemSerialize.serialize(items);
        this.slots = slots;
    }

    public List<HashMap<Map<String, Object>, Map<String, Object>>> getItemsMap() {
        return this.items;
    }

    public HashMap<String, Object> getData() {
        return this.pagedata;
    }

    public Object getData(final Object o) {
        return this.pagedata.get(o);
    }

    public boolean hasData(final Object o) {
        return this.pagedata.get(o) != null;
    }

    public Inventory getInventory() {
        final Inventory inv = Bukkit.createInventory(null, this.getSize(), this.getTitle());
        for (int x = 0; this.getItems().size() > x; ++x) {
            final int slot = this.slots.get(x);
            final ItemStack item = this.getItems().get(x);
            if (slot < this.getSize()) {
                inv.setItem(slot, item);
            }
        }
        return inv;
    }

    public Inventory getInventory(final Player player) {
        final Inventory inv = this.getInventoryFlat(player);
        return InventoryCreator.hasPlaceholder(inv) ? Placeholder.placehold(player, inv, this) : inv;
    }

    public boolean isGUI() {
        return this.gui;
    }

    public void uncooldown(final Player player) {
        for (final PageSlot ps : this.pageslots.values()) {
            ps.uncooldown(player);
        }
    }

    public Inventory getInventoryFlat(final Player player) {
        final Inventory inv = this.getInventory();
        for (int x = 0; x < inv.getSize(); ++x) {
            final PageSlot ps = this.getPageSlot(x);
            final ItemStack i = inv.getItem(x);
            boolean destroy = false;
            if (i != null) {
                destroy = !ps.canSee(player);
                if (!destroy) {
                    if (ps.getFunction().equals(Function.COMMAND)) {
                        final ItemCreator ic = new ItemCreator(i);
                        final double balance = Initiate.econ.getBalance(player);
                        if (balance < ps.getCost() && !this.hidesAffordability()) {
                            ic.addLore(Config.COST_CANNOT_AFFORD.toString());
                        }
                    } else if (ps.getFunction().equals(Function.BUY)) {
                        final ItemCreator ic = new ItemCreator(i);
                        final double balance = Initiate.econ.getBalance(player);
                        ic.addLore(" ");
                        ic.addLore(Config.COST_PREFIX.toString() + DoubleUtil.toString(ps.getCost()));
                        if (ps.getSell() > 0.0) {
                            ic.addLore(Config.SELL_PREFIX.toString() + DoubleUtil.toString(ps.getSell()));
                        }
                        if (balance < ps.getCost() && !this.hidesAffordability()) {
                            ic.addLore(Config.COST_CANNOT_AFFORD.toString());
                        }
                    } else if (ps.getFunction().equals(Function.SELL)) {
                        final ItemCreator ic = new ItemCreator(i);
                        ic.addLore("");
                        ic.addLore(Config.SELL_PREFIX.toString() + DoubleUtil.toString(ps.getSell()));
                    }
                }
            }
            if (destroy) {
                inv.setItem(x, null);
            }
        }
        return inv;
    }

    public void updateView(final Player player, final boolean hard) {
        final String pagename = Manager.get().getOpenPage(player);
        if (pagename.equals(this.getID())) {
            if (hard) {
                this.openPage(player);
                return;
            }
            final Inventory open = player.getOpenInventory().getTopInventory();
            final List<Integer> slots = this.getVisibleSlots(player);
            Initiate.log(player.getName() + " : " + this.getID() + " : " + DoubleUtil.toString(Shop.getInventoryWorth(player, open, this)));
            for (final int s : slots) {
                final ItemStack i = this.getInventoryFlat(player).getItem(s);
                if (i != null) {
                    final ItemCreator ic = new ItemCreator(i);
                    ic.placehold(player, this, s);
                    ic.replace("%worth%", DoubleUtil.toString(Shop.getInventoryWorth(player, open, this)));
                    open.setItem(s, ic.getItem());
                }
            }
        }
    }

    public Inventory openPage(final Player player) {
        final Manager manage = new Manager();
        if (!manage.getOpenPage(player).equalsIgnoreCase("")) {
            manage.setPreviousPage(player, manage.getOpenPage(player));
        }
        manage.setOpenPage(player, this.id);
        final Inventory inv = this.getInventory(player);
        if (this.getFill() != null) {
            final InventoryCreator ic = new InventoryCreator(inv);
            ic.setFill(this.getFill());
        }
        for (int i = 0; i < inv.getSize(); ++i) {
            final ItemStack item = inv.getItem(i);
            if (item != null) {
                final PageSlot ps = this.getPageSlot(i);
                if (ps.hasPageLore()) {
                    final ItemCreator ic2 = new ItemCreator(item);
                    for (final String s : ps.getPageLore()) {
                        ic2.addLore(Placeholder.placehold(player, s, this));
                    }
                }
            }
        }
        final GUI gui = new GUI(Initiate.getPlugin(), PageData.SHOP, inv, this);
        final String title = Placeholder.placehold(player, this.getTitle());
        gui.setTitle(title);
        gui.open(player);
        manage.setOpenPage(player, this.id);
        return inv;
    }

    public void openEditor(final Player player) {
        final InventoryCreator inv = new InventoryCreator(this.getInventory());
        for (int x = 0; x < inv.getInventory().getSize(); ++x) {
            final PageSlot ps = this.getPageSlot(x);
            final ItemStack i = inv.getInventory().getItem(x);
            if (i != null) {
                final double cost = ps.getCost();
                final Function f = ps.getFunction();
                final Hidemode h = ps.getHidemode();
                inv.addLore(x, " ");
                for (final String s : ps.getPageLore()) {
                    inv.addLore(x, ChatColor.translateAlternateColorCodes('&', s));
                }
                if (!ps.getPageLore().isEmpty()) {
                    inv.addLore(x, " ");
                }
                inv.addLore(x, ChatColor.BLUE + "Function" + ChatColor.DARK_GRAY + ": " + (this.isGUI() ? (ChatColor.BLUE + ps.getGUIFunction().toString()) : (ChatColor.BLUE + ps.getFunction().toString())));
                if (this.isGUI() && ps.getGUIFunction().equals(GUIFunction.QUANTITY)) {
                    inv.addLore(x, ChatColor.DARK_GREEN + "Quantity" + ChatColor.DARK_GRAY + ": " + ChatColor.DARK_GREEN + ps.getDataInt("gui_quantity"));
                }
                if (!this.isGUI()) {
                    inv.addLore(x, ChatColor.YELLOW + "Visibility" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + h.toString());
                }
                if (!f.equals(Function.NONE) && !f.equals(Function.CONFIRM) && !f.equals(Function.SELL) && !f.equals(Function.TRADE) && !this.isGUI()) {
                    inv.addLore(x, ChatColor.DARK_GREEN + "Cost" + ChatColor.DARK_GRAY + ": " + ChatColor.DARK_GREEN + DoubleUtil.toString(cost));
                }
                if (!f.equals(Function.NONE) && !f.equals(Function.CONFIRM) && !f.equals(Function.COMMAND) && !f.equals(Function.TRADE) && !this.isGUI()) {
                    inv.addLore(x, ChatColor.GREEN + "Sell" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + DoubleUtil.toString(ps.getSell()));
                }
                if (!f.equals(Function.NONE) && !f.equals(Function.CONFIRM) && ps.hasCooldown() && !this.isGUI()) {
                    inv.addLore(x, ChatColor.LIGHT_PURPLE + "Cooldown" + ChatColor.DARK_GRAY + ": " + ChatColor.LIGHT_PURPLE + ps.getCooldown());
                }
            }
        }
        final GUI gui = new GUI(Initiate.getPlugin(), PageData.EDIT_ITEM_VIEW, inv.getInventory(), this);
        gui.setTitle(ChatColor.YELLOW + this.id);
        gui.open(player);
    }

    public void buyItem(final Player player, final int slot, final int amount) {
        final PageSlot ps = this.getPageSlot(slot);
        ItemStack item = this.getInventory().getItem(slot);
        final ItemCreator itemc = new ItemCreator(item);
        item = itemc.placehold(player, this, slot);
        int affordable = new VaultAddon(Initiate.econ).getAffordable(player, ps.getCost(), amount);
        if (Initiate.econ.getBalance(player) < ps.getCost() * amount) {
            return;
        }
        for (int x = 0; x < amount; ++x) {
            final Map<Integer, ItemStack> map = player.getInventory().addItem(item);
            if (!map.values().isEmpty()) {
                --affordable;
            }
        }
        final int failed = amount - affordable;
        double finalprice = ps.getCost() * affordable;
        if (finalprice <= 0.0) {
            finalprice = 0.0;
        }
        if (finalprice > 0.0) {
            Initiate.econ.withdrawPlayer(player, finalprice);
        }
        final List<String> purchase = Config.SHOP_PURCHASE.getList();
        for (String s : purchase) {
            s = Placeholder.placehold(player, s, this);
            s = s.replaceAll("%item%", Editor.getItemName(item));
            s = s.replaceAll("%quantity%", String.valueOf(amount - failed));
            s = s.replaceAll("%cost%", DoubleUtil.toString(finalprice));
            s = s.replaceAll("%failed%", String.valueOf(failed));
            player.sendMessage(s);
        }
        if (ps.hasCooldown() && !ps.inCooldown(player)) {
            ps.cooldown(player);
        }
    }

    public void sellItem(final Player player, final int slot, final int amount) {
        final PageSlot ps = this.getPageSlot(slot);
        final ItemStack item = this.getInventory().getItem(slot);
        int failed = 0;
        for (int x = 0; x < amount; ++x) {
            final Map<Integer, ItemStack> map = player.getInventory().removeItem(item);
            if (!map.values().isEmpty()) {
                ++failed;
            }
        }
        final int affordable = amount - failed;
        double finalprice = ps.getSell() * affordable;
        if (finalprice <= 0.0) {
            finalprice = 0.0;
        }
        if (finalprice > 0.0) {
            Initiate.econ.depositPlayer(player, finalprice);
        }
        final List<String> sell = Config.SHOP_SELL.getList();
        for (String s : sell) {
            s = Placeholder.placehold(player, s, this);
            s = s.replaceAll("%item%", Editor.getItemName(item));
            s = s.replaceAll("%quantity%", String.valueOf(amount - failed));
            s = s.replaceAll("%cost%", DoubleUtil.toString(finalprice));
            player.sendMessage(s);
        }
        if (ps.hasCooldown() && !ps.inCooldown(player)) {
            ps.cooldown(player);
        }
    }

    public void updateViewers(final boolean hard) {
        for (final String p : Manager.get().getViewers(this)) {
            final Player player = Bukkit.getPlayer(p);
            if (player != null) {
                this.updateView(player, hard);
            }
        }
    }

    public void closeViewers() {
        for (final String p : Manager.get().getViewers(this)) {
            final Player player = Bukkit.getPlayer(p);
            if (player != null) {
                player.closeInventory();
            }
        }
    }

    public void saveData() {
        if (Initiate.sf != null) {
            Initiate.sf.savePageData(this.getID());
        } else {
            Initiate.log("Failed to save page: " + this.getID());
        }
    }

    public ItemStack getFill() {
        if (this.pagedata.get("fill_item") != null && this.pagedata.get("fill_item") instanceof List) {
            final ItemStack item = ItemSerialize.deserializeSingle((List<HashMap<Map<String, Object>, Map<String, Object>>>) this.pagedata.get("fill_item"));
            return item;
        }
        return null;
    }

    public void setFill(final ItemStack item) {
        if (item == null) {
            this.clearFill();
            return;
        }
        if (item.getType().equals(Material.AIR)) {
            this.clearFill();
            return;
        }
        final ItemStack serializeItem = new ItemStack(item);
        final List<HashMap<Map<String, Object>, Map<String, Object>>> serialize = ItemSerialize.serializeSingle(serializeItem);
        this.pagedata.put("fill_item", serialize);
    }

    public void clearFill() {
        this.pagedata.remove("fill_item");
    }

    public boolean instantConfirms() {
        if (this.pagedata.get("instant_confirm") != null) {
            Initiate.log("instantConfirms(): " + Boolean.parseBoolean((String) this.pagedata.get("instant_confirm")));
            return Boolean.parseBoolean((String) this.pagedata.get("instant_confirm"));
        }
        return false;
    }

    public boolean closesOnTransaction() {
        if (this.pagedata.get("close_on_transaction") != null) {
            Initiate.log("closesOnTransaction(): " + Boolean.parseBoolean((String) this.pagedata.get("close_on_transaction")));
            return Boolean.parseBoolean((String) this.pagedata.get("close_on_transaction"));
        }
        return true;
    }

    public boolean hidesAffordability() {
        if (this.pagedata.get("hide_affordability") != null) {
            Initiate.log("hidesAffordability(): " + Boolean.parseBoolean((String) this.pagedata.get("hide_affordability")));
            return Boolean.parseBoolean((String) this.pagedata.get("hide_affordability"));
        }
        return false;
    }

    public void setHideAffordability(final boolean hide) {
        Initiate.log("setHideAffordability(" + hide + "): " + this.pagedata.get("hide_affordability"));
        this.pagedata.put("hide_affordability", String.valueOf(hide));
    }

    public void setInstantConfirm(final boolean confirm) {
        Initiate.log("setInstantConfirm(" + confirm + "): " + this.pagedata.get("instant_confirm"));
        this.pagedata.put("instant_confirm", String.valueOf(confirm));
    }

    public void setCloses(final boolean closes) {
        Initiate.log("setCloses(" + closes + "): " + this.pagedata.get("close_on_transaction"));
        this.pagedata.put("close_on_transaction", String.valueOf(closes));
    }

    public int getDefaultQuantity() {
        if (this.pagedata.get("default_quantity") != null) {
            int quantity = 0;
            try {
                quantity = (int) this.pagedata.get("default_quantity");
            } catch (NumberFormatException ex) {
            }
            Initiate.log("getDefaultQuantity(): " + quantity);
            return quantity;
        }
        return 0;
    }

    public void setDefaultQuantity(final int quantity) {
        this.pagedata.put("default_quantity", quantity);
        Initiate.log("setDefaultQuantity(" + quantity + "): " + this.pagedata.get("default_quantity"));
    }
}
