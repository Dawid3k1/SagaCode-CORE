package org.sagamc.sagamcCore.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ReplyCommand implements CommandExecutor {

    private final FileConfiguration messages;

    public ReplyCommand(FileConfiguration messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cMusisz być graczem!");
            return true;
        }

        if (args.length == 0) {
            String msg = messages.getString("msgReplyUsage", "<red>Użycie: /r [wiadomość]");
            player.sendMessage(MiniMessage.miniMessage().deserialize(msg));
            return true;
        }

        UUID lastUUID = MsgCommand.getLastMessaged(player.getUniqueId());

        if (lastUUID == null) {
            String msg = messages.getString("msgNoReply", "<red>Z nikim ostatnio <dark_red>nie pisałeś!");
            player.sendMessage(MiniMessage.miniMessage().deserialize(msg));
            return true;
        }

        Player target = Bukkit.getPlayer(lastUUID);

        if (target == null || !target.isOnline()) {
            String msg = messages.getString("msgPlayerOffline", "<red>Ten gracz jest <dark_red>offline!");
            player.sendMessage(MiniMessage.miniMessage().deserialize(msg));
            return true;
        }

        StringBuilder messageBuilder = new StringBuilder();
        for (String arg : args) {
            messageBuilder.append(arg).append(" ");
        }
        String message = messageBuilder.toString().trim();

        String format = messages.getString("formatMsg",
                        "<dark_gray>(<#97CFF5>{SENDER} <dark_gray>→ <#49B5FF>{RECEIVER}<dark_gray>) <dark_gray>» <white>{MESSAGE}")
                .replace("{SENDER}", player.getName())
                .replace("{RECEIVER}", target.getName())
                .replace("{MESSAGE}", message);

        player.sendMessage(MiniMessage.miniMessage().deserialize(format));
        target.sendMessage(MiniMessage.miniMessage().deserialize(format));

        return true;
    }
}