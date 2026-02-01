package org.sagamc.sagamcCore.listeners;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    private final FileConfiguration config;
    private final FileConfiguration messages;

    public DeathListener(FileConfiguration config, FileConfiguration messages) {
        this.config = config;
        this.messages = messages;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (!config.getBoolean("deathMessagesEnabled", true)) {
            event.deathMessage(null);
            return;
        }

        Player victim = event.getPlayer();
        Player killer = victim.getKiller();

        String messageKey;
        String finalMsg;

        if (killer != null) {
            finalMsg = messages.getString("death.player-kill", "<red>☠ <white>{KILLER} <red>zabił <white>{VICTIM}")
                    .replace("{KILLER}", killer.getName())
                    .replace("{VICTIM}", victim.getName());
        }

        else {
            String deathCause = event.deathMessage() != null ? event.deathMessage().toString().toLowerCase() : "";

            if (deathCause.contains("fell") || deathCause.contains("ground")) {
                messageKey = "death.fall";
            } else if (deathCause.contains("drown")) {
                messageKey = "death.drown";
            } else if (deathCause.contains("fire") || deathCause.contains("burn")) {
                messageKey = "death.fire";
            } else if (deathCause.contains("lava")) {
                messageKey = "death.lava";
            } else if (deathCause.contains("explosion") || deathCause.contains("tnt")) {
                messageKey = "death.explosion";
            } else if (deathCause.contains("void")) {
                messageKey = "death.void";
            } else if (deathCause.contains("suffocate") || deathCause.contains("wall")) {
                messageKey = "death.suffocation";
            } else {
                messageKey = "death.generic";
            }

            finalMsg = messages.getString(messageKey, "<red>☠ <white>{PLAYER} <red>zginął")
                    .replace("{PLAYER}", victim.getName());
        }

        event.deathMessage(MiniMessage.miniMessage().deserialize(finalMsg));
    }
}