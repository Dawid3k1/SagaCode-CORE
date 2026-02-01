package org.sagamc.sagamcCore.teleport;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.sagamc.sagamcCore.SagamcCore;

public class TphereCommand implements CommandExecutor {

    private final TeleportManager teleportManager;
    private final FileConfiguration messages;

    public TphereCommand(TeleportManager teleportManager, FileConfiguration messages) {
        this.teleportManager = teleportManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(SagamcCore.color(messages.getString("tylko-gracz", "§cMusisz być graczem!")));
            return true;
        }

        if (!player.hasPermission("sagamc.tphere")) {
            player.sendMessage(SagamcCore.color(messages.getString("brak-permisji", "§cBrak permisji!")));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(SagamcCore.color(messages.getString("teleport.tphere-usage", "§cUżycie: /tphere <gracz>")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null || !target.isOnline()) {
            player.sendMessage(SagamcCore.color(messages.getString("teleport.gracz-offline", "§cTen gracz jest offline!")));
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage(SagamcCore.color(messages.getString("teleport.tp-do-siebie", "§cNie możesz teleportować się do siebie!")));
            return true;
        }

        String successMsg = messages.getString("teleport.tphere-sukces", "§aPrzeteleportowano %gracz% do siebie")
                .replace("%gracz%", target.getName());

        teleportManager.teleportInstantly(target, player.getLocation(), "");
        player.sendMessage(SagamcCore.color(successMsg));
        return true;
    }
}