package org.sagamc.sagamcCore.stats;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class StatsListener implements Listener {

    private final StatsManager statsManager;

    public StatsListener(StatsManager statsManager) {
        this.statsManager = statsManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        statsManager.onPlayerJoin(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        statsManager.onPlayerQuit(event.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getPlayer();
        Player killer = victim.getKiller();

        statsManager.onPlayerDeath(victim);

        if (killer != null) {
            statsManager.onPlayerKill(killer);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        statsManager.onBlockBreak(event.getPlayer());
    }
}