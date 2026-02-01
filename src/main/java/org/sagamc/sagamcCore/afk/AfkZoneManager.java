package org.sagamc.sagamcCore.afk;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.bossbar.BossBar;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.sagamc.sagamcCore.SagamcCore;

import java.util.*;

public class AfkZoneManager {

    private final JavaPlugin plugin;
    private final Economy economy;
    private final FileConfiguration config;
    private final FileConfiguration messages;

    private boolean enabled;
    private String regionName;

    // Premium (klucz)
    private int premiumSeconds;
    private double premiumChance;
    private String premiumCommand;
    private BossBar.Color premiumBarColor;
    private BossBar.Overlay premiumBarOverlay;

    // Standard (pieniądze)
    private int standardSeconds;
    private double standardMoney;
    private BossBar.Color standardBarColor;
    private BossBar.Overlay standardBarOverlay;

    private final Map<UUID, Long> premiumTimers = new HashMap<>();
    private final Map<UUID, Long> standardTimers = new HashMap<>();
    private final Map<UUID, BossBar> premiumBossBars = new HashMap<>();
    private final Map<UUID, BossBar> standardBossBars = new HashMap<>();

    public AfkZoneManager(JavaPlugin plugin, Economy economy, FileConfiguration config, FileConfiguration messages) {
        this.plugin = plugin;
        this.economy = economy;
        this.config = config;
        this.messages = messages;
        loadConfig();
        startTimers();
    }

    public void loadConfig() {
        this.enabled = config.getBoolean("afkZoneEnabled", true);
        this.regionName = config.getString("afkZoneRegionName", "afk");

        // Premium
        this.premiumSeconds = config.getInt("afkPremiumRewardSeconds", 3600);
        this.premiumChance = config.getDouble("afkPremiumRewardChance", 50.0);
        this.premiumCommand = config.getString("afkPremiumRewardCommand", "");
        this.premiumBarColor = convertColor(config.getString("afkPremiumBossBarColor", "YELLOW"));
        this.premiumBarOverlay = convertOverlay(config.getString("afkPremiumBossBarStyle", "NOTCHED_20"));

        // Standard
        this.standardSeconds = config.getInt("afkStandardRewardSeconds", 60);
        this.standardMoney = config.getDouble("afkStandardRewardMoney", 5.0);
        this.standardBarColor = convertColor(config.getString("afkStandardBossBarColor", "GREEN"));
        this.standardBarOverlay = convertOverlay(config.getString("afkStandardBossBarStyle", "NOTCHED_12"));
    }

    private BossBar.Color convertColor(String color) {
        return switch (color.toUpperCase()) {
            case "PINK" -> BossBar.Color.PINK;
            case "BLUE" -> BossBar.Color.BLUE;
            case "RED" -> BossBar.Color.RED;
            case "GREEN" -> BossBar.Color.GREEN;
            case "YELLOW" -> BossBar.Color.YELLOW;
            case "PURPLE" -> BossBar.Color.PURPLE;
            case "WHITE" -> BossBar.Color.WHITE;
            default -> BossBar.Color.GREEN;
        };
    }

