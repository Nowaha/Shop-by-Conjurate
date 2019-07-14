package conj.Shop.interaction;

import org.bukkit.event.inventory.*;
import org.bukkit.*;
import conj.Shop.enums.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import conj.Shop.data.*;
import org.bukkit.event.*;
import conj.Shop.events.*;
import conj.Shop.tools.*;
import conj.Shop.base.*;
import org.bukkit.plugin.*;
import java.util.*;
import org.bukkit.inventory.meta.*;

public class Editor implements Listener
{
    @EventHandler
    public void PageviewClick(final PageClickEvent event) {
        final Player player = event.getPlayer();
        final int slot = event.getSlot();
        final Page page = event.getPage();
        final ItemStack item = event.getItem();
        if (event.getPageData().equals(PageData.EDIT_ITEM_VIEW)) {
            if (event.getClick().equals((Object)ClickType.RIGHT)) {
                if (event.isTopInventory()) {
                    if (item != null && !item.getType().equals((Object)Material.AIR)) {
                        this.moveItem(player, event.getGUI(), slot, true);
                    }
                }
                else if (item != null && !item.getType().equals((Object)Material.AIR)) {
                    this.moveItem(player, event.getGUI(), slot, false);
                }
            }
            else if (event.getClick().equals((Object)ClickType.LEFT)) {
                if (event.isTopInventory() && item != null && !item.getType().equals((Object)Material.AIR)) {
                    editItem(player, event.getPage(), slot);
                }
            }
            else if (event.getClick().equals((Object)ClickType.SHIFT_RIGHT)) {
                this.moveItems(player, page);
            }
        }
        else if (event.getPageData().equals(PageData.MOVE_ITEM)) {
            if (event.getClick().equals((Object)ClickType.SHIFT_RIGHT)) {
                page.setItems(event.getGUI().getInventory());
                page.openEditor(player);
            }
            else {
                event.setCancelled(true);
            }
        }
        else if (event.getPageData().equals(PageData.EDIT_ITEM_MOVE)) {
            final int oldslot = (int) event.getGUI().getPass().get("old");
            final Inventory i = page.getInventory();
            final Inventory pi = (Inventory)player.getInventory();
            final boolean wasTop = (boolean) event.getGUI().getPass().get("top");
            if (event.isTopInventory()) {
                if (wasTop) {
                    if (i.getItem(oldslot) == null) {
                        page.openEditor(player);
                        return;
                    }
                    final ItemStack selecteditem = new ItemStack(i.getItem(oldslot));
                    final ItemStack clickeditem = i.getItem(slot);
                    i.setItem(slot, selecteditem);
                    i.setItem(oldslot, clickeditem);
                    page.swapProperties(oldslot, slot);
                    page.setItems(i);
                    page.openEditor(player);
                }
                else {
                    if (pi.getItem(oldslot) == null) {
                        page.openEditor(player);
                        return;
                    }
                    final ItemStack selecteditem = new ItemStack(pi.getItem(oldslot));
                    final ItemStack clickeditem = i.getItem(slot);
                    i.setItem(slot, selecteditem);
                    pi.setItem(oldslot, clickeditem);
                    page.setItems(i);
                    page.openEditor(player);
                }
            }
            else if (wasTop) {
                if (i.getItem(oldslot) == null) {
                    page.openEditor(player);
                    return;
                }
                final ItemStack selecteditem = new ItemStack(i.getItem(oldslot));
                final ItemStack clickeditem = pi.getItem(slot);
                pi.setItem(slot, selecteditem);
                i.setItem(oldslot, clickeditem);
                page.setItems(i);
                page.openEditor(player);
            }
            else {
                if (pi.getItem(oldslot) == null) {
                    page.openEditor(player);
                    return;
                }
                final ItemStack selecteditem = new ItemStack(pi.getItem(oldslot));
                final ItemStack clickeditem = pi.getItem(slot);
                pi.setItem(slot, selecteditem);
                pi.setItem(oldslot, clickeditem);
                page.openEditor(player);
            }
        }
        else if (event.getPageData().equals(PageData.EDIT_ITEM_MANAGE)) {
            final int itemslot = (int) event.getGUI().getPass().get("slot");
            final PageSlot ps = page.getPageSlot(itemslot);
            if (event.isTopInventory()) {
                if (event.getSlot() == 1) {
                    this.editFunction(player, page, itemslot);
                }
                else if (event.getSlot() == 8) {
                    if (ps.getFunction().equals(Function.NONE) || page.isGUI() || ps.getFunction().equals(Function.CONFIRM)) {
                        return;
                    }
                    this.editMessages(player, page, itemslot);
                }
                else if (event.getSlot() == 13) {
                    this.editItemDisplay(player, page, itemslot);
                }
                else if (event.getSlot() == 9) {
                    if (ps.getFunction().equals(Function.NONE) || page.isGUI() || ps.getFunction().equals(Function.CONFIRM)) {
                        return;
                    }
                    if (event.getClick().equals((Object)ClickType.LEFT)) {
                        this.editInput(player, page, itemslot, "cooldown");
                        player.sendMessage(ChatColor.GREEN + "Enter cooldown in seconds into chat");
                    }
                    else if (event.getClick().equals((Object)ClickType.RIGHT)) {
                        ps.setCooldown(0);
                        editItem(player, page, itemslot);
                    }
                }
                else if (event.getSlot() == 10) {
                    if (page.isGUI()) {
                        if (event.getClick().equals((Object)ClickType.LEFT)) {
                            if (ps.getGUIFunction().equals(GUIFunction.QUANTITY)) {
                                this.editInput(player, page, itemslot, "quantity");
                                player.sendMessage(ChatColor.GREEN + "Enter quantity into chat");
                            }
                        }
                        else if (event.getClick().equals((Object)ClickType.RIGHT)) {
                            ps.setData("gui_quantity", 0);
                            editItem(player, page, itemslot);
                        }
                        return;
                    }
                    this.editHidemode(player, page, itemslot);
                }
                else if (event.getSlot() == 15) {
                    if (ps.getFunction().equals(Function.NONE) || page.isGUI() || ps.getFunction().equals(Function.CONFIRM)) {
                        return;
                    }
                    if (event.getClick().equals((Object)ClickType.LEFT)) {
                        this.editInput(player, page, itemslot, "permission");
                        player.sendMessage(ChatColor.GREEN + "Enter permission into chat");
                    }
                    else if (event.getClick().equals((Object)ClickType.RIGHT)) {
                        final List<String> perms = ps.getPermissions();
                        if (perms.size() > 0) {
                            final String removal = perms.get(perms.size() - 1);
                            ps.removePermission(removal);
                        }
                        editItem(player, page, itemslot);
                    }
                }
                else if (event.getSlot() == 16) {
                    if (page.isGUI() && ps.getGUIFunction().equals(GUIFunction.CONFIRM)) {
                        if (event.getClick().equals((Object)ClickType.LEFT)) {
                            this.editInput(player, page, itemslot, "confirm1");
                            player.sendMessage(ChatColor.GREEN + "Enter message into chat");
                        }
                        else if (event.getClick().equals((Object)ClickType.RIGHT)) {
                            this.editInput(player, page, itemslot, "confirm2");
                            player.sendMessage(ChatColor.GREEN + "Enter message into chat");
                        }
                        else if (event.getClick().equals((Object)ClickType.SHIFT_LEFT)) {
                            ps.removeData("gui_confirm1");
                            editItem(player, page, itemslot);
                        }
                        else if (event.getClick().equals((Object)ClickType.SHIFT_RIGHT)) {
                            ps.removeData("gui_confirm2");
                            editItem(player, page, itemslot);
                        }
                    }
                }
                else if (event.getSlot() == 24) {
                    if (page.isGUI()) {
                        return;
                    }
                    if (event.getClick().equals((Object)ClickType.LEFT)) {
                        this.editInput(player, page, itemslot, "hidepermission");
                        player.sendMessage(ChatColor.GREEN + "Enter hide permission into chat");
                    }
                    else if (event.getClick().equals((Object)ClickType.RIGHT)) {
                        final List<String> perms = ps.getHidePermissions();
                        if (perms.size() > 0) {
                            final String removal = perms.get(perms.size() - 1);
                            ps.removeHidePermission(removal);
                        }
                        editItem(player, page, itemslot);
                    }
                }
                else if (event.getSlot() == 18) {
                    if (ps.getFunction().equals(Function.NONE) || ps.getFunction().equals(Function.COMMAND) || page.isGUI() || ps.getFunction().equals(Function.CONFIRM) || ps.getFunction().equals(Function.TRADE)) {
                        return;
                    }
                    if (event.getClick().equals((Object)ClickType.LEFT)) {
                        this.editInput(player, page, itemslot, "sell");
                        player.sendMessage(ChatColor.GREEN + "Enter sell into chat");
                        player.sendMessage(ChatColor.GREEN + "This is how much it sells per 1 of the item");
                    }
                    else if (event.getClick().equals((Object)ClickType.RIGHT)) {
                        ps.setSell(0.0);
                        editItem(player, page, itemslot);
                    }
                }
                else if (event.getSlot() == 19) {
                    if (ps.getFunction().equals(Function.NONE) || ps.getFunction().equals(Function.SELL) || ps.getFunction().equals(Function.CONFIRM) || ps.getFunction().equals(Function.TRADE)) {
                        return;
                    }
                    if (event.getClick().equals((Object)ClickType.LEFT)) {
                        this.editInput(player, page, itemslot, "cost");
                        player.sendMessage(ChatColor.GREEN + "Enter cost into chat");
                    }
                    else if (event.getClick().equals((Object)ClickType.RIGHT)) {
                        ps.setCost(0.0);
                        editItem(player, page, itemslot);
                    }
                }
                else if (event.getSlot() == 22) {
                    page.openEditor(player);
                }
                else if (event.getSlot() == 26 && ps.getFunction().equals(Function.COMMAND)) {
                    if (event.getClick().equals((Object)ClickType.LEFT)) {
                        this.editInput(player, page, itemslot, "command");
                        player.sendMessage(ChatColor.GREEN + "Enter command into chat with no slash");
                    }
                    else if (event.getClick().equals((Object)ClickType.RIGHT)) {
                        final List<String> cmd = ps.getCommands();
                        if (cmd.size() > 0) {
                            final String removal = cmd.get(cmd.size() - 1);
                            ps.removeCommand(removal);
                        }
                        editItem(player, page, itemslot);
                    }
                }
            }
        }
        else if (event.getPageData().equals(PageData.EDIT_ITEM_FUNCTION)) {
            final int itemslot = (int) event.getGUI().getPass().get("slot");
            final PageSlot ps = page.getPageSlot(itemslot);
            if (event.isTopInventory() && item != null && !item.getType().equals((Object)Material.AIR)) {
                if (!page.isGUI()) {
                    if (event.getSlot() == 8) {
                        editItem(player, page, itemslot);
                        return;
                    }
                    final String name = ChatColor.stripColor(item.getItemMeta().getDisplayName().toUpperCase().replaceAll(" ", "_"));
                    Function function = Function.NONE;
                    try {
                        function = Function.valueOf(name);
                    }
                    catch (IllegalArgumentException ex) {}
                    ps.setFunction(function);
                    editItem(player, page, itemslot);
                }
                else {
                    if (event.getSlot() == 8) {
                        editItem(player, page, itemslot);
                        return;
                    }
                    final String name = ChatColor.stripColor(item.getItemMeta().getDisplayName().toUpperCase().replaceAll(" ", "_"));
                    GUIFunction function2 = GUIFunction.NONE;
                    try {
                        function2 = GUIFunction.valueOf(name);
                    }
                    catch (IllegalArgumentException ex2) {}
                    ps.setGUIFunction(function2);
                    editItem(player, page, itemslot);
                }
            }
        }
        else if (event.getPageData().equals(PageData.EDIT_ITEM_HIDEMODE)) {
            final int itemslot = (int) event.getGUI().getPass().get("slot");
            final PageSlot ps = page.getPageSlot(itemslot);
            if (event.isTopInventory() && item != null && !item.getType().equals((Object)Material.AIR)) {
                if (event.getSlot() == 4) {
                    editItem(player, page, itemslot);
                    return;
                }
                final String name = ChatColor.stripColor(item.getItemMeta().getDisplayName().toUpperCase().replaceAll(" ", "_"));
                final Hidemode hidemode = Hidemode.valueOf(name);
                ps.setHidemode(hidemode);
                editItem(player, page, itemslot);
            }
        }
        else if (event.getPageData().equals(PageData.EDIT_ITEM_MESSAGES)) {
            final int itemslot = (int) event.getGUI().getPass().get("slot");
            final PageSlot ps = page.getPageSlot(itemslot);
            if (event.getSlot() == 1) {
                if (event.getClick().equals((Object)ClickType.LEFT)) {
                    this.editInput(player, page, itemslot, "permissionmessage");
                    player.sendMessage(ChatColor.GREEN + "Enter message you want to add into chat");
                }
                else if (event.getClick().equals((Object)ClickType.RIGHT)) {
                    final List<String> msg = ps.getMessage(MessageType.PERMISSION.toString());
                    if (msg.size() > 0) {
                        final String remove = msg.get(msg.size() - 1);
                        ps.removeMessage(MessageType.PERMISSION.toString(), remove);
                    }
                    this.editMessages(player, page, itemslot);
                }
            }
            else if (event.getSlot() == 7) {
                if (event.getClick().equals((Object)ClickType.LEFT)) {
                    this.editInput(player, page, itemslot, "cooldownmessage");
                    player.sendMessage(ChatColor.GREEN + "Enter message you want to add into chat");
                }
                else if (event.getClick().equals((Object)ClickType.RIGHT)) {
                    final List<String> msg = ps.getMessage(MessageType.COOLDOWN.toString());
                    if (msg.size() > 0) {
                        final String remove = msg.get(msg.size() - 1);
                        ps.removeMessage(MessageType.COOLDOWN.toString(), remove);
                    }
                    this.editMessages(player, page, itemslot);
                }
            }
            else if (event.getSlot() == 22) {
                editItem(player, page, itemslot);
            }
        }
        else if (event.getPageData().equals(PageData.EDIT_ITEM)) {
            final int itemslot = (int) event.getGUI().getPass().get("slot");
            if (event.getSlot() == 3) {
                if (event.getClick().equals((Object)ClickType.LEFT)) {
                    this.editInput(player, page, itemslot, "itemname");
                    player.sendMessage(ChatColor.GREEN + "Enter name you want to set into chat");
                }
                else if (event.getClick().equals((Object)ClickType.RIGHT)) {
                    final ItemStack j = page.getInventory().getItem(itemslot);
                    if (j != null) {
                        final ItemCreator ic = new ItemCreator(j);
                        final Inventory inv = page.getInventory();
                        inv.setItem(itemslot, ic.resetName());
                        page.setItems(inv);
                    }
                    this.editItemDisplay(player, page, itemslot);
                }
            }
            else if (event.getSlot() == 5) {
                if (event.getClick().equals((Object)ClickType.LEFT)) {
                    this.editInput(player, page, itemslot, "itemlore");
                    player.sendMessage(ChatColor.GREEN + "Enter lore you want to add into chat");
                }
                else if (event.getClick().equals((Object)ClickType.RIGHT)) {
                    final ItemStack j = page.getInventory().getItem(itemslot);
                    if (j != null) {
                        final List<String> lore = getLore(j);
                        Debug.log("Beginning attempt to remove lore from slot " + itemslot + " on page " + page.getID());
                        if (lore.size() > 0) {
                            final String remove = lore.get(lore.size() - 1);
                            final ItemCreator ic2 = new ItemCreator(j);
                            final Inventory inv2 = page.getInventory();
                            inv2.setItem(itemslot, ic2.removeLore(remove));
                            page.setItems(inv2);
                            Debug.log("Lore has been removed from slot " + itemslot + " on page " + page.getID());
                        }
                    }
                    this.editItemDisplay(player, page, itemslot);
                }
            }
            else if (event.getSlot() == 6) {
                final PageSlot ps = page.getPageSlot(itemslot);
                if (event.getClick().equals((Object)ClickType.LEFT)) {
                    this.editInput(player, page, itemslot, "pagelore");
                    player.sendMessage(ChatColor.GREEN + "Enter page lore into chat");
                }
                else if (event.getClick().equals((Object)ClickType.RIGHT)) {
                    final List<String> lore = ps.getPageLore();
                    if (lore.size() > 0) {
                        final String removal = lore.get(lore.size() - 1);
                        ps.removePageLore(removal);
                    }
                    this.editItemDisplay(player, page, itemslot);
                }
            }
            else if (event.getSlot() == 13) {
                if (event.getClick().equals((Object)ClickType.LEFT)) {
                    this.editInput(player, page, itemslot, "itemquantity");
                    player.sendMessage(ChatColor.GREEN + "Enter amount into chat");
                }
                else if (event.getClick().equals((Object)ClickType.RIGHT)) {
                    final ItemStack j = page.getInventory().getItem(itemslot);
                    if (j != null) {
                        j.setAmount(1);
                    }
                    page.setItem(itemslot, j);
                    this.editItemDisplay(player, page, itemslot);
                }
            }
            else if (event.getSlot() == 22) {
                editItem(player, page, itemslot);
            }
        }
    }
    
