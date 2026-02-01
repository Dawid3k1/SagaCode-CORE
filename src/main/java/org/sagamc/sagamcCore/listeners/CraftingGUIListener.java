package org.sagamc.sagamcCore.listeners;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.sagamc.sagamcCore.util.ColorUtil;

public class CraftingGUIListener implements Listener {

    private final FileConfiguration messages;

    public CraftingGUIListener(FileConfiguration messages) {
        this.messages = messages;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();
        String kowalTitle = ColorUtil.miniToLegacy(messages.getString("kowal.title", "Kowal"));
        String ksiegarzTitle = ColorUtil.miniToLegacy(messages.getString("ksiegarz.title", "Księgarz"));

        if (!title.equals(kowalTitle) && !title.equals(ksiegarzTitle)) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        player.closeInventory();

        switch (clicked.getType()) {
            case CRAFTING_TABLE -> player.openWorkbench(null, true);
            case GRINDSTONE -> player.openGrindstone(null, true);
            case ANVIL -> player.openAnvil(null, true);
            case ENCHANTING_TABLE -> player.openEnchanting(null, true);
            default -> {}
        }
    }
}