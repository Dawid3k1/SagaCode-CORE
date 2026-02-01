package org.sagamc.sagamcCore.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.sagamc.sagamcCore.SagamcCore;

public class InvseeCommand implements CommandExecutor {

    private final FileConfiguration config;

    public InvseeCommand(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(SagamcCore.color(config.getString("msgMustBePlayer", "§cMusisz być graczem!")));
            return true;
        }

        if (!player.hasPermission("sagamc.invsee")) {
            sender.sendMessage(SagamcCore.color(config.getString("msgNoPermission", "§cBrak permisji!").replace("{PERM}", "sagamc.invsee")));
            return true;
        }

        if (args.length == 0) {
            String msg = config.getString("msgInvseeUsage", "&cPoprawne użycie: &4/invsee [gracz]");
            player.sendMessage(SagamcCore.color(msg));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null || !target.isOnline()) {
            String msg = config.getString("msgPlayerOffline", "&cTen gracz jest aktualnie &4offline!");
            player.sendMessage(SagamcCore.color(msg));
            return true;
        }

        player.openInventory(target.getInventory());
        return true;
    }
}