package org.sagamc.sagamcCore.home;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.sagamc.sagamcCore.util.ColorUtil;

public class SetHomeCommand implements CommandExecutor {

    private final HomeManager homeManager;
    private final FileConfiguration messages;

    public SetHomeCommand(HomeManager homeManager, FileConfiguration messages) {
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
            String msg = messages.getString("home.usage-set", "<red>Użycie: <dark_red>/sethome [nazwa]");
            player.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        String name = args[0].toLowerCase();

        if (homeManager.getHome(player, name) != null) {
            String msg = messages.getString("home.set-exists", "<red>Dom o nazwie <dark_red>{NAME} <red>już istnieje!")
                    .replace("{NAME}", name);
            player.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        int limit = homeManager.getLimit(player);
        if (homeManager.getHomes(player).size() >= limit) {
            String msg = messages.getString("home.set-limit", "<red>Osiągnięto limit domów! <dark_red>Max: {LIMIT}")
                    .replace("{LIMIT}", String.valueOf(limit));
            player.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        homeManager.setHome(player, name);
        String msg = messages.getString("home.set-success", "<green>Ustawiono dom <dark_green>{NAME}<green>!")
                .replace("{NAME}", name);
        player.sendMessage(ColorUtil.miniMessage(msg));

        return true;
    }
}
