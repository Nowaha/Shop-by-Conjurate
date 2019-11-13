package pro.husk.shop.utility;

import pro.husk.shop.ShopPlugin;
import pro.husk.shop.objects.Shop;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MySQLHelper {

    public static void loadShops() {
        CompletableFuture<ResultSet> completableFuture = ShopPlugin.getMysql().queryAsync("SELECT * FROM `shop_registry`;");

        completableFuture.thenRunAsync(() -> {
            ResultSet results = null;

            try {
                results = completableFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            try {
                if (results != null & results.next()) {
                    // Sort data
                    String uuidString = results.getString("shop_uuid");
                    String shopName = results.getString("shop_name");

                    UUID uuid = UUID.fromString(uuidString);

                    Shop shop = new Shop(uuid, shopName);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void loadShopData() {
        CompletableFuture<ResultSet> completableFuture = ShopPlugin.getMysql().queryAsync("SELECT * FROM `shop_data`;");

        completableFuture.thenRunAsync(() -> {

            ResultSet results = null;

            try {
                results = completableFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            try {
                if (results != null & results.next()) {
                    // Sort data

                    String uuidString = results.getString("shop_id");
                    UUID uuid = UUID.fromString(uuidString);
                    int slot = results.getInt("slot");
                    String itemStack = results.getString("itemstack");
                    int buyAmount = results.getInt("buy_amount");
                    int sellAmount = results.getInt("sell_amount");
                    String function = results.getString("function");

                    Shop shop = Shop.getShopFromUUID(uuid);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        });
    }


}
