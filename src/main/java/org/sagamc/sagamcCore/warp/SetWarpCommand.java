package org.sagamc.sagamcCore.warp;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.sagamc.sagamcCore.util.ColorUtil;

public class SetWarpCommand implements CommandExecutor {

    private final WarpManager warpManager;
    private final FileConfiguration messages;

    public SetWarpCommand(WarpManager warpManager, FileConfiguration messages) {
        this.warpManager = warpManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cMusisz być graczem!");
            return true;
        }

        if (!player.hasPermission("sagamc.warp.admin")) {
            String msg = messages.getString("brak-permisji", "<red>Brak permisji!");
            player.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        if (args.length == 0) {
            String msg = messages.getString("warp.usage-setwarp", "<red>Użycie: <dark_red>/setwarp [nazwa]");
            player.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        String name = args[0].toLowerCase();

        if (warpManager.warpExists(name)) {
            String msg = messages.getString("warp.set-exists", "<red>Warp <dark_red>{NAME} <red>już istnieje!")
                    .replace("{NAME}", name);
            player.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        Material icon = Material.ENDER_PEARL;
        if (args.length > 1) {
            try {
                icon = Material.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        warpManager.setWarp(name, player.getLocation(), icon);
        String msg = messages.getString("warp.set-success", "<green>Ustawiono warp <dark_green>{NAME}<green>!")
                .replace("{NAME}", name);
        player.sendMessage(ColorUtil.miniMessage(msg));

        return true;
    }
}