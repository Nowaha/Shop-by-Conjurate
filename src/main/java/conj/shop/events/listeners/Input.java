package conj.shop.events.listeners;

import conj.shop.base.Initiate;
import conj.shop.commands.control.Manager;
import conj.shop.data.Page;
import conj.shop.events.custom.PlayerInputEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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
        this.plugin = Initiate.getPlugin();
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
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> Bukkit.getServer().getPluginManager().callEvent(e), 0);
                event.setCancelled(true);
            } else {
                this.destroy();
            }
        }
    }

    public void register() {
        final PluginManager manager = this.plugin.getServer().getPluginManager();
        manager.registerEvents(this, this.plugin);
    }

    public void unregister() {
        AsyncPlayerChatEvent.getHandlerList().unregister(this);
        PluginDisableEvent.getHandlerList().unregister(this);
        PlayerCommandPreprocessEvent.getHandlerList().unregister(this);
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
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
