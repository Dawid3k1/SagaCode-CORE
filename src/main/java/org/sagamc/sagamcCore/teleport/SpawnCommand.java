package org.sagamc.sagamcCore.teleport;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.sagamc.sagamcCore.SagamcCore;

public class SpawnCommand implements CommandExecutor {

    private final TeleportManager teleportManager;
    private final FileConfiguration config;
    private final FileConfiguration messages;

    public SpawnCommand(TeleportManager teleportManager, FileConfiguration config, FileConfiguration messages) {
        this.teleportManager = teleportManager;
        this.config = config;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(SagamcCore.color(messages.getString("tylko-gracz", "§cMusisz być graczem!")));
            return true;
        }

        Location spawn = teleportManager.getSpawnLocation(config);

        if (spawn == null) {
            player.sendMessage(SagamcCore.color(messages.getString("teleport.spawn-nie-ustawiony", "§cSpawn nie jest ustawiony!")));
            return true;
        }

        String successMsg = messages.getString("teleport.spawn-sukces", "§aPrzeteleportowano na spawn!");

        teleportManager.teleportWithCountdown(player, spawn, successMsg);
        return true;
    }
}