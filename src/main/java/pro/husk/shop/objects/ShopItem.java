package pro.husk.shop.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class ShopItem {

    @Getter
    @Setter
    String name;

    @Getter
    @Setter
    int amount;

    @Getter
    @Setter
    ItemStack itemStack;
}