    @EventHandler
    public void inputValue(final PlayerInputEvent event) {
        final Player player = event.getPlayer();
        final int slot = event.getSlot();
        final Page page = event.getPage();
        final PageSlot ps = page.getPageSlot(slot);
        final String id = event.getID();
        final String msg = event.getMessage();
        final Input input = event.getInput();
        event.setCancelled(true);
        if (id.equalsIgnoreCase("cost")) {
            try {
                final double value = Double.parseDouble(ChatColor.stripColor(msg));
                if (value >= 0.0) {
                    ps.setCost(value);
                    input.destroy();
                    editItem(player, page, slot);
                }
                else {
                    player.sendMessage(ChatColor.RED + "Value must be greater than or equal to 0");
                }
            }
            catch (NumberFormatException nfe) {
                player.sendMessage(ChatColor.RED + "Expected number");
            }
        }
        else if (id.equalsIgnoreCase("sell")) {
            try {
                final double value = Double.parseDouble(ChatColor.stripColor(msg));
                if (value >= 0.0) {
                    ps.setSell(value);
                    input.destroy();
                    editItem(player, page, slot);
                }
                else {
                    player.sendMessage(ChatColor.RED + "Value must be greater than or equal to 0");
                }
            }
            catch (NumberFormatException nfe) {
                player.sendMessage(ChatColor.RED + "Expected number");
            }
        }
        else if (id.equalsIgnoreCase("cooldown")) {
            try {
                final int value2 = Integer.parseInt(ChatColor.stripColor(msg));
                if (value2 >= 0) {
                    ps.setCooldown(value2);
                    input.destroy();
                    editItem(player, page, slot);
                }
                else {
                    player.sendMessage(ChatColor.RED + "Value must be greater than or equal to 0");
                }
            }
            catch (NumberFormatException nfe) {
                player.sendMessage(ChatColor.RED + "Expected number");
            }
        }
        else if (id.equalsIgnoreCase("itemquantity")) {
            try {
                int value2 = Integer.parseInt(ChatColor.stripColor(msg));
                if (value2 > 0) {
                    final ItemStack i = page.getInventory().getItem(slot);
                    if (i != null) {
                        i.setAmount(value2);
                    }
                    if (value2 > i.getMaxStackSize()) {
                        value2 = i.getMaxStackSize();
                    }
                    page.setItem(slot, i);
                    input.destroy();
                    this.editItemDisplay(player, page, slot);
                }
                else {
                    player.sendMessage(ChatColor.RED + "Value must be greater than 0");
                }
            }
            catch (NumberFormatException nfe) {
                player.sendMessage(ChatColor.RED + "Expected number");
            }
        }
        else if (id.equalsIgnoreCase("command")) {
            ps.addCommand(msg);
            input.destroy();
            editItem(player, page, slot);
        }
        else if (id.equalsIgnoreCase("permission")) {
            ps.addPermission(msg);
            input.destroy();
            editItem(player, page, slot);
        }
        else if (id.equalsIgnoreCase("hidepermission")) {
            ps.addHidePermission(msg);
            input.destroy();
            editItem(player, page, slot);
        }
        else if (id.equalsIgnoreCase("permissionmessage")) {
            ps.addMessage(MessageType.PERMISSION.toString(), msg);
            input.destroy();
            this.editMessages(player, page, slot);
        }
        else if (id.equalsIgnoreCase("cooldownmessage")) {
            ps.addMessage(MessageType.COOLDOWN.toString(), msg);
            input.destroy();
            this.editMessages(player, page, slot);
        }
        else if (id.equalsIgnoreCase("itemname")) {
            final ItemStack item = page.getInventory().getItem(slot);
            if (item != null) {
                final ItemCreator ic = new ItemCreator(item);
                final Inventory j = page.getInventory();
                j.setItem(slot, ic.setName(ChatColor.translateAlternateColorCodes('&', msg)));
                page.setItems(j);
            }
            input.destroy();
            this.editItemDisplay(player, page, slot);
        }
        else if (id.equalsIgnoreCase("itemlore")) {
            final ItemStack item = page.getInventory().getItem(slot);
            if (item != null) {
                final ItemCreator ic = new ItemCreator(item);
                final Inventory j = page.getInventory();
                j.setItem(slot, ic.addLore(ChatColor.translateAlternateColorCodes('&', msg)));
                page.setItems(j);
            }
            input.destroy();
            this.editItemDisplay(player, page, slot);
        }
        else if (id.equalsIgnoreCase("pagelore")) {
            ps.addPageLore(msg);
            input.destroy();
            this.editItemDisplay(player, page, slot);
        }
        else if (id.equalsIgnoreCase("quantity")) {
            try {
                final int value2 = Integer.parseInt(ChatColor.stripColor(msg));
                if (value2 >= 0) {
                    ps.setData("gui_quantity", value2);
                    input.destroy();
                    editItem(player, page, slot);
                }
                else {
                    player.sendMessage(ChatColor.RED + "Value must be greater than or equal to 0");
                }
            }
            catch (NumberFormatException nfe) {
                player.sendMessage(ChatColor.RED + "Expected number");
            }
        }
        else if (id.equalsIgnoreCase("confirm1")) {
            ps.setData("gui_confirm1", msg);
            input.destroy();
            editItem(player, page, slot);
        }
        else if (id.equalsIgnoreCase("confirm2")) {
            ps.setData("gui_confirm2", msg);
            input.destroy();
            editItem(player, page, slot);
        }
        else {
            input.destroy();
        }
    }
    
