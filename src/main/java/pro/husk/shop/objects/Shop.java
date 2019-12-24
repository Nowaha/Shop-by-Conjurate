package pro.husk.shop.objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Shop {

    @Getter
    private static List<Shop> shopsList = new ArrayList<>();

    @Getter
    UUID uuid;

    @Getter
    String name;

    @Getter
    @Setter
    HashMap<Integer, ShopItem> shopInventory = new HashMap<>();

    /**
     * Default constructor, takes a name value only
     *
     * @param name of shop
     */
    public Shop(String name) {
        this.name = name;

        shopsList.add(this);
    }

    /**
     * Constructor with a UUID, used for MySQL stored shops
     *
     * @param uuid of shop
     * @param name of shop
     */
    public Shop(UUID uuid, String name) {
        this(name);
        this.uuid = uuid;
    }

    public static Shop getShopFromUUID(UUID uuid) {
        for (Shop shop : getShopsList()) {
            if (shop.getUuid().equals(uuid)) {
                return shop;
            }
        }
        return null;
    }

    public void addShopItem(ShopItem shopItem) {
        getShopInventory().put(shopItem.getSlot(), shopItem);
    }

    public void purchaseItem(Player player) {

    }
}
