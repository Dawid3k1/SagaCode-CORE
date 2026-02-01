package org.sagamc.sagamcCore.home;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HomeManager {

    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final Map<UUID, List<Home>> homes = new HashMap<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private File homesFile;
    private FileConfiguration homesData;

    public HomeManager(JavaPlugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
        loadHomes();
    }

    private void loadHomes() {
        homesFile = new File(plugin.getDataFolder(), "homes.yml");
        if (!homesFile.exists()) {
            try {
                homesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        homesData = YamlConfiguration.loadConfiguration(homesFile);

        for (String uuidString : homesData.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidString);
            List<Home> playerHomes = new ArrayList<>();

            ConfigurationSection section = homesData.getConfigurationSection(uuidString);
            if (section != null) {
                for (String homeName : section.getKeys(false)) {
                    ConfigurationSection homeSection = section.getConfigurationSection(homeName);
                    if (homeSection != null) {
                        String world = homeSection.getString("world");
                        double x = homeSection.getDouble("x");
                        double y = homeSection.getDouble("y");
                        double z = homeSection.getDouble("z");
                        float yaw = (float) homeSection.getDouble("yaw");
                        float pitch = (float) homeSection.getDouble("pitch");

                        Location loc = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
                        playerHomes.add(new Home(homeName, loc, uuid));
                    }
                }
            }
            homes.put(uuid, playerHomes);
        }
    }

    public void saveHomes() {
        homesData = new YamlConfiguration();

        for (Map.Entry<UUID, List<Home>> entry : homes.entrySet()) {
            String uuidString = entry.getKey().toString();
            for (Home home : entry.getValue()) {
                Location loc = home.getLocation();
                homesData.set(uuidString + "." + home.getName() + ".world", loc.getWorld().getName());
                homesData.set(uuidString + "." + home.getName() + ".x", loc.getX());
                homesData.set(uuidString + "." + home.getName() + ".y", loc.getY());
                homesData.set(uuidString + "." + home.getName() + ".z", loc.getZ());
                homesData.set(uuidString + "." + home.getName() + ".yaw", loc.getYaw());
                homesData.set(uuidString + "." + home.getName() + ".pitch", loc.getPitch());
            }
        }

        try {
            homesData.save(homesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean setHome(Player player, String name) {
        UUID uuid = player.getUniqueId();
        int limit = player.hasPermission("sagamc.home.vip") ?
                config.getInt("homeSystem.vipLimit", 10) :
                config.getInt("homeSystem.defaultLimit", 3);

        List<Home> playerHomes = homes.getOrDefault(uuid, new ArrayList<>());

        for (Home home : playerHomes) {
            if (home.getName().equalsIgnoreCase(name)) {
                return false;
            }
        }

        if (playerHomes.size() >= limit) {
            return false;
        }

        playerHomes.add(new Home(name, player.getLocation(), uuid));
        homes.put(uuid, playerHomes);
        saveHomes();
        return true;
    }

    public Home getHome(Player player, String name) {
        UUID uuid = player.getUniqueId();
        List<Home> playerHomes = homes.getOrDefault(uuid, new ArrayList<>());

        for (Home home : playerHomes) {
            if (home.getName().equalsIgnoreCase(name)) {
                return home;
            }
        }
        return null;
    }

    public boolean deleteHome(Player player, String name) {
        UUID uuid = player.getUniqueId();
        List<Home> playerHomes = homes.getOrDefault(uuid, new ArrayList<>());

        boolean removed = playerHomes.removeIf(home -> home.getName().equalsIgnoreCase(name));
        if (removed) {
            saveHomes();
        }
        return removed;
    }

    public List<Home> getHomes(Player player) {
        return homes.getOrDefault(player.getUniqueId(), new ArrayList<>());
    }

    public int getLimit(Player player) {
        return player.hasPermission("sagamc.home.vip") ?
                config.getInt("homeSystem.vipLimit", 10) :
                config.getInt("homeSystem.defaultLimit", 3);
    }

    public boolean isOnCooldown(Player player) {
        UUID uuid = player.getUniqueId();
        if (!cooldowns.containsKey(uuid)) return false;

        long timeLeft = (cooldowns.get(uuid) + (config.getInt("homeSystem.cooldownSeconds", 300) * 1000)) - System.currentTimeMillis();
        return timeLeft > 0;
    }

    public int getCooldownRemaining(Player player) {
        UUID uuid = player.getUniqueId();
        if (!cooldowns.containsKey(uuid)) return 0;

        long timeLeft = (cooldowns.get(uuid) + (config.getInt("homeSystem.cooldownSeconds", 300) * 1000)) - System.currentTimeMillis();
        return (int) (timeLeft / 1000);
    }

    public void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }
}
