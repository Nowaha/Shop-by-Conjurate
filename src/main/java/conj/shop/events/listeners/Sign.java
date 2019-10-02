package conj.shop.events.listeners;

import conj.shop.commands.control.Manager;
import conj.shop.data.Page;
import conj.shop.data.enums.Config;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Sign implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void interactSign(final PlayerInteractEvent event) {
        final Block block = event.getClickedBlock();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && block != null && (block.getType().toString().contains("SIGN"))) {
            final org.bukkit.block.Sign sign = (org.bukkit.block.Sign) block.getState();
            final String line = sign.getLine(0);
            final String pagename = sign.getLine(1);
            if (line != null && pagename != null && ChatColor.stripColor(line).equalsIgnoreCase(ChatColor.stripColor(Config.SIGN_TAG.toString()))) {
                final Manager manage = new Manager();
                final Page page = manage.getPage(pagename);
                if (page != null) {
                    page.openPage(event.getPlayer());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void interactSign(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        if (block.getType().toString().contains("SIGN")) {
            final org.bukkit.block.Sign sign = (org.bukkit.block.Sign) block.getState();
            if (sign.getLine(0) != null && sign.getLine(0).equalsIgnoreCase(Config.SIGN_TAG.toString()) && !player.hasPermission("shop.sign.break")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void interactSign(final SignChangeEvent event) {
        if (event.getLine(0) != null) {
            final Player player = event.getPlayer();
            final String top = event.getLine(0);
            if (ChatColor.stripColor(top).equalsIgnoreCase(ChatColor.stripColor(Config.SIGN_TAG.toString())) && !player.hasPermission("shop.sign.create")) {
                event.setCancelled(true);
                return;
            }
            if (player.hasPermission("shop.sign.create") && top.equalsIgnoreCase("//shop//")) {
                final String page = event.getLine(1);
                if (page != null && new Manager().getPage(page) != null) {
                    event.setLine(0, Config.SIGN_TAG.toString());
                    event.setLine(1, new Manager().getPage(page).getID());
                }
            }
        }
    }
}
