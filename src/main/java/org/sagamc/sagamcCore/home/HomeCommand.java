package org.sagamc.sagamcCore.home;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.sagamc.sagamcCore.teleport.TeleportManager;
import org.sagamc.sagamcCore.util.ColorUtil;

public class HomeCommand implements CommandExecutor {

    private final HomeManager homeManager;
    private final TeleportManager teleportManager;
    private final FileConfiguration messages;

    public HomeCommand(HomeManager homeManager, TeleportManager teleportManager, FileConfiguration messages) {
        this.homeManager = homeManager;
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
            String msg = messages.getString("home.usage-home", "<red>Użycie: <dark_red>/home [nazwa]");
            player.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        String name = args[0].toLowerCase();

        if (homeManager.isOnCooldown(player)) {
            int remaining = homeManager.getCooldownRemaining(player);
            String msg = messages.getString("home.cooldown", "<red>Musisz poczekać <dark_red>{TIME}s <red>przed użyciem /home!")
                    .replace("{TIME}", String.valueOf(remaining));
            player.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        Home home = homeManager.getHome(player, name);
        if (home == null) {
            String msg = messages.getString("home.teleport-notfound", "<red>Dom <dark_red>{NAME} <red>nie istnieje!")
                    .replace("{NAME}", name);
            player.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        String successMsg = messages.getString("home.teleport-success", "<green>Teleportowano do domu <dark_green>{NAME}<green>!")
                .replace("{NAME}", name);

        teleportManager.teleportWithCountdown(player, home.getLocation(), ColorUtil.miniToLegacy(successMsg));
        homeManager.setCooldown(player);

        return true;
    }
}
