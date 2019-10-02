package conj.shop.tools;

import conj.shop.addons.Placeholder;
import conj.shop.data.Page;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ItemCreator {
    private ItemStack item;

    public ItemCreator(final ItemStack item) {
        this.item = item;
    }

    public void setType(final Material type) {
        this.item.setType(type);
    }

    public ItemStack getItem() {
        return this.item;
    }

    public boolean hasPlaceholder(final String string) {
        return string.contains("%");
    }

    public boolean hasPlaceholder(final List<String> strings) {
        final Iterator<String> iterator = strings.iterator();
        if (iterator.hasNext()) {
            final String s = iterator.next();
            return s.contains("%");
        }
        return false;
    }

    public boolean hasLore(final String lore) {
        for (final String s : this.getLore()) {
            if (s.contains(lore)) {
                return true;
            }
        }
        return false;
    }

    public ItemStack replace(final String replace, final String replacement) {
        if (this.hasLore(replace)) {
            final List<String> placehold = new ArrayList<String>();
            for (final String s : this.getLore()) {
                if (s.contains(replace)) {
                    placehold.add(s.replaceAll(replace, replacement));
                } else {
                    placehold.add(s);
                }
            }
            this.setLore(placehold);
        }
        if (this.hasDisplayName()) {
            String name = this.getName();
            if (this.getName().contains(replace)) {
                name = name.replaceAll(replace, replacement);
            }
            this.setName(name);
        }
        return this.item;
    }

    public String getNameExact() {
        if (!this.hasDisplayName()) {
            return null;
        }
        return this.getName();
    }

    public String getName() {
        if (this.item == null) {
            return ChatColor.RED + "null";
        }
        if (this.item.hasItemMeta() && this.item.getItemMeta().hasDisplayName()) {
            return this.item.getItemMeta().getDisplayName();
        }
        String name = this.item.getType().toString().toLowerCase().replaceAll("_", " ");
        name = WordUtils.capitalizeFully(name);
        return this.filterName(name, this.item.getDurability());
    }

    public String filterName(String type, final int damage) {
        if (type.equalsIgnoreCase("Ink Sack")) {
            switch (damage) {
                case 1: {
                    type = "Rose Red";
                    break;
                }
                case 2: {
                    type = "Cactus Green";
                    break;
                }
                case 3: {
                    type = "Cocoa Beans";
                    break;
                }
                case 4: {
                    type = "Lapis Lazuli";
                    break;
                }
                case 5: {
                    type = "Purple Dye";
                    break;
                }
                case 6: {
                    type = "Cyan Dye";
                    break;
                }
                case 7: {
                    type = "Light Gray Dye";
                    break;
                }
                case 8: {
                    type = "Gray Dye";
                    break;
                }
                case 9: {
                    type = "Pink Dye";
                    break;
                }
                case 10: {
                    type = "Lime Dye";
                    break;
                }
                case 11: {
                    type = "Dandelion Yellow";
                    break;
                }
                case 12: {
                    type = "Light Blue Dye";
                    break;
                }
                case 13: {
                    type = "Magenta Dye";
                    break;
                }
                case 14: {
                    type = "Orange Dye";
                    break;
                }
                case 15: {
                    type = "Bone Meal";
                    break;
                }
            }
        } else if (type.equalsIgnoreCase("Wood")) {
            switch (damage) {
                case 0: {
                    type = "Oak Wood Planks";
                    break;
                }
                case 1: {
                    type = "Spruce Wood Planks";
                    break;
                }
                case 2: {
                    type = "Birch Wood Planks";
                    break;
                }
                case 3: {
                    type = "Jungle Wood Planks";
                    break;
                }
                case 4: {
                    type = "Acacia Wood Planks";
                    break;
                }
                case 5: {
                    type = "Dark Oak Wood Planks";
                    break;
                }
            }
        } else if (type.equalsIgnoreCase("Log")) {
            switch (damage) {
                case 0: {
                    type = "Oak Wood";
                    break;
                }
                case 1: {
                    type = "Spruce Wood";
                    break;
                }
                case 2: {
                    type = "Birch Wood";
                    break;
                }
                case 3: {
                    type = "Jungle Wood";
                    break;
                }
            }
        } else if (type.equalsIgnoreCase("Log 2")) {
            switch (damage) {
                case 0: {
                    type = "Acacia Wood";
                    break;
                }
                case 1: {
                    type = "Dark Oak Wood";
                    break;
                }
            }
        } else if (type.equalsIgnoreCase("Sapling")) {
            switch (damage) {
                case 0: {
                    type = "Oak Sapling";
                    break;
                }
                case 1: {
                    type = "Spruce Sapling";
                    break;
                }
                case 2: {
                    type = "Birch Sapling";
                    break;
                }
                case 3: {
                    type = "Jungle Sapling";
                    break;
                }
                case 4: {
                    type = "Acacia Sapling";
                    break;
                }
                case 5: {
                    type = "Dark Oak Sapling";
                    break;
                }
            }
        } else if (type.equalsIgnoreCase("Leaves")) {
            switch (damage) {
                case 0: {
                    type = "Oak Leaves";
                    break;
                }
                case 1: {
                    type = "Spruce Leaves";
                    break;
                }
                case 2: {
                    type = "Birch Leaves";
                    break;
                }
                case 3: {
                    type = "Jungle Leaves";
                    break;
                }
            }
        } else if (type.equalsIgnoreCase("Leaves 2")) {
            switch (damage) {
                case 0: {
                    type = "Acacia Leaves";
                    break;
                }
                case 1: {
                    type = "Dark Oak Leaves";
                    break;
                }
            }
        } else if (type.equalsIgnoreCase("Red Rose")) {
            switch (damage) {
                case 0: {
                    type = "Poppy";
                    break;
                }
                case 1: {
                    type = "Blue Orchid";
                    break;
                }
                case 2: {
                    type = "Allium";
                    break;
                }
                case 3: {
                    type = "Azure Bluet";
                    break;
                }
                case 4: {
                    type = "Red Tulip";
                    break;
                }
                case 5: {
                    type = "Orange Tulip";
                    break;
                }
                case 6: {
                    type = "White Tulip";
                    break;
                }
                case 7: {
                    type = "Pink Tulip";
                    break;
                }
                case 8: {
                    type = "Oxeye Daisy";
                    break;
                }
            }
        } else if (type.equalsIgnoreCase("Double Plant")) {
            switch (damage) {
                case 0: {
                    type = "Sunflower";
                    break;
                }
                case 1: {
                    type = "Lilac";
                    break;
                }
                case 2: {
                    type = "Double Tallgrass";
                    break;
                }
                case 3: {
                    type = "Large Fern";
                    break;
                }
                case 4: {
                    type = "Rose Bush";
                    break;
                }
                case 5: {
                    type = "Peony";
                    break;
                }
            }
        } else if (type.equalsIgnoreCase("Wool")) {
            switch (damage) {
                case 0: {
                    type = "White Wool";
                    break;
                }
                case 1: {
                    type = "Orange Wool";
                    break;
                }
                case 2: {
                    type = "Magenta Wool";
                    break;
                }
                case 3: {
                    type = "Light Blue Wool";
                    break;
                }
                case 4: {
                    type = "Yellow Wool";
                    break;
                }
                case 5: {
                    type = "Lime Wool";
                    break;
                }
                case 6: {
                    type = "Pink Wool";
                    break;
                }
                case 7: {
                    type = "Gray Wool";
                    break;
                }
                case 8: {
                    type = "Light Gray Wool";
                    break;
                }
                case 9: {
                    type = "Cyan Wool";
                    break;
                }
                case 10: {
                    type = "Purple Wool";
                    break;
                }
                case 11: {
                    type = "Blue Wool";
                    break;
                }
                case 12: {
                    type = "Brown Wool";
                    break;
                }
                case 13: {
                    type = "Green Wool";
                    break;
                }
                case 14: {
                    type = "Red Wool";
                    break;
                }
                case 15: {
                    type = "Black Wool";
                    break;
                }
            }
        } else if (type.equalsIgnoreCase("Stained Glass")) {
            switch (damage) {
                case 0: {
                    type = "White Stained Glass";
                    break;
                }
                case 1: {
                    type = "Orange Stained Glass";
                    break;
                }
                case 2: {
                    type = "Magenta Stained Glass";
                    break;
                }
                case 3: {
                    type = "Light Blue Stained Glass";
                    break;
                }
                case 4: {
                    type = "Yellow Stained Glass";
                    break;
                }
                case 5: {
                    type = "Lime Stained Glass";
                    break;
                }
                case 6: {
                    type = "Pink Stained Glass";
                    break;
                }
                case 7: {
                    type = "Gray Stained Glass";
                    break;
                }
                case 8: {
                    type = "Light Gray Stained Glass";
                    break;
                }
                case 9: {
                    type = "Cyan Stained Glass";
                    break;
                }
                case 10: {
                    type = "Purple Stained Glass";
                    break;
                }
                case 11: {
                    type = "Blue Stained Glass";
                    break;
                }
                case 12: {
                    type = "Brown Stained Glass";
                    break;
                }
                case 13: {
                    type = "Green Stained Glass";
                    break;
                }
                case 14: {
                    type = "Red Stained Glass";
                    break;
                }
                case 15: {
                    type = "Black Stained Glass";
                    break;
                }
            }
        } else if (type.equalsIgnoreCase("Stained Glass Pane")) {
            switch (damage) {
                case 0: {
                    type = "White Stained Glass Pane";
                    break;
                }
                case 1: {
                    type = "Orange Stained Glass Pane";
                    break;
                }
                case 2: {
                    type = "Magenta Stained Glass Pane";
                    break;
                }
                case 3: {
                    type = "Light Blue Stained Glass Pane";
                    break;
                }
                case 4: {
                    type = "Yellow Stained Glass Pane";
                    break;
                }
                case 5: {
                    type = "Lime Stained Glass Pane";
                    break;
                }
                case 6: {
                    type = "Pink Stained Glass Pane";
                    break;
                }
                case 7: {
                    type = "Gray Stained Glass Pane";
                    break;
                }
                case 8: {
                    type = "Light Gray Stained Glass Pane";
                    break;
                }
                case 9: {
                    type = "Cyan Stained Glass Pane";
                    break;
                }
                case 10: {
                    type = "Purple Stained Glass Pane";
                    break;
                }
                case 11: {
                    type = "Blue Stained Glass Pane";
                    break;
                }
                case 12: {
                    type = "Brown Stained Glass Pane";
                    break;
                }
                case 13: {
                    type = "Green Stained Glass Pane";
                    break;
                }
                case 14: {
                    type = "Red Stained Glass Pane";
                    break;
                }
                case 15: {
                    type = "Black Stained Glass Pane";
                    break;
                }
            }
        } else if (type.equalsIgnoreCase("Smooth Brick")) {
            switch (damage) {
                case 0: {
                    type = "Stone Bricks";
                    break;
                }
                case 1: {
                    type = "Mossy Stone Bricks";
                    break;
                }
                case 2: {
                    type = "Cracked Stone Bricks";
                    break;
                }
                case 3: {
                    type = "Chiseled Stone Bricks";
                    break;
                }
            }
        } else if (type.equalsIgnoreCase("Monster Eggs")) {
            switch (damage) {
                case 0: {
                    type = "Stone Monster Egg";
                    break;
                }
                case 1: {
                    type = "Cobblestone Monster Egg";
                    break;
                }
                case 2: {
                    type = "Stone Brick Monster Egg";
                    break;
                }
                case 3: {
                    type = "Mossy Stone Brick Monster Egg";
                    break;
                }
                case 4: {
                    type = "Cracked Stone Brick Monster Egg";
                    break;
                }
                case 5: {
                    type = "Chiseled Stone Brick Monster Egg";
                    break;
                }
            }
        } else if (type.equalsIgnoreCase("Sponge")) {
            switch (damage) {
                case 1: {
                    type = "Wet Sponge";
                    break;
                }
            }
        } else if (type.equalsIgnoreCase("Skull Item")) {
            switch (damage) {
                case 0: {
                    type = "Skeleton Skull";
                    break;
                }
                case 1: {
                    type = "Wither Skeleton Skull";
                    break;
                }
                case 2: {
                    type = "Zombie Head";
                    break;
                }
                case 3: {
                    type = "Head";
                    break;
                }
                case 4: {
                    type = "Creeper Head";
                    break;
                }
                case 5: {
                    type = "Dragon Head";
                    break;
                }
            }
        } else if (type.equalsIgnoreCase("Raw Fish")) {
            switch (damage) {
                case 1: {
                    type = "Raw Salmon";
                    break;
                }
                case 2: {
                    type = "Clownfish";
                    break;
                }
                case 3: {
                    type = "Pufferfish";
                    break;
                }
            }
        } else if (type.equalsIgnoreCase("Cooked Fish")) {
            switch (damage) {
                case 1: {
                    type = "Cooked Salmon";
                    break;
                }
            }
        } else if (type.equalsIgnoreCase("Carrot Item")) {
            switch (damage) {
                case 0: {
                    type = "Carrot";
                    break;
                }
            }
        } else if (type.equalsIgnoreCase("Stone")) {
            switch (damage) {
                case 1: {
                    type = "Granite";
                    break;
                }
                case 2: {
                    type = "Polished Granite";
                    break;
                }
                case 3: {
                    type = "Diorite";
                    break;
                }
                case 4: {
                    type = "Polished Diorite";
                    break;
                }
                case 5: {
                    type = "Andesite";
                    break;
                }
                case 6: {
                    type = "Polished Andesite";
                    break;
                }
            }
        }
        return type;
    }

    public List<String> getLore() {
        List<String> lore = new ArrayList<String>();
        if (this.item != null && this.item.hasItemMeta() && this.item.getItemMeta().hasLore()) {
            lore = this.item.getItemMeta().getLore();
        }
        return lore;
    }

    public boolean hasEnchantments() {
        return this.item.hasItemMeta() && this.item.getItemMeta().hasEnchants();
    }

    public String getEnchants() {
        String enchantments = "";
        if (this.item.hasItemMeta() && this.hasEnchantments()) {
            for (final Map.Entry<Enchantment, Integer> e : this.item.getEnchantments().entrySet()) {
                enchantments = enchantments + "[" + e.getKey().getName() + "#" + e.getValue() + "]";
            }
        }
        return enchantments;
    }

    public ItemStack resetName() {
        final ItemStack newitem = new ItemStack(this.item.getType(), this.item.getAmount(), this.item.getDurability());
        final ItemCreator newitemc = new ItemCreator(newitem);
        newitem.setData(this.item.getData());
        newitem.setDurability(this.item.getDurability());
        if (!this.getLore().isEmpty()) {
            newitemc.setLore(this.getLore());
        }
        if (!this.item.getEnchantments().isEmpty()) {
            newitemc.setEnchants(this.item.getEnchantments());
        }
        return this.item = newitem;
    }

    public ItemStack placehold(final Player player) {
        if (this.hasPlaceholder(this.getName())) {
            String n = this.getName();
            n = Placeholder.placehold(player, n);
            this.setName(n);
        }
        if (this.hasPlaceholder(this.getLore())) {
            List<String> l = this.getLore();
            l = Placeholder.placehold(player, l);
            this.setLore(l);
        }
        return this.item;
    }

    public ItemStack placehold(final Player player, final Page page) {
        this.item = this.placehold(player);
        if (this.hasPlaceholder(this.getName())) {
            String n = this.getName();
            n = Placeholder.placehold(player, n, page);
            this.setName(n);
        }
        if (this.hasPlaceholder(this.getLore())) {
            List<String> l = this.getLore();
            l = Placeholder.placehold(player, l, page);
            this.setLore(l);
        }
        return this.item;
    }

    public ItemStack placehold(final Player player, final Page page, final int slot) {
        this.item = this.placehold(player);
        if (this.hasPlaceholder(this.getName())) {
            String n = this.getName();
            n = Placeholder.placehold(player, n, page, slot);
            this.setName(n);
        }
        if (this.hasPlaceholder(this.getLore())) {
            List<String> l = this.getLore();
            l = Placeholder.placehold(player, l, page, slot);
            this.setLore(l);
        }
        return this.item;
    }

    public ItemStack setName(final String name) {
        final ItemMeta m = this.item.getItemMeta();
        if (m != null) {
            m.setDisplayName(name);
            this.item.setItemMeta(m);
        }
        return this.item;
    }

    public ItemStack setLore(final List<String> lore) {
        final ItemMeta m = this.item.getItemMeta();
        if (m != null) {
            m.setLore(lore);
            this.item.setItemMeta(m);
        }
        return this.item;
    }

    public ItemStack setEnchants(final Map<Enchantment, Integer> enchants) {
        final ItemMeta m = this.item.getItemMeta();
        for (final Map.Entry<Enchantment, Integer> e : enchants.entrySet()) {
            final Enchantment ench = e.getKey();
            final int level = e.getValue();
            m.addEnchant(ench, level, true);
        }
        this.item.setItemMeta(m);
        return this.item;
    }

    public ItemStack addLore(final String lore) {
        final ItemMeta m = this.item.getItemMeta();
        List<String> l = new ArrayList<String>();
        if (m.hasLore()) {
            l = m.getLore();
        }
        l.add(lore);
        m.setLore(l);
        this.item.setItemMeta(m);
        return this.item;
    }

    public ItemStack removeLore(final String lore) {
        final ItemMeta m = this.item.getItemMeta();
        List<String> l = new ArrayList<String>();
        if (m.hasLore()) {
            l = m.getLore();
        }
        l.remove(lore);
        m.setLore(l);
        this.item.setItemMeta(m);
        return this.item;
    }

    public String getEnchantmentString() {
        if (this.item.getEnchantments().isEmpty()) {
            return null;
        }
        String enchantString = "";
        final int max = 0;
        for (final Map.Entry<Enchantment, Integer> e : this.item.getEnchantments().entrySet()) {
            enchantString = enchantString + e.getKey().getName() + ":" + e.getValue();
            if (max < this.item.getEnchantments().size()) {
                enchantString = enchantString + "/";
            }
        }
        return enchantString;
    }

    public void applyEnchantmentString(final String enchants) {
        final String[] split1 = enchants.split("/");
        for (final String s : split1) {
            final String[] split2 = s.split(":");
            if (split2.length < 2) {
                continue;
            }
            final Enchantment e = Enchantment.getByName(split2[0].toUpperCase().replaceAll(" ", "_"));
            if (e == null) {
                continue;
            }
            final int level = Integer.parseInt(split2[1]);
            this.item.addEnchantment(e, level);
        }
    }

    public boolean hasDisplayName() {
        final ItemMeta m = this.item.getItemMeta();
        return m != null && m.hasDisplayName();
    }

    public boolean hasLore() {
        final ItemMeta m = this.item.getItemMeta();
        return m != null && m.hasLore();
    }
}
