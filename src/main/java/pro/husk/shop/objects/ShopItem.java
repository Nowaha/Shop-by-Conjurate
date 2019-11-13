package pro.husk.shop.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class ShopItem {
    
    @Getter
    @Setter
    ItemStack itemStack;

    @Getter
    @Setter
    int slot;

    @Getter
    @Setter
    int buyAmount;

    @Getter
    @Setter
    int sellAmount;

    @Getter
    @Setter
    ShopFunction function;
}
