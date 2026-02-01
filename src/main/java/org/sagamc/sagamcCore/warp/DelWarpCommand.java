package org.sagamc.sagamcCore.warp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.sagamc.sagamcCore.util.ColorUtil;

public class DelWarpCommand implements CommandExecutor {

    private final WarpManager warpManager;
    private final FileConfiguration messages;

    public DelWarpCommand(WarpManager warpManager, FileConfiguration messages) {
        this.warpManager = warpManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sagamc.warp.admin")) {
            String msg = messages.getString("brak-permisji", "<red>Brak permisji!");
            sender.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        if (args.length == 0) {
            String msg = messages.getString("warp.usage-delwarp", "<red>Użycie: <dark_red>/delwarp [nazwa]");
            sender.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        String name = args[0].toLowerCase();

        if (warpManager.deleteWarp(name)) {
            String msg = messages.getString("warp.delete-success", "<green>Usunięto warp <dark_green>{NAME}<green>!")
                    .replace("{NAME}", name);
            sender.sendMessage(ColorUtil.miniMessage(msg));
        } else {
            String msg = messages.getString("warp.delete-notfound", "<red>Warp <dark_red>{NAME} <red>nie istnieje!")
                    .replace("{NAME}", name);
            sender.sendMessage(ColorUtil.miniMessage(msg));
        }

        return true;
    }
}