package org.sagamc.sagamcCore.stats;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.sagamc.sagamcCore.util.ColorUtil;

public class StatsGUIListener implements Listener {

    private final FileConfiguration messages;

    public StatsGUIListener(FileConfiguration messages) {
        this.messages = messages;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        String title = event.getView().getTitle();
        String expectedTitle = ColorUtil.miniToLegacy(messages.getString("stats.gui-title", "Statystyki"));

        if (title.equals(expectedTitle)) {
            event.setCancelled(true);
        }
    }
}