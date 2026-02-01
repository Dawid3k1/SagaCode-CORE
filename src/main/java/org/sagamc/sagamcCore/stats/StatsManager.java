package org.sagamc.sagamcCore.stats;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class StatsManager {

    private final JavaPlugin plugin;
    private final Map<UUID, PlayerStats> stats = new HashMap<>();
    private final Map<UUID, Long> joinTimes = new HashMap<>();
    private File statsFile;
    private FileConfiguration statsData;

    public StatsManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadStats();
    }

    private void loadStats() {
        statsFile = new File(plugin.getDataFolder(), "stats.yml");
        if (!statsFile.exists()) {
            try {
                statsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        statsData = YamlConfiguration.loadConfiguration(statsFile);

        for (String uuidString : statsData.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidString);
            ConfigurationSection section = statsData.getConfigurationSection(uuidString);

            if (section != null) {
                long playTime = section.getLong("playtime", 0);
                int kills = section.getInt("kills", 0);
                int deaths = section.getInt("deaths", 0);
                int blocksBroken = section.getInt("blocks-broken", 0);

                stats.put(uuid, new PlayerStats(uuid, playTime, kills, deaths, blocksBroken));
            }
        }
    }

    public void saveStats() {
        statsData = new YamlConfiguration();

        for (PlayerStats stat : stats.values()) {
            String uuidString = stat.getUuid().toString();
            statsData.set(uuidString + ".playtime", stat.getPlayTime());
            statsData.set(uuidString + ".kills", stat.getKills());
            statsData.set(uuidString + ".deaths", stat.getDeaths());
            statsData.set(uuidString + ".blocks-broken", stat.getBlocksBroken());
        }

        try {
            statsData.save(statsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PlayerStats getStats(UUID uuid) {
        return stats.computeIfAbsent(uuid, PlayerStats::new);
    }

    public void onPlayerJoin(Player player) {
        joinTimes.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public void onPlayerQuit(Player player) {
        UUID uuid = player.getUniqueId();
        if (joinTimes.containsKey(uuid)) {
            long joinTime = joinTimes.get(uuid);
            long playedSeconds = (System.currentTimeMillis() - joinTime) / 1000;

            PlayerStats stats = getStats(uuid);
            stats.addPlayTime(playedSeconds);

            joinTimes.remove(uuid);
            saveStats();
        }
    }

    public void onPlayerKill(Player killer) {
        PlayerStats stats = getStats(killer.getUniqueId());
        stats.addKill();
        saveStats();
    }

    public void onPlayerDeath(Player victim) {
        PlayerStats stats = getStats(victim.getUniqueId());
        stats.addDeath();
        saveStats();
    }

    public void onBlockBreak(Player player) {
        PlayerStats stats = getStats(player.getUniqueId());
        stats.addBlockBroken();
    }
}