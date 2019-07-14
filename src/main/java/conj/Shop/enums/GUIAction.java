package conj.Shop.enums;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

public enum GUIAction {
    NONE("NONE", 0, "The item will do nothing when clicked"),
    PURCHASE("PURCHASE", 1, "Buys item when clicked"),
    SELL("SELL", 2, "Sells item when clicked");

    private String description;

    GUIAction(final String s, final int n, final String description) {
        this.description = description;
    }

    public static GUIAction fromString(String string) {
        GUIAction action = GUIAction.NONE;
        string = string.replaceAll(" ", "_");
        string = string.toUpperCase();
        action = valueOf(string);
        return action;
    }

    public String getDescription() {
        return ChatColor.translateAlternateColorCodes('&', this.description);
    }

    @Override
    public String toString() {
        return WordUtils.capitalizeFully(this.name().toLowerCase().replaceAll("_", " "));
    }
}
