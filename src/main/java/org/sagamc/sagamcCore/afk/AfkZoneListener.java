package org.sagamc.sagamcCore.afk;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AfkZoneListener implements Listener {

    private final AfkZoneManager afkZoneManager;

    public AfkZoneListener(AfkZoneManager afkZoneManager) {
        this.afkZoneManager = afkZoneManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!afkZoneManager.isEnabled()) return;

        Player player = event.getPlayer();
        boolean isInZone = afkZoneManager.isInAfkZone(player);
        boolean isTracking = afkZoneManager.isTracking(player);

        // Wszedł do strefy
        if (isInZone && !isTracking) {
            afkZoneManager.addPlayer(player);
        }
        // Wyszedł ze strefy
        else if (!isInZone && isTracking) {
            afkZoneManager.removePlayer(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (afkZoneManager.isTracking(player)) {
            afkZoneManager.removePlayer(player);
        }
    }
}