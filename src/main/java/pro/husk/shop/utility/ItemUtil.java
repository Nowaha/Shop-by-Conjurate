package pro.husk.shop.utility;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class ItemUtil {

    public static String serialise(ItemStack itemStack) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("i", itemStack);
        return config.saveToString();
    }

    public static ItemStack deserialise(String itemStackString) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(itemStackString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return config.getItemStack("i", null);
    }
}
