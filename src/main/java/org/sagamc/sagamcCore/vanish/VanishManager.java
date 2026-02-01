package org.sagamc.sagamcCore.vanish;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanishManager {

    private final JavaPlugin plugin;
    private final Set<UUID> vanishedPlayers = new HashSet<>();

    public VanishManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isVanished(Player player) {
        return vanishedPlayers.contains(player.getUniqueId());
    }

    public void setVanished(Player player, boolean vanished) {
        if (vanished) {
            vanishedPlayers.add(player.getUniqueId());
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.hasPermission("sagamc.vanish.see")) {
                    online.hidePlayer(plugin, player);
                }
            }
        } else {
            vanishedPlayers.remove(player.getUniqueId());
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.showPlayer(plugin, player);
            }
        }
    }

    public void hideFromPlayer(Player viewer, Player target) {
        if (isVanished(target) && !viewer.hasPermission("sagamc.vanish.see")) {
            viewer.hidePlayer(plugin, target);
        }
    }

    public void removePlayer(Player player) {
        vanishedPlayers.remove(player.getUniqueId());
    }
}