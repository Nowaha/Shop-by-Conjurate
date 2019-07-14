package conj.Shop.enums;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

public enum GUIFunction {
    NONE("NONE", 0, "The item will do nothing when clicked"),
    QUANTITY("QUANTITY", 1, "The item will allow you to increase/decrease the quantity"),
    CONFIRM("CONFIRM", 2, "The item will act as a confirm button"),
    BACK("BACK", 3, "The item will take you back");

    private String description;

    GUIFunction(final String s, final int n, final String description) {
        this.description = description;
    }

    public static GUIFunction fromString(String string) {
        GUIFunction function = GUIFunction.NONE;
        string = string.replaceAll(" ", "_");
        string = string.toUpperCase();
        function = valueOf(string);
        return function;
    }

    public String getDescription() {
        return ChatColor.translateAlternateColorCodes('&', this.description);
    }

    @Override
    public String toString() {
        return WordUtils.capitalizeFully(this.name().toLowerCase().replaceAll("_", " "));
    }
}
