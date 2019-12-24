package pro.husk.shop.utility;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import pro.husk.shop.ShopPlugin;
import pro.husk.shop.objects.Shop;
import pro.husk.shop.objects.ShopFunction;
import pro.husk.shop.objects.ShopItem;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class YamlFile {

    @Getter
    private YamlConfiguration config;

    String filename;

    /**
     * Create a new file with a specific file name which will allow you to make custom
     * configuration files to contain any data you would like in the YAML format.
     *
     * @param filename The name the file should have, excluding ".yml".
     */
    public YamlFile(String filename) {
        this.filename = filename + ".yml";
        reloadConfig();
    }

    /**
     * This will save any changes made to the data through the YamlConfiguration variable.
     * It is good practise to call this whenever you finish modifying data inside of the
     * configuration variable. If you don't save it it will get lost if not saved later.
     */
    public void saveConfig() {
        File configFile = new File(ShopPlugin.getPlugin().getDataFolder(), filename);
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will re-load the file into the configuration variable.
     * Any changes made to the file will now be applied.
     * This will void any unsaved data (saveConfig()).
     * <p>
     * If the file is missing it will first attempt to load a default file from
     * the "resources" folder, and if there is none available it will create a
     * brand new file instead.
     */
    public void reloadConfig() {
        saveConfig();
        config = YamlConfiguration.loadConfiguration(new File(ShopPlugin.getPlugin().getDataFolder(), filename));
    }

    /**
     * Static method to load all shops from saved yml to memory
     */
    public static void loadShopsFromYML() {
        File folder = new File(ShopPlugin.getPlugin().getDataFolder() + "/shops/");
        File[] allShops = folder.listFiles();

        if (allShops == null) return;

        // Iterate over all .yml saved in /shops/
        for (File shopFile : allShops) {
            if (shopFile.getName().contains(".yml")) {
                String name = shopFile.getName().replaceAll(".yml", "");

                YamlFile shopYml = new YamlFile(name);
                YamlConfiguration config = shopYml.getConfig();

                Shop shop = new Shop(name);

                ConfigurationSection section = config.getConfigurationSection("data.slot");

                if (section == null) return;

                HashMap<Integer, ShopItem> shopContents = new HashMap<>();

                for (String slot : section.getKeys(false)) {
                    int buy = section.getInt(slot + ".buy");
                    int sell = section.getInt(slot + ".sell");
                    ItemStack itemStack = ItemUtil.deserialise(section.getString(slot + ".itemstack"));
                    ShopFunction function = ShopFunction.valueOf(section.getString(slot + ".function"));
                    int slotNumber = Integer.parseInt(slot);

                    ShopItem shopItem = new ShopItem(itemStack, slotNumber, buy, sell, function);

                    shopContents.put(slotNumber, shopItem);
                }

                shop.setShopInventory(shopContents);
            }
        }
    }
}
