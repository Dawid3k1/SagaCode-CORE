package org.sagamc.sagamcCore.chat;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.sagamc.sagamcCore.util.ColorUtil;

public class ChatCommand implements CommandExecutor {

    private final ChatManager chatManager;
    private final FileConfiguration messages;

    public ChatCommand(ChatManager chatManager, FileConfiguration messages) {
        this.chatManager = chatManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sagamc.chat.admin")) {
            String msg = messages.getString("brak-permisji", "<red>Brak permisji!");
            sender.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        if (args.length == 0) {
            String msg = messages.getString("chat.usage", "<red>Użycie: <dark_red>/chat <on/off/clear/cooldown>");
            sender.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "on" -> {
                chatManager.setChatEnabled(true);
                String msg = messages.getString("chat.wlaczono", "<green>Chat został <dark_green>włączony!");
                Bukkit.broadcast(ColorUtil.miniMessage(msg));
            }

            case "off" -> {
                chatManager.setChatEnabled(false);
                String msg = messages.getString("chat.wylaczono-admin", "<red>Chat został <dark_red>wyłączony!");
                Bukkit.broadcast(ColorUtil.miniMessage(msg));
            }

            case "clear" -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.hasPermission("sagamc.chat.bypass")) {
                        for (int i = 0; i < 100; i++) {
                            player.sendMessage("");
                        }

                        String msg = messages.getString("chat.cleared-player", "<green>Wyczyszczono <dark_green>czat!");
                        player.sendMessage(ColorUtil.miniMessage(msg));
                    }
                }

                String msg = messages.getString("chat.wyczyszczono", "<green>Chat został <dark_green>wyczyszczony!");
                sender.sendMessage(ColorUtil.miniMessage(msg));
            }

            case "cooldown" -> {
                if (args.length < 2) {
                    sender.sendMessage(ColorUtil.miniMessage("<red>Użycie: <dark_red>/chat cooldown <sekundy>"));
                    return true;
                }

                try {
                    int seconds = Integer.parseInt(args[1]);
                    if (seconds < 0) {
                        sender.sendMessage(ColorUtil.miniMessage("<red>Cooldown nie może być ujemny!"));
                        return true;
                    }

                    chatManager.setCooldownSeconds(seconds);
                    String msg = messages.getString("chat.cooldown-ustawiono", "<green>Cooldown ustawiono na <dark_green>{TIME}s")
                            .replace("{TIME}", String.valueOf(seconds));
                    sender.sendMessage(ColorUtil.miniMessage(msg));

                } catch (NumberFormatException e) {
                    sender.sendMessage(ColorUtil.miniMessage("<red>Podaj prawidłową liczbę!"));
                }
            }

            default -> sender.sendMessage(ColorUtil.miniMessage(messages.getString("chat.usage", "<red>Użycie: <dark_red>/chat <on/off/clear/cooldown>")));
        }

        return true;
    }
}