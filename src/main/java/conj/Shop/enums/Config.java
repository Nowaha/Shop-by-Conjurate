package conj.Shop.enums;

import conj.Shop.auto.Autobackup;
import conj.Shop.auto.Autosave;
import conj.Shop.base.Initiate;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public enum Config {
    MAIN_PAGE("MAIN_PAGE", 0, "Misc", ""),
    UPDATE_CHECK("UPDATE_CHECK", 1, "Misc", true),
    COMMAND_PLACEHOLD("COMMAND_PLACEHOLD", 2, "Misc", false),
    PURCHASE_GUI("PURCHASE_GUI", 3, "Purchase", ""),
    COST_PREFIX("COST_PREFIX", 4, "Purchase", "&aCost&7: &2"),
    SHOP_PURCHASE("Purchase", Initiate.shop_purchase),
    SELL_GUI("SELL_GUI", 6, "Sell", ""),
    SELL_PREFIX("SELL_PREFIX", 7, "Sell", "&bSell&7: &3"),
    SELL_COMPLETE("SELL_COMPLETE", 8, "Sell", "&aYou earned &2%worth%"),
    SHOP_SELL("Sell", Initiate.shop_sell),
    TRADE_GUI("TRADE_GUI", 10, "Trade", ""),
    BLACKLIST_ERROR("BLACKLIST_ERROR", 11, "Messages", "&cYou can't use the shop in this world"),
    PERMISSION_ERROR("PERMISSION_ERROR", 12, "Messages", "&cYou don't have permission to use this command"),
    COST_CANNOT_AFFORD("COST_CANNOT_AFFORD", 13, "Messages", "&cYou can't afford this item"),
    TRADE_NEED_SPACE("TRADE_NEED_SPACE", 14, "Messages", "&cYou need space in your inventory to complete this trade"),
    AUTOSAVE("AUTOSAVE", 15, "Auto", true),
    AUTOBACKUP("AUTOBACKUP", 16, "Auto", true),
    AUTOSAVE_DELAY("AUTOSAVE_DELAY", 17, "Auto", 20),
    SIGN_TAG("SIGN_TAG", 18, "Sign", "[Shop]"),
    SIGN_ENABLED("SIGN_ENABLED", 19, "Sign", true);

    private String base;
    private String message;
    private List<String> messages;
    private int numeral;
    private boolean active;

    Config(final String s, final int n, final String base, final String message) {
        this.base = base;
        this.message = message;
    }

    Config(final String base, final List<String> messages) {
        this.base = base;
        this.messages = messages;
    }

    Config(final String s, final int n, final String base, final boolean active) {
        this.base = base;
        this.active = active;
    }

    Config(final String s, final int n, final String base, final int numeral) {
        this.base = base;
        this.numeral = numeral;
    }

    public static void save() {
        final Plugin plugin = JavaPlugin.getPlugin((Class) Initiate.class);
        Config[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final Config c = values[i];
            plugin.getConfig().set(c.name(), c.getValue());
        }
        plugin.saveConfig();
    }

    public static void load() {
        final Plugin plugin = JavaPlugin.getPlugin((Class) Initiate.class);
        plugin.reloadConfig();
        Config[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final Config c = values[i];
            if (plugin.getConfig().get(c.base + "." + c.name()) != null) {
                final Object o = plugin.getConfig().get(c.base + "." + c.name());
                try {
                    if (o instanceof Boolean) {
                        final boolean ob = (boolean) o;
                        c.active = ob;
                    }
                    if (o instanceof Integer) {
                        final int ib = (int) o;
                        c.numeral = ib;
                    }
                    if (o instanceof String) {
                        final String sb = (String) o;
                        c.message = sb;
                    }
                    if (o instanceof List) {
                        final List<String> lb = (List<String>) o;
                        c.messages = lb;
                    }
                } catch (ClassCastException e) {
                    plugin.getLogger().info("Failed to load config value: " + c.base + "." + c.name());
                }
            } else {
                final Object old = plugin.getConfig().get(c.name());
                plugin.getConfig().set(c.base + "." + c.name(), (old == null) ? c.getValue() : old);
            }
        }
        plugin.saveConfig();
        Autosave.start();
        Autobackup.start();
    }

    public String getBase() {
        return this.base;
    }

    public List<String> getList() {
        return this.messages;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public Object getValue() {
        if (this.message != null) {
            return this.message;
        }
        if (this.messages != null) {
            return this.messages;
        }
        if (this.numeral > 0) {
            return this.numeral;
        }
        return this.active;
    }

    public int getNumeral() {
        return this.numeral;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setBoolean(final Boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        if (this.message != null) {
            return ChatColor.translateAlternateColorCodes('&', this.message);
        }
        if (this.messages != null) {
            String build = "";
            for (final String s : this.messages) {
                build = build + s;
            }
            return ChatColor.translateAlternateColorCodes('&', this.message);
        }
        if (this.numeral > 0) {
            return String.valueOf(this.numeral);
        }
        return this.active ? "True" : "False";
    }
}
