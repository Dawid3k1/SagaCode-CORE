package org.sagamc.sagamcCore.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.time.Duration;

public class BroadcastCommand implements CommandExecutor {

    private final FileConfiguration messages;

    public BroadcastCommand(FileConfiguration messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sagamc.broadcast")) {
            String msg = messages.getString("brak-permisji", "<red>Brak permisji!");
            sender.sendMessage(MiniMessage.miniMessage().deserialize(msg));
            return true;
        }

        if (args.length < 2) {
            String msg = messages.getString("broadcast.usage",
                    "<red>Użycie: <dark_red>/alert [subtitle/chat/actionbar/title] [wiadomość]");
            sender.sendMessage(MiniMessage.miniMessage().deserialize(msg));
            return true;
        }

        String type = args[0].toLowerCase();
        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            messageBuilder.append(args[i]).append(" ");
        }
        String message = messageBuilder.toString().trim();

        Component component = MiniMessage.miniMessage().deserialize(message);

        switch (type) {
            case "chat" -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(component);
                }
            }

            case "actionbar" -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendActionBar(component);
                }
            }

            case "title" -> {
                Title title = Title.title(
                        component,
                        Component.empty(),
                        Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500))
                );
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.showTitle(title);
                }
            }

            case "subtitle" -> {
                Title title = Title.title(
                        Component.empty(),
                        component,
                        Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500))
                );
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.showTitle(title);
                }
            }

            default -> {
                String msg = messages.getString("broadcast.usage",
                        "<red>Użycie: <dark_red>/alert [subtitle/chat/actionbar/title] [wiadomość]");
                sender.sendMessage(MiniMessage.miniMessage().deserialize(msg));
                return true;
            }
        }

        String successMsg = messages.getString("broadcast.sent", "<green>Broadcast został <dark_green>wysłany!");
        sender.sendMessage(MiniMessage.miniMessage().deserialize(successMsg));

        return true;
    }
}