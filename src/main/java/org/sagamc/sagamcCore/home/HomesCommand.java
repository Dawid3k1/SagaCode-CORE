package org.sagamc.sagamcCore.home;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.sagamc.sagamcCore.util.ColorUtil;

import java.util.List;

public class HomesCommand implements CommandExecutor {

    private final HomeManager homeManager;
    private final FileConfiguration messages;

    public HomesCommand(HomeManager homeManager, FileConfiguration messages) {
        this.homeManager = homeManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cMusisz być graczem!");
            return true;
        }

        List<Home> homes = homeManager.getHomes(player);
        int limit = homeManager.getLimit(player);

        if (homes.isEmpty()) {
            String msg = messages.getString("home.list-empty", "<red>Nie masz żadnych <dark_red>domów!");
            player.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        String header = messages.getString("home.list-header", "<green>Twoje domy <dark_gray>({COUNT}/{LIMIT})<green>:")
                .replace("{COUNT}", String.valueOf(homes.size()))
                .replace("{LIMIT}", String.valueOf(limit));
        player.sendMessage(ColorUtil.miniMessage(header));

        for (Home home : homes) {
            Location loc = home.getLocation();
            String item = messages.getString("home.list-item", "<dark_gray>• <white>{NAME} <dark_gray>- <gray>({X}, {Y}, {Z})")
                    .replace("{NAME}", home.getName())
                    .replace("{X}", String.valueOf((int) loc.getX()))
                    .replace("{Y}", String.valueOf((int) loc.getY()))
                    .replace("{Z}", String.valueOf((int) loc.getZ()));
            player.sendMessage(ColorUtil.miniMessage(item));
        }

        return true;
    }
}