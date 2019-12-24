package pro.husk.shop.utility;

import org.bukkit.inventory.ItemStack;
import pro.husk.shop.ShopPlugin;
import pro.husk.shop.objects.Shop;
import pro.husk.shop.objects.ShopFunction;
import pro.husk.shop.objects.ShopItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySQLHelper {

    /**
     * Method used to load all the shop data into objects
     */
    public static void loadShops() {
        loadAllShopRegistry();
        loadAllShopData();
    }

    /**
     * Method to load the shop registry, creates the shop objects required to then load the data
     */
    private static void loadAllShopRegistry() {
        CompletableFuture<ResultSet> completableFuture = ShopPlugin.getMysql().queryAsync("SELECT * FROM `shop_registry`;");

        completableFuture.thenAccept(results -> {
            try {
                while (results != null & results.next()) {
                    String uuidString = results.getString("shop_id");
                    String shopName = results.getString("shop_name");

                    UUID uuid = UUID.fromString(uuidString);

                    Shop shop = new Shop(uuid, shopName);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Method to load the shop data, adds the data to the respective shop object
     */
    private static void loadAllShopData() {
        CompletableFuture<ResultSet> completableFuture = ShopPlugin.getMysql().queryAsync("SELECT * FROM `shop_data`;");

        completableFuture.thenAccept(results -> {
            try {

                if (results == null) return;

                while (results.next()) {
                    String uuidString = results.getString("shop_id");
                    int slot = results.getInt("slot");
                    UUID uuid = UUID.fromString(uuidString);
                    String itemStackString = results.getString("itemstack");
                    int buyAmount = results.getInt("buy_amount");
                    int sellAmount = results.getInt("sell_amount");
                    String functionString = results.getString("function");
                    ShopFunction function = ShopFunction.valueOf(functionString);

                    Shop shop = Shop.getShopFromUUID(uuid);

                    if (shop != null) {
                        ItemStack itemStack = ItemUtil.deserialise(itemStackString);

                        ShopItem shopItem = new ShopItem(itemStack, slot, buyAmount, sellAmount, function);

                        shop.addShopItem(shopItem);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Method to save shop to database. This shouldn't be called unless creating a shop from command
     *
     * @param shop object to save to MySQL
     */
    public void saveShopData(Shop shop) {
        CompletableFuture.runAsync(() -> {
            String uuidString = shop.getUuid().toString();
            String name = shop.getName();

            String updateRegistry = "INSERT INTO `shop_registry` (`shop_id`, `shop_name`) VALUES" +
                    " ('" + uuidString + "', '" + name + "');";

            ShopPlugin.getMysql().updateAsync(updateRegistry);

            // Loop through all the items in the inventory
            for (int slot : shop.getShopInventory().keySet()) {
                ShopItem shopItem = shop.getShopInventory().get(slot);

                String itemStackString = ItemUtil.serialise(shopItem.getItemStack());
                int buyAmount = shopItem.getBuyAmount();
                int sellAmount = shopItem.getSellAmount();
                String shopFunctionString = shopItem.getFunction().toString();

                String update = "INSERT INTO `shop_data` " +
                        "(`shop_id`, `slot`, `itemstack`, `buy_amount`, `sell_amount`, `function`) VALUES" +
                        " ('" + uuidString + "', '" + slot + "', '" + itemStackString + "', '" + buyAmount + "', " +
                        "'" + sellAmount + "', '" + shopFunctionString + "');";

                ShopPlugin.getMysql().updateAsync(update);
            }
        });
    }
}
