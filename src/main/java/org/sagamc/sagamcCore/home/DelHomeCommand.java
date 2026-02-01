package org.sagamc.sagamcCore.home;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.sagamc.sagamcCore.util.ColorUtil;

public class DelHomeCommand implements CommandExecutor {

    private final HomeManager homeManager;
    private final FileConfiguration messages;

    public DelHomeCommand(HomeManager homeManager, FileConfiguration messages) {
        this.homeManager = homeManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cMusisz być graczem!");
            return true;
        }

        if (args.length == 0) {
            String msg = messages.getString("home.usage-delhome", "<red>Użycie: <dark_red>/delhome [nazwa]");
            player.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        String name = args[0].toLowerCase();

        if (homeManager.deleteHome(player, name)) {
            String msg = messages.getString("home.delete-success", "<green>Usunięto dom <dark_green>{NAME}<green>!")
                    .replace("{NAME}", name);
            player.sendMessage(ColorUtil.miniMessage(msg));
        } else {
            String msg = messages.getString("home.delete-notfound", "<red>Dom <dark_red>{NAME} <red>nie istnieje!")
                    .replace("{NAME}", name);
            player.sendMessage(ColorUtil.miniMessage(msg));
        }

        return true;
    }
}
