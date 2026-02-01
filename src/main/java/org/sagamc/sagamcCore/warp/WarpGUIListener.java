package org.sagamc.sagamcCore.warp;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.sagamc.sagamcCore.teleport.TeleportManager;
import org.sagamc.sagamcCore.util.ColorUtil;

public class WarpGUIListener implements Listener {

    private final WarpManager warpManager;
    private final TeleportManager teleportManager;
    private final FileConfiguration messages;

    public WarpGUIListener(WarpManager warpManager, TeleportManager teleportManager, FileConfiguration messages) {
        this.warpManager = warpManager;
        this.teleportManager = teleportManager;
        this.messages = messages;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();
        String expectedTitle = ColorUtil.miniToLegacy(messages.getString("warp.gui-title", "Warpy"));

        if (!title.equals(expectedTitle)) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        String itemName = clicked.getItemMeta().getDisplayName();
        if (itemName == null || itemName.isBlank()) return;

        String warpName = ColorUtil.stripColor(itemName).toLowerCase();

        Warp warp = warpManager.getWarp(warpName);
        if (warp == null) return;

        player.closeInventory();

        String successMsg = messages.getString("warp.teleport-success", "<green>Teleportowano do warpu <dark_green>{NAME}<green>!")
                .replace("{NAME}", warp.getName());

        teleportManager.teleportWithCountdown(player, warp.getLocation(), ColorUtil.miniToLegacy(successMsg));
    }
}