    public void moveItem(final Player player, final GUI ogui, final int slot, final boolean top) {
        final GUI gui = new GUI(ogui);
        gui.setTitle("Click slot to move item to");
        gui.setData(PageData.EDIT_ITEM_MOVE);
        gui.addPass("old", slot);
        gui.addPass("top", top);
        gui.open(player);
    }
    
    public void editInput(final Player player, final Page page, final int slot, final String id) {
        final Input input = new Input(player, page.getID(), slot, id);
        input.register();
        player.sendMessage(ChatColor.YELLOW + "Type \"-cancel\" in the chat to cancel");
        player.sendMessage(ChatColor.YELLOW + "Type \"&&\" in the chat for a blank");
        player.closeInventory();
    }
    
    public static void editItem(final Player player, final Page page, final int slot) {
        final PageSlot ps = page.getPageSlot(slot);
        final InventoryCreator inv = new InventoryCreator(new StringBuilder().append(ChatColor.BLUE).append(slot).append(ChatColor.DARK_GRAY).append("\u2590 Properties").toString(), 3);
        if (page.getInventory().getItem(slot) == null) {
            page.openEditor(player);
            return;
        }
        inv.getInventory().setItem(4, page.getInventory().getItem(slot));
        inv.addLore(4, " ");
        inv.addLore(4, ChatColor.WHITE + "Page" + ChatColor.GRAY + ": " + ChatColor.WHITE + ChatColor.BOLD + page.getID());
        inv.addLore(4, ChatColor.BLUE + "Function" + ChatColor.DARK_GRAY + ": " + new String(page.isGUI() ? (ChatColor.BLUE + ps.getGUIFunction().toString()) : (ChatColor.BLUE + ps.getFunction().toString())));
        if (page.isGUI() && ps.getGUIFunction().equals(GUIFunction.QUANTITY)) {
            inv.addLore(4, ChatColor.DARK_GREEN + "Quantity" + ChatColor.DARK_GRAY + ": " + ChatColor.DARK_GREEN + ps.getDataInt("gui_quantity"));
        }
        if (!page.isGUI()) {
            inv.addLore(4, ChatColor.YELLOW + "Visibility" + ChatColor.DARK_GRAY + ": " + ChatColor.YELLOW + ps.getHidemode().toString());
        }
        if (!ps.getFunction().equals(Function.NONE) && !ps.getFunction().equals(Function.CONFIRM) && !ps.getFunction().equals(Function.SELL) && !ps.getFunction().equals(Function.TRADE) && !page.isGUI()) {
            inv.addLore(4, ChatColor.DARK_GREEN + "Cost" + ChatColor.DARK_GRAY + ": " + ChatColor.DARK_GREEN + DoubleUtil.toString(ps.getCost()));
        }
        if (!ps.getFunction().equals(Function.NONE) && !ps.getFunction().equals(Function.CONFIRM) && !ps.getFunction().equals(Function.COMMAND) && !ps.getFunction().equals(Function.TRADE) && !page.isGUI()) {
            inv.addLore(4, ChatColor.GREEN + "Sell" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + DoubleUtil.toString(ps.getSell()));
        }
        if (!ps.getFunction().equals(Function.NONE) && !ps.getFunction().equals(Function.CONFIRM) && ps.hasCooldown() && !page.isGUI()) {
            inv.addLore(4, ChatColor.LIGHT_PURPLE + "Cooldown" + ChatColor.DARK_GRAY + ": " + ChatColor.LIGHT_PURPLE + ps.getCooldown());
        }
        inv.getInventory().setItem(1, new ItemStack(Material.EMERALD));
        inv.setDisplay(1, ChatColor.GREEN + "Change Function");
        inv.addLore(1, ChatColor.WHITE + "Function" + ChatColor.GRAY + ": " + new String(page.isGUI() ? (ChatColor.BLUE + ps.getGUIFunction().toString()) : (ChatColor.BLUE + ps.getFunction().toString())));
        inv.addLore(1, " ");
        inv.addLore(1, ChatColor.GRAY + "Left-click to change function");
        if (!page.isGUI()) {
            inv.getInventory().setItem(10, new ItemStack(Material.GLASS));
            inv.setDisplay(10, ChatColor.GREEN + "Change Visibility");
            inv.addLore(10, ChatColor.WHITE + "Visibility" + ChatColor.GRAY + ": " + ChatColor.YELLOW + ps.getHidemode().toString());
            inv.addLore(10, " ");
            inv.addLore(10, ChatColor.GRAY + "Left-click to change visibility");
            if (ps.getFunction().equals(Function.TRADE)) {
                inv.getInventory().setItem(19, new ItemStack(Material.CHEST));
                inv.setDisplay(19, ChatColor.GREEN + "Item Inventory");
                inv.addLore(19, ChatColor.GRAY + "Manage item's inventory");
            }
            if (!ps.getFunction().equals(Function.NONE) && !ps.getFunction().equals(Function.CONFIRM)) {
                if (!ps.getFunction().equals(Function.SELL) && !ps.getFunction().equals(Function.TRADE)) {
                    inv.getInventory().setItem(19, new ItemStack(Material.GOLD_INGOT));
                    inv.setDisplay(19, ChatColor.GREEN + "Change Cost");
                    inv.addLore(19, ChatColor.WHITE + "Cost" + ChatColor.GRAY + ": " + ChatColor.DARK_GREEN + DoubleUtil.toString(ps.getCost()));
                    inv.addLore(19, " ");
                    inv.addLore(19, ChatColor.GRAY + "Left-click to set cost");
                    inv.addLore(19, ChatColor.GRAY + "Right-click to remove cost");
                }
                if (!ps.getFunction().equals(Function.COMMAND) && !ps.getFunction().equals(Function.TRADE)) {
                    inv.getInventory().setItem(18, new ItemStack(Material.DIAMOND));
                    inv.setDisplay(18, ChatColor.GREEN + "Change Sell");
                    inv.addLore(18, ChatColor.WHITE + "Sell" + ChatColor.GRAY + ": " + ChatColor.GREEN + DoubleUtil.toString(ps.getSell()));
                    inv.addLore(18, " ");
                    inv.addLore(18, ChatColor.GRAY + "Left-click to set sell");
                    inv.addLore(18, ChatColor.GRAY + "Right-click to remove sell");
                }
                inv.getInventory().setItem(9, new ItemStack(Material.CLOCK));
                inv.setDisplay(9, ChatColor.GREEN + "Change Cooldown");
                inv.addLore(9, ChatColor.WHITE + "Cooldown" + ChatColor.GRAY + ": " + ChatColor.LIGHT_PURPLE + ps.getCooldown());
                inv.addLore(9, " ");
                inv.addLore(9, ChatColor.GRAY + "Left-click to set");
                inv.addLore(9, ChatColor.GRAY + "Right-click to remove");
                inv.setItem(15, Material.GREEN_WOOL, 14, ChatColor.GREEN + "Manage Permissions");
                inv.addLore(15, " ");
                for (final String s : ps.getPermissions()) {
                    inv.addLore(15, ChatColor.WHITE + s);
                }
                inv.addLore(15, " ");
                inv.addLore(15, ChatColor.GRAY + "Left-click to add");
                inv.addLore(15, ChatColor.GRAY + "Right-click to remove");
                inv.getInventory().setItem(8, new ItemStack(Material.PAPER));
                inv.setDisplay(8, ChatColor.GOLD + "Messages");
                inv.addLore(8, ChatColor.GRAY + "Manage the messages your item sends");
            }
            inv.setItem(24, Material.GRAY_WOOL, 7, ChatColor.GREEN + "Manage Hide Permissions");
            inv.addLore(24, " ");
            for (final String s : ps.getHidePermissions()) {
                inv.addLore(24, ChatColor.WHITE + s);
            }
            inv.addLore(24, " ");
            inv.addLore(24, ChatColor.GRAY + "Left-click to add");
            inv.addLore(24, ChatColor.GRAY + "Right-click to remove");
            if (ps.getFunction().equals(Function.COMMAND)) {
                inv.setItem(26, Material.PAPER, ChatColor.GREEN + "Manage Commands");
                inv.addLore(26, " ");
                for (final String s : ps.getCommands()) {
                    inv.addLore(26, ChatColor.WHITE + s);
                }
                inv.addLore(26, " ");
                inv.addLore(26, ChatColor.GRAY + "Left-click to add");
                inv.addLore(26, ChatColor.GRAY + "Right-click to remove");
            }
        }
        inv.getInventory().setItem(22, new ItemStack(Material.COMPASS));
        inv.setDisplay(22, ChatColor.RED + "Back");
        inv.addLore(22, ChatColor.GRAY + "Return to item manager");
        inv.getInventory().setItem(13, new ItemStack(Material.PAPER));
        inv.setDisplay(13, ChatColor.GOLD + "Item Display");
        inv.addLore(13, ChatColor.GRAY + "Modify your item's display");
        if (page.isGUI()) {
            if (ps.getGUIFunction().equals(GUIFunction.QUANTITY)) {
                inv.setItem(10, Material.DIAMOND, ChatColor.GREEN + "Change Quantity");
                int amount = 0;
                amount = ps.getDataInt("gui_quantity");
                inv.addLore(10, ChatColor.WHITE + "Quantity" + ChatColor.GRAY + ": " + ChatColor.DARK_GREEN + String.valueOf(amount));
                inv.addLore(10, " ");
                inv.addLore(10, ChatColor.GRAY + "Left-click to set quantity");
                inv.addLore(10, ChatColor.GRAY + "Right-click to remove quantity");
            }
            inv.getInventory().setItem(15, new ItemStack(XMaterial.matchXMaterial("SIGN").parseMaterial()));
            inv.setDisplay(15, ChatColor.GOLD + "Placeholders");
            inv.addLore(15, ChatColor.GRAY + "%item% - item being sold/bought");
            inv.addLore(15, ChatColor.GRAY + "%earnings% - sell GUI");
            inv.addLore(15, ChatColor.GRAY + "%price% - buy GUI");
            inv.addLore(15, ChatColor.GRAY + "%amount% - selected amount");
            inv.addLore(15, ChatColor.GRAY + "%confirm% - for confirm message");
            inv.addLore(15, ChatColor.GRAY + "%quantity% - for quantity function");
            inv.addLore(15, ChatColor.GRAY + "%balance% - player's money");
            inv.addLore(15, ChatColor.GRAY + "%player% - player's name");
            inv.addLore(15, ChatColor.GRAY + "%item_display% - turns item's name");
            inv.addLore(15, ChatColor.GRAY + "and material to the item that the");
            inv.addLore(15, ChatColor.GRAY + "player is selling/buying");
            if (ps.getGUIFunction().equals(GUIFunction.CONFIRM)) {
                inv.setItem(16, Material.PAPER, ChatColor.GREEN + "Change Confirm Message");
                inv.addLore(16, " ");
                inv.addLore(16, ChatColor.WHITE + "1: " + new String((ps.getDataString("gui_confirm1") != null) ? ps.getDataString("gui_confirm1") : ""));
                inv.addLore(16, ChatColor.WHITE + "2: " + new String((ps.getDataString("gui_confirm2") != null) ? ps.getDataString("gui_confirm2") : ""));
                inv.addLore(16, " ");
                inv.addLore(16, ChatColor.GRAY + "Left-click to set confirm text 1");
                inv.addLore(16, ChatColor.GRAY + "Right-click to set confirm text 2");
                inv.addLore(16, ChatColor.GRAY + "Shift left-click to clear text 1");
                inv.addLore(16, ChatColor.GRAY + "Shift right-click to clear text 2");
            }
        }
        inv.setBlank(Material.BLUE_STAINED_GLASS_PANE, 11);
        final GUI gui = new GUI((Plugin)Initiate.getPlugin((Class)Initiate.class), PageData.EDIT_ITEM_MANAGE, inv.getInventory(), page);
        gui.addPass("slot", slot);
        gui.open(player);
    }
    
