package org.sagamc.sagamcCore.warp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WarpManager {

    private final JavaPlugin plugin;
    private final Map<String, Warp> warps = new HashMap<>();
    private File warpsFile;
    private FileConfiguration warpsData;

    public WarpManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadWarps();
    }

    private void loadWarps() {
        warpsFile = new File(plugin.getDataFolder(), "warps.yml");
        if (!warpsFile.exists()) {
            try {
                warpsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        warpsData = YamlConfiguration.loadConfiguration(warpsFile);

        for (String name : warpsData.getKeys(false)) {
            ConfigurationSection section = warpsData.getConfigurationSection(name);
            if (section != null) {
                String worldName = section.getString("world");
                World world = Bukkit.getWorld(worldName);

                if (world == null) {
                    plugin.getLogger().warning("Nie można załadować warpu " + name + " - świat " + worldName + " nie istnieje!");
                    continue;
                }

                double x = section.getDouble("x");
                double y = section.getDouble("y");
                double z = section.getDouble("z");
                float yaw = (float) section.getDouble("yaw");
                float pitch = (float) section.getDouble("pitch");
                String iconMaterial = section.getString("icon", "ENDER_PEARL");

                Location loc = new Location(world, x, y, z, yaw, pitch);

                Material icon;
                try {
                    icon = Material.valueOf(iconMaterial);
                } catch (IllegalArgumentException e) {
                    icon = Material.ENDER_PEARL;
                    plugin.getLogger().warning("Nieprawidłowy materiał ikony dla warpu " + name + ", używam ENDER_PEARL");
                }

                warps.put(name.toLowerCase(), new Warp(name, loc, icon));
            }
        }

        plugin.getLogger().info("Załadowano " + warps.size() + " warpów");
    }

    public void saveWarps() {
        warpsData = new YamlConfiguration();

        for (Warp warp : warps.values()) {
            Location loc = warp.getLocation();
            if (loc.getWorld() == null) {
                plugin.getLogger().warning("Nie można zapisać warpu " + warp.getName() + " - brak świata!");
                continue;
            }

            warpsData.set(warp.getName() + ".world", loc.getWorld().getName());
            warpsData.set(warp.getName() + ".x", loc.getX());
            warpsData.set(warp.getName() + ".y", loc.getY());
            warpsData.set(warp.getName() + ".z", loc.getZ());
            warpsData.set(warp.getName() + ".yaw", loc.getYaw());
            warpsData.set(warp.getName() + ".pitch", loc.getPitch());
            warpsData.set(warp.getName() + ".icon", warp.getIcon().name());
        }

        try {
            warpsData.save(warpsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean setWarp(String name, Location location, Material icon) {
        if (warps.containsKey(name.toLowerCase())) {
            return false;
        }
        warps.put(name.toLowerCase(), new Warp(name, location, icon));
        saveWarps();
        return true;
    }

    public Warp getWarp(String name) {
        return warps.get(name.toLowerCase());
    }

    public boolean deleteWarp(String name) {
        boolean removed = warps.remove(name.toLowerCase()) != null;
        if (removed) {
            saveWarps();
        }
        return removed;
    }

    public List<Warp> getAllWarps() {
        return new ArrayList<>(warps.values());
    }

    public boolean warpExists(String name) {
        return warps.containsKey(name.toLowerCase());
    }
}