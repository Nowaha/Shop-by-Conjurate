package pro.husk.shop;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pro.husk.mysql.MySQL;
import pro.husk.shop.utility.YamlFile;

import java.util.logging.Logger;

public class ShopPlugin extends JavaPlugin {

    @Getter
    private static ShopPlugin plugin;

    @Getter
    private static MySQL mysql;

    @Getter
    private static Logger log;

    @Getter
    private static Economy economy = null;

    @Getter
    YamlFile shopData;

    /**
     * Everything that is needed as soon as the plugin starts up.
     * Prepare everything in here that could be needed as soon as the plugin is enabled.
     */
    public void onEnable() {
        plugin = this;
        log = getLogger();

        saveDefaultConfig();

        if (getConfig().getString("storage").equalsIgnoreCase("mysql")) {
            log.info("Storage type set to MySQL... Loading database now!");
            setupMySQL();
            loadMySQL();
        } else {
            log.info("Storage type set to yml... Loading shops now!");
            loadYml();
        }

        if (!setupEconomy()) {
            log.severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    public void onDisable() {

    }

    /**
     * This will attempt to hook onto Vault as a plugin dependency. This will allow
     * you to use the features the Vault API offers.
     *
     * @return Whether the economy successfully loaded.
     */
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    /**
     * If the saving method was set to MySQL, this should be use to make sure
     * the MySQL databases are ready to be queried.
     */
    private void setupMySQL() {
        String createRegistryTable = "CREATE TABLE `shop_registry` (\n" +
                "  `shop_id` varchar(32) NOT NULL,\n" +
                "  `shop_name` text NOT NULL\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

        String createDataTable = "CREATE TABLE IF NOT EXISTS `shop_data` (\n" +
                "  `shop_id` varchar(42) NOT NULL,\n" +
                "  `slot` int(2) NOT NULL,\n" +
                "  `itemstack` text NOT NULL,\n" +
                "  `buy_amount` int(11) NOT NULL,\n" +
                "  `sell_amount` int(11) NOT NULL,\n" +
                "  `function` text NOT NULL\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

        // Load object
        mysql = new MySQL(getConfig().getString("mysql.hostname"), getConfig().getString("mysql.port"),
                getConfig().getString("mysql.database"), getConfig().getString("mysql.username"),
                getConfig().getString("mysql.password"), getConfig().getBoolean("mysql.useSSL"));

        mysql.updateAsync(createRegistryTable);
        mysql.updateAsync(createDataTable);

        log.info("MySQL loaded, ready to roll!");
    }

    public void loadMySQL() {

    }

    /**
     * If the saving method was set to YAML, this should be use to make sure
     * the YAML files are ready to be modified and read.
     */
    private void loadYml() {
        YamlFile.loadShopsFromYML();
    }
}