    public void editFunction(final Player player, final Page page, final int slot) {
        final InventoryCreator inv = new InventoryCreator(new StringBuilder().append(ChatColor.BLUE).append(slot).append(ChatColor.DARK_GRAY).append("\u2590 Functions").toString(), 1);
        if (!page.isGUI()) {
            Function[] values;
            for (int length = (values = Function.values()).length, i = 0; i < length; ++i) {
                final Function f = values[i];
                if (page.getType() != 0 || f != Function.CONFIRM) {
                    final ItemStack item = new ItemStack(Material.EMERALD);
                    final ItemMeta itemM = item.getItemMeta();
                    itemM.setDisplayName(ChatColor.GREEN + f.toString());
                    final ArrayList<String> lore = new ArrayList<String>();
                    lore.add(ChatColor.WHITE + f.getDescription());
                    itemM.setLore((List)lore);
                    item.setItemMeta(itemM);
                    inv.getInventory().addItem(new ItemStack[] { item });
                }
            }
            inv.getInventory().setItem(8, new ItemStack(Material.COMPASS));
            inv.setDisplay(8, ChatColor.RED + "Back");
            inv.addLore(8, ChatColor.GRAY + "Return to item properties");
        }
        else {
            GUIFunction[] values2;
            for (int length2 = (values2 = GUIFunction.values()).length, j = 0; j < length2; ++j) {
                final GUIFunction f2 = values2[j];
                final ItemStack item = new ItemStack(Material.EMERALD);
                final ItemMeta itemM = item.getItemMeta();
                itemM.setDisplayName(ChatColor.GREEN + f2.toString());
                final ArrayList<String> lore = new ArrayList<String>();
                lore.add(ChatColor.WHITE + f2.getDescription());
                itemM.setLore((List)lore);
                item.setItemMeta(itemM);
                inv.getInventory().addItem(new ItemStack[] { item });
            }
            inv.getInventory().setItem(8, new ItemStack(Material.COMPASS));
            inv.setDisplay(8, ChatColor.RED + "Back");
            inv.addLore(8, ChatColor.GRAY + "Return to item properties");
        }
        final GUI gui = new GUI((Plugin)Initiate.getPlugin((Class)Initiate.class), PageData.EDIT_ITEM_FUNCTION, inv.getInventory(), page);
        gui.addPass("slot", slot);
        gui.open(player);
    }
    
