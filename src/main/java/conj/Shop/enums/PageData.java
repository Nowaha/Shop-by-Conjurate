package conj.Shop.enums;

import org.bukkit.ChatColor;

public enum PageData {
    NONE("NONE", 0, "Nothing"),
    SHOP("SHOP", 1, "Normal view page"),
    PURCHASE_ITEM("PURCHASE_ITEM", 2, "Purchase item"),
    TRADE_ITEM("TRADE_ITEM", 3, "Trade item"),
    SELL_ITEM("SELL_ITEM", 4, "Sell item"),
    EDIT_ITEM("EDIT_ITEM", 5, "Change item's display"),
    EDIT_ITEM_HIDEMODE("EDIT_ITEM_HIDEMODE", 6, "Change item's hidemode"),
    EDIT_ITEM_VIEW("EDIT_ITEM_VIEW", 7, "Page editor select item"),
    EDIT_ITEM_MOVE("EDIT_ITEM_MOVE", 8, "Change item's position"),
    EDIT_ITEM_MANAGE("EDIT_ITEM_MANAGE", 9, "Manage clicked item"),
    EDIT_ITEM_FUNCTION("EDIT_ITEM_FUNCTION", 10, "Change item's function"),
    EDIT_ITEM_MESSAGES("EDIT_ITEM_MESSAGES", 11, "Change item's messages"),
    EDIT_ITEM_INVENTORY("EDIT_ITEM_INVENTORY", 12, "Change item's inventory"),
    MOVE_ITEM("MOVE_ITEM", 13, "Move items"),
    PAGE_PROPERTIES("PAGE_PROPERTIES", 14, "Page properties"),
    PAGE_PROPERTIES_FILL_SLOTS("PAGE_PROPERTIES_FILL_SLOTS", 15, "Page properties - fill slots"),
    GUI("GUI", 16, "View and interact with GUI");

    private String info;

    private PageData(final String s, final int n, final String info) {
        this.info = info;
    }

    public String getInfo() {
        return ChatColor.translateAlternateColorCodes('&', this.info);
    }
}
