package pro.husk.shop;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pro.husk.mysql.MySQL;

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

    private void loadYml() {

    }
}
