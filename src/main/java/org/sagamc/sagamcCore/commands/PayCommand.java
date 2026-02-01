package org.sagamc.sagamcCore.commands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.sagamc.sagamcCore.util.ColorUtil;

public class PayCommand implements CommandExecutor {

    private final Economy economy;
    private final FileConfiguration messages;
    private static final double TAX_PERCENTAGE = 0.05; // 5% podatek

    public PayCommand(Economy economy, FileConfiguration messages) {
        this.economy = economy;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cMusisz być graczem!");
            return true;
        }

        if (args.length < 2) {
            String msg = messages.getString("msgPayUsage", "<red>Użycie: <dark_red>/pay [gracz] [ilość]");
            player.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null || !target.isOnline()) {
            String msg = messages.getString("msgPlayerOffline", "<red>Ten gracz jest <dark_red>offline!");
            player.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        if (target.equals(player)) {
            String msg = messages.getString("msgPaySelf", "<red>Ale co ty robisz?");
            player.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            String msg = messages.getString("msgPayUsage", "<red>Użycie: <dark_red>/pay [gracz] [ilość]");
            player.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        if (amount < 100) {
            String msg = messages.getString("msgPayMin", "<red>Nie możesz przelać mniej niż <dark_red>$100");
            player.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        double tax = amount * TAX_PERCENTAGE;
        double totalCost = amount + tax;

        if (!economy.has(player, totalCost)) {
            String msg = messages.getString("msgNoMoney", "<red>Nie posiadasz tyle <dark_red>pieniędzy!");
            player.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        economy.withdrawPlayer(player, totalCost);
        economy.depositPlayer(target, amount);

        String sentMsg = messages.getString("msgPaySent",
                        "<green>Przelano <dark_green>${AMOUNT} <green>dla gracza <dark_green>{PLAYER} <dark_gray>(<red>Podatek: ${TAX}<dark_gray>)")
                .replace("{AMOUNT}", String.format("%.2f", amount))
                .replace("{PLAYER}", target.getName())
                .replace("{TAX}", String.format("%.2f", tax));
        player.sendMessage(ColorUtil.miniMessage(sentMsg));

        String receivedMsg = messages.getString("msgPayReceived",
                        "<green>Otrzymałeś <dark_green>${AMOUNT} <green>od gracza <dark_green>{PLAYER}")
                .replace("{AMOUNT}", String.format("%.2f", amount))
                .replace("{PLAYER}", player.getName());
        target.sendMessage(ColorUtil.miniMessage(receivedMsg));

        return true;
    }
}