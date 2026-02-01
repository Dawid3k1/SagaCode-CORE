package org.sagamc.sagamcCore.teleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.sagamc.sagamcCore.SagamcCore;

import java.util.*;

public class TeleportManager {

    private final JavaPlugin plugin;
    private final FileConfiguration messages;
    private final Map<UUID, List<TeleportRequest>> pendingRequests = new HashMap<>();
    private final Set<UUID> teleporting = new HashSet<>();
    private final Map<UUID, Location> lastLocations = new HashMap<>();

    private int countdownSeconds;
    private boolean cancelOnMove;
    private boolean cancelOnDamage;
    private int requestExpireSeconds;

    public TeleportManager(JavaPlugin plugin, FileConfiguration config, FileConfiguration messages) {
        this.plugin = plugin;
        this.messages = messages;
        loadConfig(config);
    }

    public void loadConfig(FileConfiguration config) {
        this.countdownSeconds = config.getInt("teleport.countdown-seconds", 5);
        this.cancelOnMove = config.getBoolean("teleport.cancel-on-move", true);
        this.cancelOnDamage = config.getBoolean("teleport.cancel-on-damage", true);
        this.requestExpireSeconds = config.getInt("teleport.request-expire-seconds", 60);
    }

    public void sendRequest(Player requester, Player target) {
        UUID targetUUID = target.getUniqueId();

        if (!pendingRequests.containsKey(targetUUID)) {
            pendingRequests.put(targetUUID, new ArrayList<>());
        }

        List<TeleportRequest> requests = pendingRequests.get(targetUUID);

        requests.removeIf(req -> req.isExpired(requestExpireSeconds));

        if (requests.stream().anyMatch(req -> req.getRequester().equals(requester))) {
            String msg = messages.getString("teleport.juz-wyslana", "§cJuż wysłałeś prośbę!");
            requester.sendMessage(SagamcCore.color(msg));
            return;
        }

        requests.add(new TeleportRequest(requester, target));

        String msgSender = messages.getString("teleport.prosba-wyslana", "§aWysłano prośbę do %gracz%")
                .replace("%gracz%", target.getName());
        requester.sendMessage(SagamcCore.color(msgSender));

        String msgTarget = messages.getString("teleport.prosba-otrzymana", "§a%gracz% prosi o teleportację!")
                .replace("%gracz%", requester.getName());
        target.sendMessage(SagamcCore.color(msgTarget));
    }

    public List<TeleportRequest> getRequests(Player player) {
        UUID uuid = player.getUniqueId();
        if (!pendingRequests.containsKey(uuid)) {
            return new ArrayList<>();
        }

        List<TeleportRequest> requests = pendingRequests.get(uuid);
        requests.removeIf(req -> req.isExpired(requestExpireSeconds));
        return requests;
    }

    public void removeRequest(Player target, Player requester) {
        UUID uuid = target.getUniqueId();
        if (!pendingRequests.containsKey(uuid)) return;

        pendingRequests.get(uuid).removeIf(req -> req.getRequester().equals(requester));
    }

    public void teleportWithCountdown(Player player, Location destination, String successMsg) {
        if (teleporting.contains(player.getUniqueId())) return;

        teleporting.add(player.getUniqueId());
        lastLocations.put(player.getUniqueId(), player.getLocation());

        new BukkitRunnable() {
            int countdown = countdownSeconds;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    teleporting.remove(player.getUniqueId());
                    return;
                }

                if (cancelOnMove && hasMoved(player)) {
                    String msg = messages.getString("teleport.anulowano-ruch", "§cTeleportacja anulowana - ruch!");
                    player.sendMessage(SagamcCore.color(msg));
                    cancel();
                    teleporting.remove(player.getUniqueId());
                    lastLocations.remove(player.getUniqueId());
                    return;
                }

                if (countdown > 0) {
                    String msg = messages.getString("teleport.countdown", "§aTeleportacja za %czas%s")
                            .replace("%czas%", String.valueOf(countdown));

                    player.sendActionBar(net.kyori.adventure.text.Component.text(SagamcCore.color(msg)));
                    countdown--;
                } else {
                    player.teleport(destination);
                    player.sendMessage(SagamcCore.color(successMsg));
                    cancel();
                    teleporting.remove(player.getUniqueId());
                    lastLocations.remove(player.getUniqueId());
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void teleportInstantly(Player player, Location destination, String successMsg) {
        player.teleport(destination);
        player.sendMessage(SagamcCore.color(successMsg));
    }

    private boolean hasMoved(Player player) {
        Location last = lastLocations.get(player.getUniqueId());
        Location current = player.getLocation();

        return last.getX() != current.getX() ||
                last.getY() != current.getY() ||
                last.getZ() != current.getZ();
    }

    public void cancelTeleport(Player player) {
        if (teleporting.contains(player.getUniqueId())) {
            teleporting.remove(player.getUniqueId());
            lastLocations.remove(player.getUniqueId());

            if (cancelOnDamage) {
                String msg = messages.getString("teleport.anulowano-obrazenia", "§cTeleportacja anulowana - obrażenia!");
                player.sendMessage(SagamcCore.color(msg));
            }
        }
    }

    public Location getSpawnLocation(FileConfiguration config) {
        ConfigurationSection spawn = config.getConfigurationSection("teleport.spawn");
        if (spawn == null) return null;

        String worldName = spawn.getString("world");
        if (worldName == null || Bukkit.getWorld(worldName) == null) return null;

        return new Location(
                Bukkit.getWorld(worldName),
                spawn.getDouble("x"),
                spawn.getDouble("y"),
                spawn.getDouble("z"),
                (float) spawn.getDouble("yaw"),
                (float) spawn.getDouble("pitch")
        );
    }
}