package org.sagamc.sagamcCore.lottery;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.sagamc.sagamcCore.util.ColorUtil;

public class LotteryAdminCommand implements CommandExecutor {

    private final LotteryManager lotteryManager;
    private final FileConfiguration messages;

    public LotteryAdminCommand(LotteryManager lotteryManager, FileConfiguration messages) {
        this.lotteryManager = lotteryManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sagamc.lottery.admin")) {
            String msg = messages.getString("brak-permisji", "<red>Brak permisji!");
            sender.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ColorUtil.miniMessage("<red>Użycie: <dark_red>/lottoadmin <losuj/reset/info>"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "losuj":
            case "draw":
                lotteryManager.drawWinner();
                sender.sendMessage(ColorUtil.miniMessage("<green>Losowanie <dark_green>wykonano!"));
                break;

            case "reset":
                lotteryManager.startLottery();
                sender.sendMessage(ColorUtil.miniMessage("<green>Loteria została <dark_green>zresetowana!"));
                break;

            case "info":
                sender.sendMessage(ColorUtil.miniMessage("<green>Pula: <dark_green>$" + String.format("%.2f", lotteryManager.getCurrentPot())));
                sender.sendMessage(ColorUtil.miniMessage("<green>Sprzedanych losów: <dark_green>" + lotteryManager.getTotalTickets()));
                break;

            default:
                sender.sendMessage(ColorUtil.miniMessage("<red>Użycie: <dark_red>/lottoadmin <losuj/reset/info>"));
                break;
        }

        return true;
    }
}