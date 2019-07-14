package conj.Shop.tools;

import conj.Shop.base.Initiate;
import conj.Shop.control.Manager;
import conj.Shop.data.Page;
import conj.Shop.events.PlayerInputEvent;
import conj.Shop.interaction.Editor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class Input implements Listener {
    private String page;
    private String id;
    private Player player;
    private int slot;
    private Plugin plugin;

    public Input(final Player player, final String page, final int slot, final String id) {
        this.player = player;
        this.page = page;
        this.id = id;
        this.slot = slot;
        this.plugin = (Plugin) Initiate.getPlugin((Class) Initiate.class);
    }

    public Player getPlayer() {
        return this.player;
    }

    public Page getPage() {
        return new Manager().getPage(this.page);
    }

    public String getID() {
        return this.id;
    }

    public int getSlot() {
        return this.slot;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void enterInput(final PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        if (this.player == null) {
            return;
        }
        if (this.player.getUniqueId().equals(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Input required");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void enterInput(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        if (this.player == null) {
            return;
        }
        if (this.player.getUniqueId().equals(player.getUniqueId())) {
            event.setCancelled(true);
            String fullmsg = event.getMessage();
            final String msg = ChatColor.stripColor(event.getMessage());
            if (msg.equalsIgnoreCase("-cancel")) {
                Editor.editItem(player, new Manager().getPage(this.page), this.slot);
                this.destroy();
                return;
            }
            if (msg.equalsIgnoreCase("&&")) {
                fullmsg = " ";
            }
            final Page page = this.getPage();
            if (page != null) {
                final PlayerInputEvent e = new PlayerInputEvent(player, page, this.id, fullmsg, this.slot, this);
                Bukkit.getServer().getPluginManager().callEvent((Event) e);
                if (e.isCancelled()) {
                    event.setCancelled(true);
                }
            } else {
                this.destroy();
            }
        }
    }

    public void register() {
        final PluginManager manager = this.plugin.getServer().getPluginManager();
        manager.registerEvents((Listener) this, this.plugin);
    }

    public void unregister() {
        AsyncPlayerChatEvent.getHandlerList().unregister((Listener) this);
        PluginDisableEvent.getHandlerList().unregister((Listener) this);
        PlayerCommandPreprocessEvent.getHandlerList().unregister((Listener) this);
        InventoryClickEvent.getHandlerList().unregister((Listener) this);
        InventoryCloseEvent.getHandlerList().unregister((Listener) this);
    }

    public void destroyData() {
        this.page = null;
        this.id = null;
        this.player = null;
        this.plugin = null;
    }

    public void destroy() {
        this.destroyData();
        this.unregister();
    }
}
