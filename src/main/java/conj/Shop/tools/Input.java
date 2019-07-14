package conj.Shop.tools;

import org.bukkit.entity.*;
import conj.Shop.base.*;
import conj.Shop.data.*;
import conj.Shop.control.*;
import org.bukkit.event.player.*;
import conj.Shop.interaction.*;
import conj.Shop.events.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.plugin.*;
import org.bukkit.event.server.*;
import org.bukkit.event.inventory.*;

public class Input implements Listener
{
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
        this.plugin = (Plugin)Initiate.getPlugin((Class)Initiate.class);
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
                Bukkit.getScheduler().runTask(plugin, () -> {
                    Editor.editItem(player, new Manager().getPage(this.page), this.slot);
                    this.destroy();
                });
                return;
            }
            if (msg.equalsIgnoreCase("&&")) {
                fullmsg = " ";
            }
            final Page page = this.getPage();
            if (page != null) {
                final PlayerInputEvent e = new PlayerInputEvent(player, page, this.id, fullmsg, this.slot, this);
                Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getServer().getPluginManager().callEvent(e));
            }
            else {
                this.destroy();
            }
        }
    }
    
    public void register() {
        final PluginManager manager = this.plugin.getServer().getPluginManager();
        manager.registerEvents((Listener)this, this.plugin);
    }
    
    public void unregister() {
        AsyncPlayerChatEvent.getHandlerList().unregister((Listener)this);
        PluginDisableEvent.getHandlerList().unregister((Listener)this);
        PlayerCommandPreprocessEvent.getHandlerList().unregister((Listener)this);
        InventoryClickEvent.getHandlerList().unregister((Listener)this);
        InventoryCloseEvent.getHandlerList().unregister((Listener)this);
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
