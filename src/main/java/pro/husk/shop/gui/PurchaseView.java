package pro.husk.shop.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class PurchaseView extends BaseView {

    /**
     * Default constructor
     *
     * @param player that opened the gui
     */
    public PurchaseView(Player player) {
        super(player);
    }

    /**
     * 
     * @return
     */
    @Override
    public Inventory getInventory() {
        return null;
    }
}
