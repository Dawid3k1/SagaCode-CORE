package org.sagamc.sagamcCore.commands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.sagamc.sagamcCore.util.ColorUtil;

import java.util.*;
import java.util.stream.Collectors;

public class TopMoneyCommand implements CommandExecutor {

    private final Economy economy;
    private final FileConfiguration messages;

    public TopMoneyCommand(Economy economy, FileConfiguration messages) {
        this.economy = economy;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<PlayerBalance> balances = new ArrayList<>();

        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if (player.getName() != null && player.hasPlayedBefore()) {
                double balance = economy.getBalance(player);
                if (balance > 0) {
                    balances.add(new PlayerBalance(player.getName(), balance));
                }
            }
        }

        balances.sort((a, b) -> Double.compare(b.balance, a.balance));

        List<PlayerBalance> top10 = balances.stream()
                .limit(10)
                .collect(Collectors.toList());

        if (top10.isEmpty()) {
            String msg = messages.getString("topmoney.empty", "<red>Nie znaleziono żadnych <dark_red>graczy!");
            sender.sendMessage(ColorUtil.miniMessage(msg));
            return true;
        }

        String header = messages.getString("topmoney.header", "");
        if (!header.isEmpty()) {
            sender.sendMessage(ColorUtil.miniMessage(header));
        }

        for (int i = 0; i < top10.size(); i++) {
            PlayerBalance pb = top10.get(i);

            String line = messages.getString("topmoney.entry", "<white>{POSITION}. <gradient:#97CFF5:#BAE0FA:#97CFF5>{PLAYER}</gradient> <dark_gray>- <gradient:#6DFF45:#9DFF83:#6DFF45>${MONEY}</gradient>")
                    .replace("{POSITION}", String.valueOf(i + 1))
                    .replace("{PLAYER}", pb.playerName)
                    .replace("{MONEY}", String.format("%.2f", pb.balance));

            sender.sendMessage(ColorUtil.miniMessage(line));
        }

        String footer = messages.getString("topmoney.footer", "");
        if (!footer.isEmpty()) {
            sender.sendMessage(ColorUtil.miniMessage(footer));
        }

        return true;
    }

    private static class PlayerBalance {
        String playerName;
        double balance;

        PlayerBalance(String playerName, double balance) {
            this.playerName = playerName;
            this.balance = balance;
        }
    }
}