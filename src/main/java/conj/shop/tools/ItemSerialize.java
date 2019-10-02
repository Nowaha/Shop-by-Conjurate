package conj.shop.tools;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemSerialize {
    public static final List<ItemStack> deserialize(final List<HashMap<Map<String, Object>, Map<String, Object>>> items) {
        final List<ItemStack> retrieveditems = new ArrayList<ItemStack>();
        if (items == null) {
            return retrieveditems;
        }
        for (final HashMap<Map<String, Object>, Map<String, Object>> serializemap : items) {
            final Map.Entry<Map<String, Object>, Map<String, Object>> serializeditems = serializemap.entrySet().iterator().next();
            final Map<String, Object> item = serializeditems.getKey();
            final ItemStack i = ItemStack.deserialize(item);
            if (serializeditems.getValue() != null) {
                final ItemMeta meta = (ItemMeta) ConfigurationSerialization.deserializeObject(serializeditems.getValue(), ConfigurationSerialization.getClassByAlias("ItemMeta"));
                i.setItemMeta(meta);
            }
            if (i != null) {
                retrieveditems.add(i);
            }
        }
        return retrieveditems;
    }

    public static final ItemStack deserializeSingle(final List<HashMap<Map<String, Object>, Map<String, Object>>> itemserial) {
        for (final HashMap<Map<String, Object>, Map<String, Object>> serializemap : itemserial) {
            final Map.Entry<Map<String, Object>, Map<String, Object>> serializeditems = serializemap.entrySet().iterator().next();
            final Map<String, Object> item = serializeditems.getKey();
            final ItemStack i = ItemStack.deserialize(item);
            if (serializeditems.getValue() != null) {
                final ItemMeta meta = (ItemMeta) ConfigurationSerialization.deserializeObject(serializeditems.getValue(), ConfigurationSerialization.getClassByAlias("ItemMeta"));
                i.setItemMeta(meta);
            }
            if (i != null) {
                return i;
            }
        }
        return null;
    }

    public static final List<HashMap<Map<String, Object>, Map<String, Object>>> serialize(final List<ItemStack> items) {
        final List<HashMap<Map<String, Object>, Map<String, Object>>> serialized = new ArrayList<HashMap<Map<String, Object>, Map<String, Object>>>();
        for (ItemStack item : items) {
            final HashMap<Map<String, Object>, Map<String, Object>> serialization = new HashMap<Map<String, Object>, Map<String, Object>>();
            if (item == null) {
                item = new ItemStack(Material.AIR);
            }
            final Map<String, Object> itemmeta = item.hasItemMeta() ? item.getItemMeta().serialize() : null;
            item.setItemMeta(null);
            final Map<String, Object> itemstack = item.serialize();
            serialization.put(itemstack, itemmeta);
            serialized.add(serialization);
        }
        return serialized;
    }

    public static final List<HashMap<Map<String, Object>, Map<String, Object>>> serializeSingle(ItemStack item) {
        final List<HashMap<Map<String, Object>, Map<String, Object>>> serialized = new ArrayList<HashMap<Map<String, Object>, Map<String, Object>>>();
        final HashMap<Map<String, Object>, Map<String, Object>> serialization = new HashMap<Map<String, Object>, Map<String, Object>>();
        if (item == null) {
            item = new ItemStack(Material.AIR);
        }
        final Map<String, Object> itemmeta = item.hasItemMeta() ? item.getItemMeta().serialize() : null;
        item.setItemMeta(null);
        final Map<String, Object> itemstack = item.serialize();
        serialization.put(itemstack, itemmeta);
        serialized.add(serialization);
        return serialized;
    }

    public static String serializeSoft(final ItemStack item) {
        String serial = item.getType().toString() + ":" + item.getDurability();
        final ItemCreator ic = new ItemCreator(item);
        if (ic.hasDisplayName()) {
            serial = serial + ":" + ic.getName();
        }
        if (ic.hasLore()) {
            serial = serial + ":" + ic.getLore();
        }
        if (ic.hasEnchantments()) {
            serial = serial + ic.getEnchants();
        }
        return serial;
    }

    public static String serializeSoftPerfect(final ItemStack item) {
        String serial = item.getType().toString() + ":/:" + item.getDurability();
        final ItemCreator ic = new ItemCreator(item);
        if (ic.hasDisplayName()) {
            serial = serial + ":/:" + ic.getName();
        }
        if (ic.hasLore()) {
            serial = serial + ":/:" + ic.getLore();
        }
        if (ic.hasEnchantments()) {
            serial = serial + ic.getEnchants();
        }
        return serial;
    }
}
