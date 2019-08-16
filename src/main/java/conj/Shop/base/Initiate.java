package conj.Shop.base;

import conj.Shop.auto.Autobackup;
import conj.Shop.auto.Autosave;
import conj.Shop.control.Control;
import conj.Shop.data.Page;
import conj.Shop.data.Sign;
import conj.Shop.data.Update;
import conj.Shop.enums.Config;
import conj.Shop.interaction.Editor;
import conj.Shop.interaction.PageProperties;
import conj.Shop.interaction.Shop;
import conj.Shop.interaction.TradeEditor;
import conj.Shop.tools.Debug;
import conj.Shop.tools.NPCAddon;
import conj.Shop.tools.PlaceholderAddon;
import conj.UA.api.files.ShopFile;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
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
    public String version;
    public String pluginname;
    private static Initiate plugin;

    public Initiate() {
        this.version = this.getDescription().getVersion();
        this.pluginname = this.getDescription().getName();
    }

    public void onEnable() {
        plugin = this;
        Initiate.shop_purchase = new ArrayList<String>();
        final String[] add = {"&aYou have purchased &b%quantity% &r%item% &afor &2%cost%", "&cA total of &4%failed% &cfailed to purchase.", "&eYour new balance is &2%balance%"};
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
        if (!this.setupEconomy()) {
            this.getLogger().log(Level.SEVERE, "Disabled due to no Vault dependency found. (Example: Essentials)");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.setupPermissions();
        this.getCommand("shop").setExecutor(new Control());
        this.getServer().getPluginManager().registerEvents(new VersionChecker(), this);
        this.getServer().getPluginManager().registerEvents(new Sign(), this);
        this.getServer().getPluginManager().registerEvents(new Editor(), this);
        this.getServer().getPluginManager().registerEvents(new TradeEditor(), this);
        this.getServer().getPluginManager().registerEvents(new Shop(), this);
        this.getServer().getPluginManager().registerEvents(new PageProperties(), this);
        final boolean mainfolder = this.getDataFolder().mkdir();
        if (Debug.debug) {
            this.getLogger().info("Created main folder: " + mainfolder);
        }
        final File datafolder = new File(this.getDataFolder() + "/data");
        final boolean data = datafolder.mkdir();
        if (Debug.debug) {
            this.getLogger().info("Created data folder: " + data);
        }
        final File backupfolder = new File(this.getDataFolder() + "/backup");
        final boolean back = backupfolder.mkdir();
        if (Debug.debug) {
            this.getLogger().info("Created backup folder: " + back);
        }
        Update.runUpdate(1);
        Config.load();
        Update.runUpdate(2);
        (Initiate.sf = new ShopFile(this.getDataFolder().getPath())).loadCitizensData();
        Initiate.sf.loadWorthData();
        Initiate.sf.loadMiscData();
        final List<Page> pages = Initiate.sf.loadPages();
        for (final Page p : pages) {
            if (Debug.debug) {
                this.getLogger().info("Loaded page: " + p.getID());
            }
        }
        if (Config.UPDATE_CHECK.isActive()) {
            final String pluginversion = VersionChecker.check();
            if (VersionChecker.check() != null && !this.version.equals(pluginversion)) {
                this.getLogger().info(this.version + " Shop is outdated");
                this.getLogger().info(pluginversion + " Shop is available for download");
                this.getLogger().info("Go to http://spigotmc.org/resources/shop.8185/ to update");
            }
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PlaceholderAddon.register(this);
            Initiate.placeholderapi = true;
        } else {
            this.getLogger().info("PlaceholderAPI not found, alternative placeholders will be used.");
        }
        if (Bukkit.getPluginManager().getPlugin("Citizens") != null) {
            try {
                CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(NPCAddon.class).withName("shop"));
                Initiate.citizens = true;
                this.getLogger().info("Successfully hooked into Citizens.");
            } catch (NullPointerException npe) {
                this.getLogger().info("An error occured when trying to register a trait. Your Citizens version might not be supported.");
            } catch (NoClassDefFoundError ncd) {
                this.getLogger().info("An error occured when trying to register a trait. Your Citizens version might not be supported.");
            }
        } else {
            this.getLogger().info("Citizens not found. NPCs will not be available.");
        }
        Autosave.start();
        Autobackup.start();
    }

    public void onDisable() {
        Autosave.save();
    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        final RegisteredServiceProvider<Economy> rsp = (RegisteredServiceProvider<Economy>) this.getServer().getServicesManager().getRegistration((Class) Economy.class);
        if (rsp == null) {
            return false;
        }
        Initiate.econ = rsp.getProvider();
        return Initiate.econ != null;
    }

    private boolean setupPermissions() {
        final RegisteredServiceProvider<Permission> rsp = (RegisteredServiceProvider<Permission>) this.getServer().getServicesManager().getRegistration((Class) Permission.class);
        Initiate.perms = rsp.getProvider();
        return Initiate.perms != null;
    }

    public static Initiate getPlugin() {
        return plugin;
    }
}
