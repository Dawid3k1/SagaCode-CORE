package org.sagamc.sagamcCore.vanish;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {

    private final VanishManager vanishManager;
    private final FileConfiguration messages;

    public VanishCommand(VanishManager vanishManager, FileConfiguration messages) {
        this.vanishManager = vanishManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cMusisz być graczem!");
            return true;
        }

        if (!player.hasPermission("sagamc.vanish")) {
            String msg = messages.getString("brak-permisji", "<red>Brak permisji!");
            player.sendMessage(MiniMessage.miniMessage().deserialize(msg));
            return true;
        }

        boolean isVanished = vanishManager.isVanished(player);
        vanishManager.setVanished(player, !isVanished);

        if (!isVanished) {
            String msg = messages.getString("vanish.enabled", "<green>Vanish został <dark_green>włączony!");
            player.sendMessage(MiniMessage.miniMessage().deserialize(msg));
        } else {
            String msg = messages.getString("vanish.disabled", "<red>Vanish został <dark_red>wyłączony!");
            player.sendMessage(MiniMessage.miniMessage().deserialize(msg));
        }

        return true;
    }
}