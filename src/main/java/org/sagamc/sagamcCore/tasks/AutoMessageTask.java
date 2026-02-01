package org.sagamc.sagamcCore.tasks;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class AutoMessageTask extends BukkitRunnable {

    private final FileConfiguration config;
    private int currentIndex = 0;

    public AutoMessageTask(FileConfiguration config) {
        this.config = config;
    }

    public void start(JavaPlugin plugin) {
        int interval = config.getInt("autoMessagesInterval", 300) * 20;
        this.runTaskTimer(plugin, interval, interval);
    }

    @Override
    public void run() {
        List<String> messages = config.getStringList("autoMessages");

        if (messages.isEmpty()) {
            return;
        }

        String message = messages.get(currentIndex);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(message));
        }

        currentIndex = (currentIndex + 1) % messages.size();
    }
}