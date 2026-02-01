package org.sagamc.sagamcCore.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MsgCommand implements CommandExecutor {

    private final FileConfiguration messages;
    private static final Map<UUID, UUID> lastMessaged = new HashMap<>();

    public MsgCommand(FileConfiguration messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cMusisz być graczem!");
            return true;
        }

        if (args.length < 2) {
            String msg = messages.getString("msgMsgUsage", "<red>Użycie: /msg [gracz] [wiadomość]");
            player.sendMessage(MiniMessage.miniMessage().deserialize(msg));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null || !target.isOnline()) {
            String msg = messages.getString("msgPlayerOffline", "<red>Ten gracz jest <dark_red>offline!");
            player.sendMessage(MiniMessage.miniMessage().deserialize(msg));
            return true;
        }

        if (target.equals(player)) {
            String msg = messages.getString("msgSelfMsg", "<red>Czemu piszesz sam <dark_red>ze sobą?");
            player.sendMessage(MiniMessage.miniMessage().deserialize(msg));
            return true;
        }

        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            messageBuilder.append(args[i]).append(" ");
        }
        String message = messageBuilder.toString().trim();

        String format = messages.getString("formatMsg",
                        "<dark_gray>(<#97CFF5>{SENDER} <dark_gray>→ <#49B5FF>{RECEIVER}<dark_gray>) <dark_gray>» <white>{MESSAGE}")
                .replace("{SENDER}", player.getName())
                .replace("{RECEIVER}", target.getName())
                .replace("{MESSAGE}", message);

        player.sendMessage(MiniMessage.miniMessage().deserialize(format));
        target.sendMessage(MiniMessage.miniMessage().deserialize(format));

        lastMessaged.put(player.getUniqueId(), target.getUniqueId());
        lastMessaged.put(target.getUniqueId(), player.getUniqueId());

        return true;
    }

    public static UUID getLastMessaged(UUID playerUUID) {
        return lastMessaged.get(playerUUID);
    }
}