package conj.Shop.tools;

import conj.Shop.control.Manager;
import conj.Shop.data.Page;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;

public class NPCAddon extends Trait {
    public NPCAddon() {
        super("shop");
    }

    public static void setCitizenPage(final int id, final String page) {
        if (page == null) {
            Manager.cnpcs.remove(id);
            return;
        }
        Manager.cnpcs.put(id, page);
    }

    @EventHandler
    public void click(final NPCRightClickEvent event) {
        if (event.getNPC().hasTrait(NPCAddon.class)) {
            final Player player = event.getClicker();
            final Manager manager = new Manager();
            final Page page = manager.getPage(event.getNPC());
            final List<String> perms = manager.getCitizenPermissions(event.getNPC().getId());
            if (!perms.isEmpty()) {
                for (final String s : perms) {
                    if (!player.hasPermission(s)) {
                        return;
                    }
                }
            }
            if (page != null) {
                page.openPage(player);
            }
        }
    }

    @EventHandler
    public void click(final NPCRemoveEvent event) {
        Manager.get().setCitizenPage(event.getNPC().getId(), null);
    }

    public void onAttach() {
    }

    public void onDespawn() {
    }

    public void onSpawn() {
    }

    public void onRemove() {
    }
}
