package org.sagamc.sagamcCore.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.sagamc.sagamcCore.SagamcCore;

public class GamemodeCommand implements CommandExecutor {

    private final FileConfiguration messages;

    public GamemodeCommand(FileConfiguration messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sagamc.gamemode")) {
            sender.sendMessage(SagamcCore.color(messages.getString("brak-permisji", "§cBrak permisji!").replace("{PERM}", "sagamc.gamemode")));
            return true;
        }

        if (args.length == 0) {
            String msg = messages.getString("commands.gamemode-usage", "&cPoprawne użycie: &4/gamemode [0/1/2/3]");
            sender.sendMessage(SagamcCore.color(msg));
            return true;
        }

        GameMode mode = parseGameMode(args[0]);
        if (mode == null) {
            String msg = messages.getString("commands.gamemode-usage", "&cPoprawne użycie: &4/gamemode [0/1/2/3]");
            sender.sendMessage(SagamcCore.color(msg));
            return true;
        }

        if (args.length >= 2) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null || !target.isOnline()) {
                sender.sendMessage(SagamcCore.color(messages.getString("gracz-offline", "&cTen gracz jest aktualnie &4offline!")));
                return true;
            }

            target.setGameMode(mode);
            String msg = messages.getString("commands.gamemode-changed-other", "&aZmieniono tryb gry gracza &2{PLAYER} &ana &2{MODE}")
                    .replace("%gracz%", target.getName())
                    .replace("%mode%", mode.name());
            sender.sendMessage(SagamcCore.color(msg));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(SagamcCore.color(messages.getString("tylko-gracz", "§cMusisz być graczem!")));
            return true;
        }

        player.setGameMode(mode);
        String msg = messages.getString("commands.gamemode-changed-self", "&aZmieniono twój tryb gry na &2{MODE}")
                .replace("%mode%", mode.name());
        player.sendMessage(SagamcCore.color(msg));

        return true;
    }

    private GameMode parseGameMode(String input) {
        return switch (input.toLowerCase()) {
            case "0", "s", "survival" -> GameMode.SURVIVAL;
            case "1", "c", "creative" -> GameMode.CREATIVE;
            case "2", "a", "adventure" -> GameMode.ADVENTURE;
            case "3", "sp", "spectator" -> GameMode.SPECTATOR;
            default -> null;
        };
    }
}