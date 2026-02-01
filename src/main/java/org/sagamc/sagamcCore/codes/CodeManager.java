package org.sagamc.sagamcCore.codes;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CodeManager {
    private final JavaPlugin plugin;
    private final Map<String, Code> codes = new HashMap<>();
    private File usedCodesFile;
    private FileConfiguration usedCodesConfig;

    public CodeManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadCodes();
        loadUsedCodes();
    }

    private void loadCodes() {
        codes.clear();
        FileConfiguration config = plugin.getConfig();

        List<Map<?, ?>> codesList = config.getMapList("codes");
        for (Map<?, ?> codeMap : codesList) {
            String name = (String) codeMap.get("name");
            String requiredTime = (String) codeMap.get("requiredTime");
            List<String> commands = (List<String>) codeMap.get("commands");
            List<String> broadcast = (List<String>) codeMap.get("broadcast");

            long requiredSeconds = Code.parseTime(requiredTime);
            codes.put(name.toLowerCase(), new Code(name, requiredSeconds, commands, broadcast));
        }
    }

    private void loadUsedCodes() {
        usedCodesFile = new File(plugin.getDataFolder(), "used_codes.yml");
        if (!usedCodesFile.exists()) {
            try {
                usedCodesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        usedCodesConfig = YamlConfiguration.loadConfiguration(usedCodesFile);
    }

    public void reload() {
        loadCodes();
        loadUsedCodes();
    }

    public Code getCode(String name) {
        return codes.get(name.toLowerCase());
    }

    public boolean hasUsedCode(UUID playerUUID, String codeName) {
        return usedCodesConfig.getBoolean(playerUUID.toString() + "." + codeName.toLowerCase(), false);
    }

    public void markCodeAsUsed(UUID playerUUID, String codeName) {
        usedCodesConfig.set(playerUUID.toString() + "." + codeName.toLowerCase(), true);
        try {
            usedCodesConfig.save(usedCodesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasRequiredPlaytime(Player player, long requiredSeconds) {
        int playTimeTicks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        long playTimeSeconds = playTimeTicks / 20L;
        return playTimeSeconds >= requiredSeconds;
    }

    public long getRemainingTime(Player player, long requiredSeconds) {
        int playTimeTicks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        long playTimeSeconds = playTimeTicks / 20L;
        return Math.max(0, requiredSeconds - playTimeSeconds);
    }

    public void executeCode(Player player, Code code) {
        for (String command : code.getCommands()) {
            String cmd = command.replace("<player>", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }

        if (code.getBroadcast() != null && !code.getBroadcast().isEmpty()) {
            for (String line : code.getBroadcast()) {
                String message = line.replace("<player>", player.getName());
                Bukkit.broadcast(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(message));
            }
        }

        markCodeAsUsed(player.getUniqueId(), code.getName());
    }
}