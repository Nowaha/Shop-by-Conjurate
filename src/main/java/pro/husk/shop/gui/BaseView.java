package pro.husk.shop.gui;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.UUID;

public abstract class BaseView {

    @Getter
    private static HashMap<UUID, BaseView> viewerMap = new HashMap<>();

    /**
     * Default constructor
     *
     * @param player that opened the gui
     */
    public BaseView(Player player) {
        viewerMap.put(player.getUniqueId(), this);
    }

    /**
     * @param player
     * @return
     */
    public static boolean isViewer(Player player) {
        return viewerMap.containsKey(player.getUniqueId());
    }

    public abstract Inventory getInventory();
}
