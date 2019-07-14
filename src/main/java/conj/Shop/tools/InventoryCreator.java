package conj.Shop.tools;

import conj.Shop.interaction.Editor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class InventoryCreator {
    String title;
    int size;
    Inventory inv;

    public InventoryCreator(final List<ItemStack> items, final int size) {
        this.inv = Bukkit.createInventory((InventoryHolder) null, size * 9);
        for (final ItemStack i : items) {
            if (i != null) {
                this.inv.addItem(new ItemStack[]{i});
            }
        }
    }

    public InventoryCreator(final List<ItemStack> items) {
        int size = 1;
        if (items.size() > 9) {
            size = 2;
        }
        if (items.size() > 18) {
            size = 3;
        }
        if (items.size() > 27) {
            size = 4;
        }
        if (items.size() > 36) {
            size = 5;
        }
        if (items.size() > 45) {
            size = 6;
        }
        this.inv = Bukkit.createInventory((InventoryHolder) null, size * 9);
        for (final ItemStack i : items) {
            if (i != null) {
                this.inv.addItem(new ItemStack[]{i});
            }
        }
    }

    public InventoryCreator(final ItemStack[] items) {
        int size = 1;
        if (items.length > 9) {
            size = 2;
        }
        if (items.length > 18) {
            size = 3;
        }
        if (items.length > 27) {
            size = 4;
        }
        if (items.length > 36) {
            size = 5;
        }
        if (items.length > 45) {
            size = 6;
        }
        this.inv = Bukkit.createInventory((InventoryHolder) null, size * 9);
        for (final ItemStack i : items) {
            if (i != null) {
                this.inv.addItem(new ItemStack[]{i});
            }
        }
    }

    public InventoryCreator(final Inventory inventory) {
        this.inv = inventory;
    }

    public InventoryCreator(final Inventory inventory, final boolean copy) {
        if (copy) {
            final Inventory i = Bukkit.createInventory((InventoryHolder) null, 54);
            i.setContents(inventory.getContents());
            this.inv = i;
        } else {
            this.inv = inventory;
        }
    }

    public InventoryCreator(final String title, final int size) {
        this.title = title;
        this.size = size;
        this.inv = Bukkit.createInventory((InventoryHolder) null, size * 9, title);
    }

    public static boolean hasPlaceholder(final Inventory inventory) {
        for (int x = 0; inventory.getSize() > x; ++x) {
            final ItemStack i = inventory.getItem(x);
            if (i != null) {
                final ItemCreator ic = new ItemCreator(i);
                for (final String s : ic.getLore()) {
                    if (s.contains("%")) {
                        return true;
                    }
                }
                if (ic.getName().contains("%")) {
                    return true;
                }
            }
        }
        return false;
    }

    public Inventory getInventory() {
        return this.inv;
    }

    public void setItem(final int slot, final Material type, final int damage, final String name) {
        final ItemStack i = new ItemStack(type, 1);
        final ItemMeta im = i.getItemMeta();
        im.setDisplayName(name);
        i.setItemMeta(im);
        this.inv.setItem(slot, i);
    }

    public void setItem(final int[] slot, final Material type, final int damage, final String name) {
        final ItemStack i = new ItemStack(type, 1, (short) damage);
        final ItemMeta im = i.getItemMeta();
        im.setDisplayName(name);
        i.setItemMeta(im);
        for (final int s : slot) {
            this.inv.setItem(s, i);
        }
    }

    public void setItem(final int slot, final Material type, final String name) {
        final ItemStack i = new ItemStack(type, 1);
        final ItemMeta im = i.getItemMeta();
        im.setDisplayName(name);
        i.setItemMeta(im);
        this.inv.setItem(slot, i);
    }

    public void setItem(final int[] slot, final Material type, final String name) {
        final ItemStack i = new ItemStack(type, 1);
        final ItemMeta im = i.getItemMeta();
        im.setDisplayName(name);
        i.setItemMeta(im);
        for (final int s : slot) {
            this.inv.setItem(s, i);
        }
    }

    public void setBlank(final int[] slot, final Material type) {
        final ItemStack i = new ItemStack(type);
        final ItemMeta im = i.getItemMeta();
        im.setDisplayName(" ");
        i.setItemMeta(im);
        for (final int s : slot) {
            this.inv.setItem(s, i);
        }
    }

    public void setBlank(final int[] slot, final Material type, final int damage) {
        final ItemStack i = new ItemStack(type, 1, (short) damage);
        final ItemMeta im = i.getItemMeta();
        im.setDisplayName(" ");
        i.setItemMeta(im);
        for (final int s : slot) {
            this.inv.setItem(s, i);
        }
    }

    public void setFill(final ItemStack i) {
        for (int x = 0; x < this.inv.getSize(); ++x) {
            final ItemStack item = this.inv.getItem(x);
            if (item == null) {
                this.inv.setItem(x, i);
            }
        }
    }

    public void setBlank(final Material type, final int damage) {
        final ItemStack i = new ItemStack(type, 1);
        final ItemMeta im = i.getItemMeta();
        im.setDisplayName(" ");
        i.setItemMeta(im);
        for (int x = 0; x < this.inv.getSize(); ++x) {
            final ItemStack item = this.inv.getItem(x);
            if (item == null) {
                this.inv.setItem(x, i);
            }
        }
    }

    public void setDisplay(final int slot, final String name) {
        if (this.inv.getItem(slot) != null) {
            final ItemStack item = this.inv.getItem(slot);
            final ItemMeta im = item.getItemMeta();
            im.setDisplayName(name);
            item.setItemMeta(im);
        }
    }

    public void addLore(final int slot, final String lore) {
        if (this.inv.getItem(slot) != null) {
            final ItemStack item = this.inv.getItem(slot);
            final ItemMeta im = item.getItemMeta();
            List<String> l = new ArrayList<String>();
            if (im.hasLore()) {
                l = (List<String>) im.getLore();
            }
            l.add(lore);
            im.setLore((List) l);
            item.setItemMeta(im);
        }
    }

    public void addLore(final int[] slots, final String lore) {
        for (final int slot : slots) {
            if (this.inv.getItem(slot) != null) {
                final ItemStack item = this.inv.getItem(slot);
                final ItemMeta im = item.getItemMeta();
                List<String> l = new ArrayList<String>();
                if (im.hasLore()) {
                    l = (List<String>) im.getLore();
                }
                l.add(lore);
                im.setLore((List) l);
                item.setItemMeta(im);
            }
        }
    }

    public void setItem(final int i, final ItemStack item) {
        this.inv.setItem(i, item);
    }

    public int getAmount(final ItemStack item) {
        int amount = 0;
        for (int x = 0; this.inv.getSize() > x; ++x) {
            final ItemStack i = this.inv.getItem(x);
            if (i != null && i.isSimilar(item)) {
                amount += i.getAmount();
            }
        }
        return amount;
    }

    public void replace(final String replace, final String replacement) {
        for (int x = 0; this.inv.getSize() > x; ++x) {
            final ItemStack item = this.inv.getItem(x);
            if (item != null) {
                final ItemMeta itemmeta = item.getItemMeta();
                List<String> lore = new ArrayList<String>();
                String name = Editor.getItemName(item);
                if (itemmeta.hasLore()) {
                    lore = (List<String>) itemmeta.getLore();
                    final List<String> placehold = new ArrayList<String>();
                    for (final String s : lore) {
                        if (s.contains(replace)) {
                            placehold.add(s.replaceAll(replace, replacement));
                        } else {
                            placehold.add(s);
                        }
                    }
                    itemmeta.setLore((List) placehold);
                }
                if (name.contains(replace)) {
                    name = name.replaceAll(replace, replacement);
                    itemmeta.setDisplayName(name);
                }
                item.setItemMeta(itemmeta);
            }
        }
    }

    public boolean canAdd(final ItemStack item) {
        if (this.getInventory().firstEmpty() != -1) {
            return true;
        }
        for (final ItemStack i : this.getInventory()) {
            if (i == null) {
                continue;
            }
            if (i.getType().equals((Object) Material.AIR)) {
                continue;
            }
            if (!i.isSimilar(item)) {
                continue;
            }
            final int c = i.getMaxStackSize() - i.getAmount();
            if (item.getAmount() <= c) {
                return true;
            }
        }
        return false;
    }
}
