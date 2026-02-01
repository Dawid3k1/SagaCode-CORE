package org.sagamc.sagamcCore.listeners;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.sagamc.sagamcCore.vanish.VanishManager;

public class JoinQuitListener implements Listener {

    private final FileConfiguration config;
    private final FileConfiguration messages;
    private final VanishManager vanishManager;

    public JoinQuitListener(FileConfiguration config, FileConfiguration messages, VanishManager vanishManager) {
        this.config = config;
        this.messages = messages;
        this.vanishManager = vanishManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        for (Player online : Bukkit.getOnlinePlayers()) {
            vanishManager.hideFromPlayer(player, online);
        }

        if (config.getBoolean("joinQuitMessagesEnabled", true)) {
            String msg = messages.getString("join-quit.join", "<green>[+] <white>{PLAYER} <green>dołączył do gry!")
                    .replace("{PLAYER}", player.getName());
            event.joinMessage(MiniMessage.miniMessage().deserialize(msg));
        } else {
            event.joinMessage(null);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        vanishManager.removePlayer(player);
        if (config.getBoolean("joinQuitMessagesEnabled", true)) {
            String msg = messages.getString("join-quit.quit", "<red>[-] <white>{PLAYER} <red>opuścił grę!")
                    .replace("{PLAYER}", player.getName());
            event.quitMessage(MiniMessage.miniMessage().deserialize(msg));
        } else {
            event.quitMessage(null);
        }
    }
}