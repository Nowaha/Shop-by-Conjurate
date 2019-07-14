package conj.Shop.events;

import org.bukkit.event.*;
import conj.Shop.data.*;
import conj.Shop.tools.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import conj.Shop.enums.*;

public class PageOpenEvent extends Event implements Cancellable {
    private static final HandlerList handlers;
    private Page page;
    private GUI gui;
    private Player player;
    private Inventory inventory;
    private int slot;
    private boolean cancelled;
    private PageData pagedata;

    static {
        handlers = new HandlerList();
    }

    public PageOpenEvent(final Player player, final PageData pagedata, final GUI gui, final Page page, final int slot, final Inventory inventory) {
        this.page = page;
        this.slot = slot;
        this.player = player;
        this.gui = gui;
        this.pagedata = pagedata;
        this.inventory = inventory;
    }

    public Page getPage() {
        return this.page;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public GUI getGUI() {
        return this.gui;
    }

    public PageData getPageData() {
        return this.pagedata;
    }

    public int getSlot() {
        return this.slot;
    }

    public Player getPlayer() {
        return this.player;
    }

    public HandlerList getHandlers() {
        return PageOpenEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return PageOpenEvent.handlers;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
}
