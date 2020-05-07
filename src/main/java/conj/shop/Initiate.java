package conj.shop;

import conj.shop.addons.NPCAddon;
import conj.shop.addons.PlaceholderAddon;
import conj.shop.addons.ShopFile;
import conj.shop.commands.control.Control;
import conj.shop.data.Page;
import conj.shop.data.Update;
import conj.shop.data.enums.Config;
import conj.shop.events.listeners.*;
import conj.shop.tools.auto.Autobackup;
import conj.shop.tools.auto.Autosave;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Initiate extends JavaPlugin {
    public static ShopFile sf;
    public static List<String> shop_purchase;
    public static List<String> shop_sell;
    public static boolean placeholderapi;
    public static boolean citizens;
    public static Economy econ;
    public static Permission perms;
    public static Chat chat;
    public static boolean debug;
    private static Initiate plugin;
    public String version;
    public String pluginname;

    public Initiate() {
        this.version = this.getDescription().getVersion();
        this.pluginname = this.getDescription().getName();
    }

    /**
     * Instance getter
     *
     * @return instance of initiate
     */
    public static Initiate getPlugin() {
        return plugin;
    }

    public static void log(final String message) {
        if (debug) {
            Initiate.getPlugin().getLogger().info(message);
        }
    }

    public static void log(final Object o) {
        if (debug) {
            Initiate.getPlugin().getLogger().info(o.toString());
        }
    }

    public void onEnable() {
        plugin = this;

        Initiate.shop_purchase = new ArrayList<String>();
        final String[] add = {"&aYou have purchased &b%quantity% &r%item% &afor &2%cost%", "&eYour new balance is &2%balance%"};

        String[] array;
        for (int length = (array = add).length, i = 0; i < length; ++i) {
            final String s = array[i];
            Initiate.shop_purchase.add(s);
        }

        Initiate.shop_sell = new ArrayList<String>();
        final String[] adds = {"&aYou have sold &b%quantity% &r%item% &afor &2%cost%", "&eYour new balance is &2%balance%"};

        String[] array2;
        for (int length2 = (array2 = adds).length, j = 0; j < length2; ++j) {
            final String s2 = array2[j];
            Initiate.shop_sell.add(s2);
        }

        // Vault
        if (!this.setupEconomy()) {
            this.getLogger().log(Level.SEVERE, "Disabled due to no Vault dependency found. (Example: Essentials)");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        // More vault
        this.setupPermissions();

        // Register command
        Control control = new Control();
        PluginCommand command = this.getCommand("shop");
        command.setExecutor(control);
        command.setTabCompleter(control);


        // Register events
        this.getServer().getPluginManager().registerEvents(new Sign(), this);
        this.getServer().getPluginManager().registerEvents(new Editor(), this);
        this.getServer().getPluginManager().registerEvents(new TradeEditor(), this);
        this.getServer().getPluginManager().registerEvents(new Shop(), this);
        this.getServer().getPluginManager().registerEvents(new PageProperties(), this);

        // Create config folder
        final boolean mainfolder = this.getDataFolder().mkdir();

        // Debug
        if (debug) {
            this.getLogger().info("Created main folder: " + mainfolder);
        }

        final File datafolder = new File(this.getDataFolder() + "/data");
        final boolean data = datafolder.mkdir();

        if (debug) {
            this.getLogger().info("Created data folder: " + data);
        }

        final File backupfolder = new File(this.getDataFolder() + "/backup");

        final boolean back = backupfolder.mkdir();

        if (debug) {
            this.getLogger().info("Created backup folder: " + back);
        }

        // Load tasks
        Update.runUpdate(1);
        Config.load();
        Update.runUpdate(2);

        // Citizens
        (Initiate.sf = new ShopFile(this.getDataFolder().getPath())).loadCitizensData();
        Initiate.sf.loadWorthData();
        Initiate.sf.loadMiscData();
        final List<Page> pages = Initiate.sf.loadPages();
        for (final Page p : pages) {
            if (debug) {
                this.getLogger().info("Loaded page: " + p.getID());
            }
        }

        // PlaceHolderAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PlaceholderAddon.register(this);
            Initiate.placeholderapi = true;
        } else {
            this.getLogger().info("PlaceholderAPI not found, alternative placeholders will be used.");
        }

        // Citizens
        if (Bukkit.getPluginManager().getPlugin("Citizens") != null) {
            try {
                CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(NPCAddon.class).withName("shop"));
                Initiate.citizens = true;
                this.getLogger().info("Successfully hooked into Citizens.");
            } catch (NullPointerException | NoClassDefFoundError npe) {
                this.getLogger().info("An error occurred when trying to register a trait. Your Citizens version might not be supported.");
            }
        } else {
            this.getLogger().info("Citizens not found. NPCs will not be available.");
        }

        // Start auto tasks
        Autosave.start();
        Autobackup.start();
    }

    public void onDisable() {
        Autosave.save();
    }

    /**
     * Vault setup
     *
     * @return
     */
    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        final RegisteredServiceProvider<Economy> rsp = (RegisteredServiceProvider<Economy>) this.getServer().getServicesManager().getRegistration((Class) Economy.class);
        if (rsp == null) {
            return false;
        }
        Initiate.econ = rsp.getProvider();
        return true;
    }

    /**
     * Vault setup
     */
    private void setupPermissions() {
        final RegisteredServiceProvider<Permission> rsp = (RegisteredServiceProvider<Permission>) this.getServer().getServicesManager().getRegistration((Class) Permission.class);
        Initiate.perms = rsp.getProvider();
    }
}
