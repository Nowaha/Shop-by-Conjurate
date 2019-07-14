package conj.Shop.enums;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

public enum Hidemode {
    VISIBLE("VISIBLE", 0, "Visible to all"),
    HIDDEN("HIDDEN", 1, "Visible to nobody"),
    PERMISSION("PERMISSION", 2, "Visible to players with permission"),
    OP("OP", 3, "Visible to players with OP");

    private String description;

    Hidemode(final String s, final int n, final String description) {
        this.description = description;
    }

    public static Hidemode fromString(String string) {
        Hidemode hidemode = Hidemode.VISIBLE;
        string = string.replaceAll(" ", "_");
        string = string.toUpperCase();
        hidemode = valueOf(string);
        return hidemode;
    }

    public String getDescription() {
        return ChatColor.translateAlternateColorCodes('&', this.description);
    }

    @Override
    public String toString() {
        return WordUtils.capitalizeFully(this.name().toLowerCase().replaceAll("_", " "));
    }
}
