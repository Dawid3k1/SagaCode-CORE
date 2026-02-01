package org.sagamc.sagamcCore.teleport;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class TeleportListener implements Listener {

    private final TeleportManager teleportManager;

    public TeleportListener(TeleportManager teleportManager) {
        this.teleportManager = teleportManager;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        teleportManager.cancelTeleport(player);
    }
}