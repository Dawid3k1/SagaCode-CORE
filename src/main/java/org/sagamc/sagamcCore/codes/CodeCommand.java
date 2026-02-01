package org.sagamc.sagamcCore.codes;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.sagamc.sagamcCore.SagamcCore;

public class CodeCommand implements CommandExecutor {

    private final CodeManager codeManager;
    private final FileConfiguration messages;

    public CodeCommand(CodeManager codeManager, FileConfiguration messages) {
        this.codeManager = codeManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(SagamcCore.color(messages.getString("tylko-gracz", "§cMusisz być graczem!")));
            return true;
        }

        if (args.length == 0) {
            String msg = messages.getString("codes.brak-argumentu", "§cMusisz podać kod!");
            player.sendMessage(SagamcCore.color(msg));
            return true;
        }

        String codeName = args[0];
        Code code = codeManager.getCode(codeName);

        if (code == null) {
            String msg = messages.getString("codes.nie-istnieje", "§cTen kod nie istnieje!");
            player.sendMessage(SagamcCore.color(msg));
            return true;
        }

        if (codeManager.hasUsedCode(player.getUniqueId(), codeName)) {
            String msg = messages.getString("codes.juz-uzyty", "§cWykorzystałeś już ten kod!");
            player.sendMessage(SagamcCore.color(msg));
            return true;
        }

        if (!codeManager.hasRequiredPlaytime(player, code.getRequiredTimeSeconds())) {
            long remaining = codeManager.getRemainingTime(player, code.getRequiredTimeSeconds());
            String timeStr = Code.formatTime(remaining);
            String msg = messages.getString("codes.brak-czasu", "§cMusisz grać jeszcze %czas%!")
                    .replace("%czas%", timeStr);
            player.sendMessage(SagamcCore.color(msg));
            return true;
        }

        codeManager.executeCode(player, code);

        String msg = messages.getString("codes.sukces", "§aPomyślnie użyto kodu %kod%!")
                .replace("%kod%", code.getName());
        player.sendMessage(SagamcCore.color(msg));

        return true;
    }
}