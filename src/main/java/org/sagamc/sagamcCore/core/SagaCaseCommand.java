package org.sagamc.sagamcCore.core;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SagaCaseCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§x§f§f§5§5§5§5❌ &8⁎ <gradient:#aa0000:#ff5555:#aa0000>Ta komenda jest dostępna tylko dla graczy!</gradient>");
            return true;
        }


        if (args.length == 0) {
            player.performCommand("fastcase");
            return true;
        }

        String subCommand = String.join(" ", args);
        player.performCommand("fastcase " + subCommand);

        return true;
    }
}
