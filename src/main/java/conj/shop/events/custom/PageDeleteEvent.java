package conj.shop.events.custom;

import conj.shop.data.Page;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PageDeleteEvent extends Event implements Cancellable {
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    private Page page;
    private boolean cancelled;

    public PageDeleteEvent(final Page page) {
        this.page = page;
    }

    public static HandlerList getHandlerList() {
        return PageDeleteEvent.handlers;
    }

    public Page getPage() {
        return this.page;
    }

    public HandlerList getHandlers() {
        return PageDeleteEvent.handlers;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
}
