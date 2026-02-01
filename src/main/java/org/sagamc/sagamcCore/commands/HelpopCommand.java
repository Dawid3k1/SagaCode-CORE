package org.sagamc.sagamcCore.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.sagamc.sagamcCore.util.ColorUtil;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HelpopCommand implements CommandExecutor {

    private final FileConfiguration messages;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_TIME = 30000;

    public HelpopCommand(FileConfiguration messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cMusisz być graczem!");
            return true;
        }

        if (args.length == 0) {
            String msg = messages.getString("msgHelpopUsage", "<red>Użycie: <dark_red>/helpop [treść]");
            player.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        UUID uuid = player.getUniqueId();
        if (cooldowns.containsKey(uuid)) {
            long timeLeft = (cooldowns.get(uuid) + COOLDOWN_TIME) - System.currentTimeMillis();
            if (timeLeft > 0) {
                String msg = messages.getString("msgHelpopCooldown", "<red>Zwolnij wysyłanie wiadomości na <dark_red>helpopie!");
                player.sendMessage(ColorUtil.miniMessage(msg));
                return true;
            }
        }

        StringBuilder messageBuilder = new StringBuilder();
        for (String arg : args) {
            messageBuilder.append(arg).append(" ");
        }
        String message = messageBuilder.toString().trim();

        String sentMsg = messages.getString("msgHelpopSent", "<green>Wiadomość do administracji <dark_green>została wysłana!");
        player.sendMessage(ColorUtil.miniMessage(sentMsg));

        List<String> helpopReceived = messages.getStringList("helpopReceived");
        for (String line : helpopReceived) {
            String formatted = line
                    .replace("{PLAYER}", player.getName())
                    .replace("{MESSAGE}", message);

            for (Player admin : Bukkit.getOnlinePlayers()) {
                if (admin.hasPermission("sagamc.helpop.receive")) {
                    admin.sendMessage(ColorUtil.miniMessage(formatted));
                }
            }
        }

        Component titleComponent = ColorUtil.miniMessage("<#97CFF5><b>Helpop</b>");
        Component subtitleComponent = ColorUtil.miniMessage("<white>Nowe zgłoszenie!");

        Title title = Title.title(
                titleComponent,
                subtitleComponent,
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500))
        );

        for (Player admin : Bukkit.getOnlinePlayers()) {
            if (admin.hasPermission("sagamc.helpop.receive")) {
                admin.showTitle(title);
            }
        }

        cooldowns.put(uuid, System.currentTimeMillis());

        return true;
    }
}