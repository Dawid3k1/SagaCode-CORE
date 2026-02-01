package org.sagamc.sagamcCore.chat;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatManager {

    private boolean chatEnabled;
    private int cooldownSeconds;
    private String bypassPermission;
    private final Map<UUID, Long> lastMessageTime = new HashMap<>();

    public ChatManager(FileConfiguration config) {
        loadConfig(config);
    }

    public void loadConfig(FileConfiguration config) {
        this.chatEnabled = config.getBoolean("chat.enabled", true);
        this.cooldownSeconds = config.getInt("chat.cooldown-seconds", 5);
        this.bypassPermission = config.getString("chat.bypass-permission", "sagamc.chat.bypass");
    }

    public boolean isChatEnabled() {
        return chatEnabled;
    }

    public void setChatEnabled(boolean enabled) {
        this.chatEnabled = enabled;
    }

    public int getCooldownSeconds() {
        return cooldownSeconds;
    }

    public void setCooldownSeconds(int seconds) {
        this.cooldownSeconds = seconds;
    }

    public boolean canSendMessage(Player player) {
        if (player.hasPermission(bypassPermission)) {
            return true;
        }

        UUID uuid = player.getUniqueId();
        if (!lastMessageTime.containsKey(uuid)) {
            return true;
        }

        long lastTime = lastMessageTime.get(uuid);
        long currentTime = System.currentTimeMillis();
        long timePassed = (currentTime - lastTime) / 1000;

        return timePassed >= cooldownSeconds;
    }

    public int getRemainingCooldown(Player player) {
        UUID uuid = player.getUniqueId();
        if (!lastMessageTime.containsKey(uuid)) {
            return 0;
        }

        long lastTime = lastMessageTime.get(uuid);
        long currentTime = System.currentTimeMillis();
        long timePassed = (currentTime - lastTime) / 1000;

        return Math.max(0, cooldownSeconds - (int) timePassed);
    }

    public void updateLastMessageTime(Player player) {
        lastMessageTime.put(player.getUniqueId(), System.currentTimeMillis());
    }
}