    public void editHidemode(final Player player, final Page page, final int slot) {
        final InventoryCreator inv = new InventoryCreator(new StringBuilder().append(ChatColor.BLUE).append(slot).append(ChatColor.DARK_GRAY).append("\u2590 Visibility").toString(), 1);
        Hidemode[] values;
        for (int length = (values = Hidemode.values()).length, i = 0; i < length; ++i) {
            final Hidemode hide = values[i];
            final ItemStack item = new ItemStack(Material.EMERALD);
            final ItemMeta itemM = item.getItemMeta();
            itemM.setDisplayName(ChatColor.GREEN + hide.toString());
            final ArrayList<String> lore = new ArrayList<String>();
            lore.add(ChatColor.WHITE + hide.getDescription());
            itemM.setLore((List)lore);
            item.setItemMeta(itemM);
            inv.getInventory().addItem(new ItemStack[] { item });
        }
        inv.getInventory().setItem(4, new ItemStack(Material.COMPASS));
        inv.setDisplay(4, ChatColor.RED + "Back");
        inv.addLore(4, ChatColor.GRAY + "Return to item properties");
        final GUI gui = new GUI((Plugin)Initiate.getPlugin((Class)Initiate.class), PageData.EDIT_ITEM_HIDEMODE, inv.getInventory(), page);
        gui.addPass("slot", slot);
        gui.open(player);
    }
    
