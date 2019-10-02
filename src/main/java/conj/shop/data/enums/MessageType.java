package conj.shop.data.enums;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public enum MessageType {
    COOLDOWN("COOLDOWN", 0, "&cYou're currently on cooldown."),
    PERMISSION("PERMISSION", 1, "&cYou don't have enough permission to access this item.");

    private String def;

    MessageType(final String s, final int n, final String def) {
        this.def = def;
    }

    public static MessageType fromString(String string) {
        MessageType type = MessageType.COOLDOWN;
        string = string.replaceAll(" ", "_");
        string = string.toUpperCase();
        type = valueOf(string);
        return type;
    }

    public List<String> getDefault() {
        final List<String> list = new ArrayList<String>();
        list.add(ChatColor.translateAlternateColorCodes('&', this.def));
        return list;
    }
}
