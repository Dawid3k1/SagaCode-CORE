package org.sagamc.sagamcCore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.sagamc.sagamcCore.util.ColorUtil;

public class FlyCommand implements CommandExecutor {

    private final FileConfiguration messages;

    public FlyCommand(FileConfiguration messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cMusisz być graczem!");
            return true;
        }

        if (!player.hasPermission("sagamc.fly")) {
            String msg = messages.getString("brak-permisji", "<red>Brak permisji!");
            player.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        boolean flying = !player.getAllowFlight();
        player.setAllowFlight(flying);
        player.setFlying(flying);

        String msgKey = flying ? "msgFlyEnabled" : "msgFlyDisabled";
        String msg = messages.getString(msgKey, flying ?
                "<green>Latanie zostało <dark_green>włączone!" :
                "<red>Latanie zostało <dark_red>wyłączone!");

        player.sendMessage(ColorUtil.miniMessage(msg));
        return true;
    }
}