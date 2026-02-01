package org.sagamc.sagamcCore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.sagamc.sagamcCore.util.ColorUtil;

public class SpeedCommand implements CommandExecutor {

    private final FileConfiguration messages;

    public SpeedCommand(FileConfiguration messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cMusisz być graczem!");
            return true;
        }

        if (!player.hasPermission("sagamc.speed")) {
            String msg = messages.getString("brak-permisji", "<red>Brak permisji!");
            player.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        if (args.length == 0) {
            String msg = messages.getString("msgSpeedUsage", "<red>Użycie: <dark_red>/speed [0.1-1.0]");
            player.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        float speed;
        try {
            speed = Float.parseFloat(args[0]);
        } catch (NumberFormatException e) {
            String msg = messages.getString("msgSpeedUsage", "<red>Użycie: <dark_red>/speed [0.1-1.0]");
            player.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        if (speed < 0.1f || speed > 1.0f) {
            String msg = messages.getString("msgSpeedRange", "<red>Wartość musi być z zakresu <dark_red>0.1 do 1.0");
            player.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        if (player.isFlying()) {
            player.setFlySpeed(speed);
            String msg = messages.getString("msgFlySpeedSet", "<green>Ustawiono prędkość latania na <dark_green>{SPEED}")
                    .replace("{SPEED}", String.valueOf(speed));
            player.sendMessage(ColorUtil.miniMessage(msg));
        } else {
            player.setWalkSpeed(speed);
            String msg = messages.getString("msgWalkSpeedSet", "<green>Ustawiono prędkość chodzenia na <dark_green>{SPEED}")
                    .replace("{SPEED}", String.valueOf(speed));
            player.sendMessage(ColorUtil.miniMessage(msg));
        }

        return true;
    }
}