    private BossBar.Overlay convertOverlay(String style) {
        return switch (style.toUpperCase()) {
            case "NOTCHED_6" -> BossBar.Overlay.NOTCHED_6;
            case "NOTCHED_10" -> BossBar.Overlay.NOTCHED_10;
            case "NOTCHED_12" -> BossBar.Overlay.NOTCHED_12;
            case "NOTCHED_20" -> BossBar.Overlay.NOTCHED_20;
            default -> BossBar.Overlay.PROGRESS;
        };
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isInAfkZone(Player player) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));

        for (ProtectedRegion region : set) {
            if (region.getId().equalsIgnoreCase(regionName)) {
                return true;
            }
        }
        return false;
    }

    public void addPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        premiumTimers.put(uuid, System.currentTimeMillis());
        standardTimers.put(uuid, System.currentTimeMillis());

        // Utwórz BossBary Adventure API
        BossBar premiumBar = BossBar.bossBar(
                Component.text(""),
                1.0f,
                premiumBarColor,
                premiumBarOverlay
        );
        player.showBossBar(premiumBar);
        premiumBossBars.put(uuid, premiumBar);

        BossBar standardBar = BossBar.bossBar(
                Component.text(""),
                1.0f,
                standardBarColor,
                standardBarOverlay
        );
        player.showBossBar(standardBar);
        standardBossBars.put(uuid, standardBar);

        // FIX: Użyj SagamcCore.color() + MiniMessage
        String msg = messages.getString("afk.enabled", "<green>Dołączyłeś na strefę <dark_green>AFK!");
        player.sendMessage(MiniMessage.miniMessage().deserialize(msg));
    }

    public void removePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        premiumTimers.remove(uuid);
        standardTimers.remove(uuid);

        BossBar premiumBar = premiumBossBars.remove(uuid);
        if (premiumBar != null) {
            player.hideBossBar(premiumBar);
        }

        BossBar standardBar = standardBossBars.remove(uuid);
        if (standardBar != null) {
            player.hideBossBar(standardBar);
        }

        // FIX: Użyj MiniMessage
        String msg = messages.getString("afk.disabled", "<red>Wyszedłeś ze strefy <dark_red>AFK!");
        player.sendMessage(MiniMessage.miniMessage().deserialize(msg));
    }

    public boolean isTracking(Player player) {
        return premiumTimers.containsKey(player.getUniqueId());
    }

    private void startTimers() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!enabled) return;

                for (UUID uuid : new HashSet<>(premiumTimers.keySet())) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null || !player.isOnline() || !isInAfkZone(player)) {
                        removePlayer(player != null ? player : Bukkit.getOfflinePlayer(uuid).getPlayer());
                        continue;
                    }

                    updateBossBars(player);
                    checkRewards(player);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void updateBossBars(Player player) {
        UUID uuid = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        // Premium BossBar
        long premiumElapsed = (currentTime - premiumTimers.get(uuid)) / 1000;
        double premiumProgress = Math.min(1.0, (double) premiumElapsed / premiumSeconds);
        int premiumRemaining = Math.max(0, premiumSeconds - (int) premiumElapsed);

        String premiumText = messages.getString("afk.premium-reward-title", "")
                .replace("<chance>", String.valueOf((int) premiumChance))
                .replace("<time>", formatTime(premiumRemaining))
                .replace("<percentage>", String.format("%.0f", premiumProgress * 100));

        BossBar premiumBar = premiumBossBars.get(uuid);
        if (premiumBar != null) {
            Component premiumComponent = MiniMessage.miniMessage().deserialize(premiumText);
            premiumBar.name(premiumComponent);
            premiumBar.progress((float) premiumProgress);
        }

        // Standard BossBar
        long standardElapsed = (currentTime - standardTimers.get(uuid)) / 1000;
        double standardProgress = Math.min(1.0, (double) standardElapsed / standardSeconds);
        int standardRemaining = Math.max(0, standardSeconds - (int) standardElapsed);

        String standardText = messages.getString("afk.standard-reward-title", "")
                .replace("<money>", String.valueOf((int) standardMoney))
                .replace("<time>", formatTime(standardRemaining))
                .replace("<percentage>", String.format("%.0f", standardProgress * 100));

        BossBar standardBar = standardBossBars.get(uuid);
        if (standardBar != null) {
            Component standardComponent = MiniMessage.miniMessage().deserialize(standardText);
            standardBar.name(standardComponent);
            standardBar.progress((float) standardProgress);
        }
    }

    private void checkRewards(Player player) {
        UUID uuid = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        // Premium reward
        long premiumElapsed = (currentTime - premiumTimers.get(uuid)) / 1000;
        if (premiumElapsed >= premiumSeconds) {
            givePremiumReward(player);
            premiumTimers.put(uuid, currentTime);
        }

        // Standard reward
        long standardElapsed = (currentTime - standardTimers.get(uuid)) / 1000;
        if (standardElapsed >= standardSeconds) {
            giveStandardReward(player);
            standardTimers.put(uuid, currentTime);
        }
    }

    private void givePremiumReward(Player player) {
        Random random = new Random();
        double roll = random.nextDouble() * 100;

        if (roll <= premiumChance) {
            String cmd = premiumCommand.replace("<player>", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);

            String msg = messages.getString("afk.premium-success", "<green>Otrzymałeś super <dark_green>nagrodę!");
            player.sendMessage(MiniMessage.miniMessage().deserialize(msg));
        } else {
            String msg = messages.getString("afk.premium-fail", "<red>Nie udało się trafić <dark_red>super nagrody!");
            player.sendMessage(MiniMessage.miniMessage().deserialize(msg));
        }
    }

    private void giveStandardReward(Player player) {
        economy.depositPlayer(player, standardMoney);
        String msg = messages.getString("afk.standard-success", "<green>Otrzymałeś standardową <dark_green>nagrodę!");
        player.sendMessage(MiniMessage.miniMessage().deserialize(msg));
    }

    private String formatTime(int seconds) {
        if (seconds < 60) return seconds + "s";
        int minutes = seconds / 60;
        int secs = seconds % 60;
        if (minutes < 60) return minutes + "m " + secs + "s";
        int hours = minutes / 60;
        int mins = minutes % 60;
        return hours + "h " + mins + "m";
    }
}