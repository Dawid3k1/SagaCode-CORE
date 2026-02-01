package org.sagamc.sagamcCore.warp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.sagamc.sagamcCore.teleport.TeleportManager;
import org.sagamc.sagamcCore.util.ColorUtil;

public class WarpCommand implements CommandExecutor {

    private final WarpManager warpManager;
    private final TeleportManager teleportManager;
    private final FileConfiguration messages;

    public WarpCommand(WarpManager warpManager, TeleportManager teleportManager, FileConfiguration messages) {
        this.warpManager = warpManager;
        this.teleportManager = teleportManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cMusisz być graczem!");
            return true;
        }

        if (args.length == 0) {
            WarpGUI.openWarpsGUI(player, warpManager, messages);
            return true;
        }

        String name = args[0].toLowerCase();
        Warp warp = warpManager.getWarp(name);

        if (warp == null) {
            String msg = messages.getString("warp.teleport-notfound", "<red>Warp <dark_red>{NAME} <red>nie istnieje!")
                    .replace("{NAME}", name);
            player.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        String successMsg = messages.getString("warp.teleport-success", "<green>Teleportowano do warpu <dark_green>{NAME}<green>!")
                .replace("{NAME}", name);

        teleportManager.teleportWithCountdown(player, warp.getLocation(), ColorUtil.miniToLegacy(successMsg));

        return true;
    }
}