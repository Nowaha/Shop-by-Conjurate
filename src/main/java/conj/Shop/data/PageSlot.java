package conj.Shop.data;

import conj.Shop.base.ItemSerialize;
import conj.Shop.control.Manager;
import conj.Shop.enums.*;
import conj.Shop.tools.InventoryCreator;
import conj.Shop.tools.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageSlot {
    public List<HashMap<Map<String, Object>, Map<String, Object>>> items;
    private String page;
    private int slot;
    private double cost;
    private double sell;
    private int cooldown;
    private HashMap<String, Long> cd;
    private Hidemode visibility;
    private Function function;
    private GUIFunction guifunction;
    private List<String> command;
    private List<String> permission;
    private List<String> hidepermission;
    private List<String> pagelore;
    private HashMap<String, Object> slotdata;

    public PageSlot(final String page, final int slot) {
        this.cd = new HashMap<String, Long>();
        this.visibility = Hidemode.VISIBLE;
        this.function = Function.NONE;
        this.guifunction = GUIFunction.NONE;
        this.command = new ArrayList<String>();
        this.permission = new ArrayList<String>();
        this.hidepermission = new ArrayList<String>();
        this.pagelore = new ArrayList<String>();
        this.items = new ArrayList<HashMap<Map<String, Object>, Map<String, Object>>>();
        this.slotdata = new HashMap<String, Object>();
        this.page = page;
        this.slot = slot;
    }

    public PageSlot(final String page, final int slot, final double cost, final double sell, final int cooldown, final HashMap<String, Long> cd, final Hidemode visibility, final Function function, final GUIFunction guifunction, final List<String> command, final List<String> permission, final List<String> hidepermission, final List<String> pagelore, final List<HashMap<Map<String, Object>, Map<String, Object>>> items, final HashMap<String, Object> slotdata) {
        this.cd = new HashMap<String, Long>();
        this.visibility = Hidemode.VISIBLE;
        this.function = Function.NONE;
        this.guifunction = GUIFunction.NONE;
        this.command = new ArrayList<String>();
        this.permission = new ArrayList<String>();
        this.hidepermission = new ArrayList<String>();
        this.pagelore = new ArrayList<String>();
        this.items = new ArrayList<HashMap<Map<String, Object>, Map<String, Object>>>();
        this.slotdata = new HashMap<String, Object>();
        this.page = page;
        this.slot = slot;
        this.cost = cost;
        this.sell = sell;
        this.cooldown = cooldown;
        this.cd = cd;
        this.visibility = visibility;
        this.function = function;
        this.guifunction = guifunction;
        this.command = ((command != null) ? command : new ArrayList<String>());
        this.permission = ((permission != null) ? permission : new ArrayList<String>());
        this.hidepermission = ((hidepermission != null) ? hidepermission : new ArrayList<String>());
        this.pagelore = ((pagelore != null) ? pagelore : new ArrayList<String>());
        this.items = ((items != null) ? items : new ArrayList<HashMap<Map<String, Object>, Map<String, Object>>>());
        this.slotdata = slotdata;
    }

    public String getPage() {
        return this.page;
    }

    public int getSlot() {
        return this.slot;
    }

    public void setSlot(final int slot) {
        this.slot = slot;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(final int seconds) {
        this.cooldown = seconds;
    }

    public double getCost() {
        return this.cost;
    }

    public void setCost(final double cost) {
        this.cost = cost;
    }

    public double getSell() {
        return this.sell;
    }

    public void setSell(final double cost) {
        this.sell = cost;
    }

    public float getDataFloat(final String key) {
        if (this.getData(key) != null && this.getData(key) instanceof Float) {
            return (float) this.getData(key);
        }
        return 0.0f;
    }

    public double getDataDouble(final String key) {
        if (this.getData(key) != null && this.getData(key) instanceof Double) {
            return (double) this.getData(key);
        }
        return 0.0;
    }

    public int getDataInt(final String key) {
        if (this.getData(key) != null && this.getData(key) instanceof Integer) {
            return (int) this.getData(key);
        }
        return 0;
    }

    public String getDataString(final String key) {
        if (this.getData(key) != null && this.getData(key) instanceof String) {
            return (String) this.getData(key);
        }
        return null;
    }

    public List<?> getDataList(final String key) {
        if (this.getData(key) != null && this.getData(key) instanceof List) {
            return (List<?>) this.getData(key);
        }
        return null;
    }

    public HashMap<String, Object> getSlotData() {
        return this.slotdata;
    }

    public Object getData(final String key) {
        return this.slotdata.get(key);
    }

    public void setData(final String key, final Object value) {
        if (this.slotdata == null) {
            this.slotdata = new HashMap<String, Object>();
        }
        this.slotdata.put(key, value);
    }

    public Object removeData(final String key) {
        return this.slotdata.remove(key);
    }

    public Hidemode getHidemode() {
        return this.visibility;
    }

    public void setHidemode(final Hidemode hidemode) {
        this.visibility = hidemode;
    }

    public Function getFunction() {
        return this.function;
    }

    public void setFunction(final Function function) {
        this.function = function;
    }

    public GUIFunction getGUIFunction() {
        return this.guifunction;
    }

    public void setGUIFunction(final GUIFunction function) {
        this.guifunction = function;
    }

    public boolean canSee(final Player player) {
        boolean see = true;
        final Hidemode hidemode = this.getHidemode();
        final List<String> permission = this.getPermissions();
        final List<String> hidepermission = this.getHidePermissions();
        if (hidemode.equals(Hidemode.HIDDEN)) {
            see = false;
        }
        if (hidemode.equals(Hidemode.OP) && !player.isOp()) {
            see = false;
        }
        if (hidemode.equals(Hidemode.PERMISSION)) {
            if (!permission.isEmpty()) {
                for (final String s : permission) {
                    if (!player.hasPermission(s)) {
                        see = false;
                        break;
                    }
                }
            }
            if (!hidepermission.isEmpty()) {
                for (final String s : hidepermission) {
                    if (player.hasPermission(s)) {
                        see = false;
                        break;
                    }
                }
            }
        }
        return see;
    }

    public void sendMessage(final Player player, final String type) {
        final List<String> messages = this.getMessage(type);
        for (final String m : messages) {
            player.sendMessage(Placeholder.placehold(player, m, Manager.get().getPage(this.page), this.slot));
        }
    }

    public void setMessage(final String type, final List<String> messages) {
        this.setData(type, messages);
    }

    public boolean addMessage(final String type, final String message) {
        final Object d = this.getData(type);
        boolean restore = false;
        if (d == null) {
            restore = true;
        } else if (((List) d).isEmpty()) {
            restore = true;
        }
        if (restore) {
            final List<String> newmessages = new ArrayList<String>();
            newmessages.add(message);
            this.setMessage(type, newmessages);
            return true;
        }
        return this.getMessage(type).add(message);
    }

    public List<String> getMessage(final String type) {
        final MessageType mt = MessageType.fromString(type);
        if (mt == null) {
            return new ArrayList<String>();
        }
        final Object d = this.getData(type);
        if (d == null) {
            return mt.getDefault();
        }
        if (!(d instanceof List)) {
            return mt.getDefault();
        }
        return (List<String>) (((List) d).isEmpty() ? mt.getDefault() : d);
    }

    public boolean removeMessage(final String type, final String message) {
        String removal = null;
        final List<String> messages = this.getMessage(type);
        for (final String s : messages) {
            if (s.contains(message)) {
                removal = s;
            }
        }
        return messages.remove((removal == null) ? message : removal);
    }

    public List<ItemStack> getItems() {
        return ItemSerialize.deserialize(this.items);
    }

    public void setItems(final List<ItemStack> items) {
        if (items == null) {
            return;
        }
        this.items = ItemSerialize.serialize(items);
    }

    public void addItem(final ItemStack item) {
        final ItemStack clone = new ItemStack(item);
        final List<ItemStack> items = this.getItems();
        if (items.add(clone)) {
            this.setItems(items);
        }
    }

    public void removeItem(final ItemStack item) {
        final List<ItemStack> items = this.getItems();
        if (items.remove(item)) {
            this.setItems(items);
        }
    }

    public boolean attemptTrade(final Player player) {
        final Page page = Manager.get().getPage(this.page);
        if (page == null) {
            return false;
        }
        if (page.getInventory().getItem(this.slot) == null) {
            return false;
        }
        boolean b = false;
        final InventoryCreator ic = new InventoryCreator(this.getItems());
        final List<ItemStack> items_only = new ArrayList<ItemStack>();
        for (int i = 0; i < 36; ++i) {
            final ItemStack item = player.getInventory().getItem(i);
            if (item != null && !item.getType().equals((Object) Material.AIR)) {
                items_only.add(new ItemStack(item));
            }
        }
        final InventoryCreator pic = new InventoryCreator(items_only, 4);
        for (final ItemStack j : ic.getInventory()) {
            if (j == null) {
                continue;
            }
            if (j.getType().equals((Object) Material.AIR)) {
                continue;
            }
            final int amount = ic.getAmount(j);
            if (!player.getInventory().containsAtLeast(j, amount)) {
                b = true;
            }
            pic.getInventory().remove(j);
        }
        if (!b) {
            if (pic.canAdd(page.getInventory().getItem(this.slot))) {
                for (final ItemStack j : this.getItems()) {
                    player.getInventory().removeItem(new ItemStack[]{j});
                }
                player.getInventory().addItem(new ItemStack[]{page.getInventory().getItem(this.slot)});
                if (page.closesOnTransaction()) {
                    player.closeInventory();
                } else {
                    page.openPage(player);
                }
            } else {
                player.sendMessage(Config.TRADE_NEED_SPACE.toString());
            }
        }
        return !b;
    }

    public List<String> getCommands() {
        return this.command;
    }

    public void setCommands(final List<String> commands) {
        this.command = commands;
    }

    public void addCommand(final String command) {
        if (this.command != null) {
            this.command.add(command);
        } else {
            final List<String> newlist = new ArrayList<String>();
            newlist.add(command);
            this.command = newlist;
        }
    }

    public boolean removeCommand(final String command) {
        return this.command.remove(command);
    }

    public HashMap<String, Long> getCooldowns() {
        return this.cd;
    }

    public long getCooldown(final Player player) {
        long cooldown = 0L;
        if (this.cd != null) {
            for (final Map.Entry<String, Long> e : this.cd.entrySet()) {
                if (e.getKey().equals(player.getUniqueId().toString())) {
                    cooldown = e.getValue();
                }
            }
        }
        return cooldown;
    }

    public boolean inCooldown(final Player player) {
        return this.inCooldown(this.getCooldown(player));
    }

    public boolean inCooldown(final long cooldown) {
        final long difference = (System.currentTimeMillis() - cooldown) / 1000L;
        return difference < this.getCooldown();
    }

    public boolean hasCooldown() {
        return this.cooldown != 0;
    }

    public boolean uncooldown(final Player player) {
        return this.cd.remove(player.getUniqueId().toString()) != null;
    }

    public Long cooldown(final Player player) {
        return this.cd.put(player.getUniqueId().toString(), System.currentTimeMillis());
    }

    public List<String> getPermissions() {
        return this.permission;
    }

    public boolean hasPermissions() {
        return !this.permission.isEmpty();
    }

    public void addPermission(final String permission) {
        if (this.permission != null) {
            this.permission.add(permission);
        } else {
            final List<String> newlist = new ArrayList<String>();
            newlist.add(permission);
            this.permission = newlist;
        }
    }

    public boolean removePermission(final String permission) {
        return this.permission.remove(permission);
    }

    public List<String> getHidePermissions() {
        return this.hidepermission;
    }

    public boolean hasHidePermissions() {
        return !this.hidepermission.isEmpty();
    }

    public void addHidePermission(final String permission) {
        if (this.hidepermission != null) {
            this.hidepermission.add(permission);
        } else {
            final List<String> newlist = new ArrayList<String>();
            newlist.add(permission);
            this.hidepermission = newlist;
        }
    }

    public boolean removeHidePermission(final String permission) {
        return this.hidepermission.remove(permission);
    }

    public List<String> getPageLore() {
        return this.pagelore;
    }

    public boolean hasPageLore() {
        return !this.pagelore.isEmpty();
    }

    public void addPageLore(final String lore) {
        if (this.pagelore != null) {
            this.pagelore.add(lore);
        } else {
            final List<String> newlist = new ArrayList<String>();
            newlist.add(lore);
            this.pagelore = newlist;
        }
    }

    public boolean removePageLore(final String lore) {
        return this.pagelore.remove(lore);
    }
}