    public void editMessages(final Player player, final Page page, final int slot) {
        final PageSlot ps = page.getPageSlot(slot);
        final InventoryCreator inv = new InventoryCreator(new StringBuilder().append(ChatColor.BLUE).append(slot).append(ChatColor.DARK_GRAY).append("\u2590 Messages").toString(), 3);
        final GUI gui = new GUI((Plugin)Initiate.getPlugin((Class)Initiate.class), PageData.EDIT_ITEM_MESSAGES, inv.getInventory(), page);
        inv.getInventory().setItem(22, new ItemStack(Material.COMPASS));
        inv.setDisplay(22, ChatColor.RED + "Back");
        inv.addLore(22, ChatColor.GRAY + "Return to item properties");
        inv.setItem(1, Material.PAPER, ChatColor.GREEN + "Permission Error");
        inv.addLore(1, " ");
        for (final String s : ps.getMessage(MessageType.PERMISSION.toString())) {
            inv.addLore(1, ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', s));
        }
        inv.addLore(1, " ");
        inv.addLore(1, ChatColor.GRAY + "Left-click to add");
        inv.addLore(1, ChatColor.GRAY + "Right-click to remove");
        inv.setItem(7, Material.PAPER, ChatColor.GREEN + "Cooldown Error");
        inv.addLore(7, " ");
        for (final String s : ps.getMessage(MessageType.COOLDOWN.toString())) {
            inv.addLore(7, ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', s));
        }
        inv.addLore(7, " ");
        inv.addLore(7, ChatColor.GRAY + "Left-click to add");
        inv.addLore(7, ChatColor.GRAY + "Right-click to remove");
        inv.setBlank(Material.BLUE_STAINED_GLASS_PANE, 11);
        gui.addPass("slot", slot);
        gui.open(player);
    }
    
    public void editItemDisplay(final Player player, final Page page, final int slot) {
        if (page.getInventory().getItem(slot) == null) {
            player.closeInventory();
            return;
        }
        final InventoryCreator inv = new InventoryCreator(new StringBuilder().append(ChatColor.BLUE).append(slot).append(ChatColor.DARK_GRAY).append("\u2590 Item Display").toString(), 3);
        final GUI gui = new GUI((Plugin)Initiate.getPlugin((Class)Initiate.class), PageData.EDIT_ITEM, inv.getInventory(), page);
        inv.getInventory().setItem(4, page.getInventory().getItem(slot));
        inv.getInventory().setItem(22, new ItemStack(Material.COMPASS));
        inv.setDisplay(22, ChatColor.RED + "Back");
        inv.addLore(22, ChatColor.GRAY + "Return to item properties");
        inv.setItem(3, Material.NAME_TAG, ChatColor.GREEN + "Change Display Name");
        inv.addLore(3, " ");
        inv.addLore(3, ChatColor.WHITE + getItemName(page.getInventory().getItem(slot)));
        inv.addLore(3, " ");
        inv.addLore(3, ChatColor.GRAY + "Left-click to set");
        inv.addLore(3, ChatColor.GRAY + "Right-click to reset");
        inv.setItem(5, Material.BOOK, ChatColor.GREEN + "Add Lore");
        inv.addLore(5, " ");
        for (final String s : getLore(page.getInventory().getItem(slot))) {
            inv.addLore(5, ChatColor.WHITE + s);
        }
        if (!getLore(page.getInventory().getItem(slot)).isEmpty()) {
            inv.addLore(5, " ");
        }
        inv.addLore(5, ChatColor.GRAY + "Left-click to add");
        inv.addLore(5, ChatColor.GRAY + "Right-click to remove");
        final PageSlot ps = page.getPageSlot(slot);
        if (ps != null) {
            inv.getInventory().setItem(6, new ItemStack(Material.ENCHANTED_BOOK));
            inv.setDisplay(6, ChatColor.GREEN + "Add Page Lore");
            inv.addLore(6, " ");
            for (final String s2 : ps.getPageLore()) {
                inv.addLore(6, ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', s2));
            }
            if (!ps.getPageLore().isEmpty()) {
                inv.addLore(6, " ");
            }
            inv.addLore(6, ChatColor.GRAY + "Left-click to add");
            inv.addLore(6, ChatColor.GRAY + "Right-click to remove");
        }
        inv.setItem(13, Material.ANVIL, ChatColor.GREEN + "Change Amount");
        inv.addLore(13, " ");
        inv.addLore(13, ChatColor.WHITE + String.valueOf(page.getInventory().getItem(slot).getAmount()));
        inv.addLore(13, " ");
        inv.addLore(13, ChatColor.GRAY + "Left-click to set");
        inv.addLore(13, ChatColor.GRAY + "Right-click to reset");
        inv.setBlank(Material.BLUE_STAINED_GLASS_PANE, 11);
        gui.addPass("slot", slot);
        gui.open(player);
    }
    
    public void moveItems(final Player player, final Page page) {
        final GUI gui = new GUI((Plugin)Initiate.getPlugin((Class)Initiate.class), PageData.MOVE_ITEM, page.getInventory(), page);
        gui.setTitle(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "Manual" + ChatColor.DARK_GRAY + "] Move items");
        gui.open(player);
    }
    
    public static String getItemName(final ItemStack item) {
        if (item != null) {
            return new ItemCreator(item).getName();
        }
        return ChatColor.RED + "null";
    }
    
    public static List<String> getLore(final ItemStack item) {
        List<String> lore = new ArrayList<String>();
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
            lore = (List<String>)item.getItemMeta().getLore();
        }
        return lore;
    }
}
