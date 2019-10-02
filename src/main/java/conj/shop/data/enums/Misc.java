package conj.shop.data.enums;

import org.bukkit.ChatColor;

import java.util.List;

public enum Misc {
    UPDATE_2_X("UPDATE_2_X", 0, false);

    private String message;
    private List<String> messages;
    private boolean active;

    Misc(final String s, final int n, final String message) {
        this.message = message;
    }

    Misc(final List<String> messages) {
        this.messages = messages;
    }

    Misc(final String s, final int n, final boolean active) {
        this.active = active;
    }

    public List<String> getList() {
        return this.messages;
    }

    public Object getValue() {
        if (this.message != null) {
            return this.message;
        }
        if (this.messages != null) {
            return this.messages;
        }
        return this.active;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setMessage(final String message) {
        this.message = message;
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
        return this.active ? "True" : "False";
    }
}
