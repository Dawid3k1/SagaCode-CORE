package org.sagamc.sagamcCore.stats;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.sagamc.sagamcCore.util.ColorUtil;

public class StatsCommand implements CommandExecutor {

    private final StatsManager statsManager;
    private final Economy economy;
    private final FileConfiguration messages;

    public StatsCommand(StatsManager statsManager, Economy economy, FileConfiguration messages) {
        this.statsManager = statsManager;
        this.economy = economy;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player viewer = (sender instanceof Player) ? (Player) sender : null;
        Player target;

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                String msg = messages.getString("stats.usage", "<red>Użycie: <dark_red>/stats [gracz]");
                sender.sendMessage(ColorUtil.miniMessage(msg));
                return true;
            }
            target = (Player) sender;
        } else {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                if (!offlinePlayer.hasPlayedBefore()) {
                    String msg = messages.getString("stats.player-not-found", "<red>Gracz <dark_red>{PLAYER} <red>nie został znaleziony!")
                            .replace("{PLAYER}", args[0]);
                    sender.sendMessage(ColorUtil.miniMessage(msg));
                    return true;
                }
                showOfflineStats(sender, offlinePlayer);
                return true;
            }
        }

        if (viewer != null) {
            StatsGUI.openStatsGUI(viewer, target, statsManager, economy, messages);
        } else {
            showStats(sender, target);
        }

        return true;
    }

    private void showStats(CommandSender sender, Player player) {
        PlayerStats stats = statsManager.getStats(player.getUniqueId());
        double money = economy.getBalance(player);

        String header = messages.getString("stats.header", "");
        sender.sendMessage(ColorUtil.miniMessage(header));

        String playerMsg = messages.getString("stats.player", "")
                .replace("{PLAYER}", player.getName());
        sender.sendMessage(ColorUtil.miniMessage(playerMsg));

        String playtimeMsg = messages.getString("stats.playtime", "")
                .replace("{TIME}", stats.getFormattedPlayTime());
        sender.sendMessage(ColorUtil.miniMessage(playtimeMsg));

        String moneyMsg = messages.getString("stats.money", "")
                .replace("{MONEY}", String.format("%.2f", money));
        sender.sendMessage(ColorUtil.miniMessage(moneyMsg));

        String killsMsg = messages.getString("stats.kills", "")
                .replace("{KILLS}", String.valueOf(stats.getKills()));
        sender.sendMessage(ColorUtil.miniMessage(killsMsg));

        String deathsMsg = messages.getString("stats.deaths", "")
                .replace("{DEATHS}", String.valueOf(stats.getDeaths()));
        sender.sendMessage(ColorUtil.miniMessage(deathsMsg));

        String kdrMsg = messages.getString("stats.kdr", "")
                .replace("{KDR}", String.format("%.2f", stats.getKDR()));
        sender.sendMessage(ColorUtil.miniMessage(kdrMsg));

        String blocksMsg = messages.getString("stats.blocks-broken", "")
                .replace("{BLOCKS}", String.valueOf(stats.getBlocksBroken()));
        sender.sendMessage(ColorUtil.miniMessage(blocksMsg));

        String footer = messages.getString("stats.footer", "");
        sender.sendMessage(ColorUtil.miniMessage(footer));
    }

    private void showOfflineStats(CommandSender sender, OfflinePlayer player) {
        PlayerStats stats = statsManager.getStats(player.getUniqueId());
        double money = economy.getBalance(player);

        String header = messages.getString("stats.header", "");
        sender.sendMessage(ColorUtil.miniMessage(header));

        String playerMsg = messages.getString("stats.player", "")
                .replace("{PLAYER}", player.getName());
        sender.sendMessage(ColorUtil.miniMessage(playerMsg));

        String playtimeMsg = messages.getString("stats.playtime", "")
                .replace("{TIME}", stats.getFormattedPlayTime());
        sender.sendMessage(ColorUtil.miniMessage(playtimeMsg));

        String moneyMsg = messages.getString("stats.money", "")
                .replace("{MONEY}", String.format("%.2f", money));
        sender.sendMessage(ColorUtil.miniMessage(moneyMsg));

        String killsMsg = messages.getString("stats.kills", "")
                .replace("{KILLS}", String.valueOf(stats.getKills()));
        sender.sendMessage(ColorUtil.miniMessage(killsMsg));

        String deathsMsg = messages.getString("stats.deaths", "")
                .replace("{DEATHS}", String.valueOf(stats.getDeaths()));
        sender.sendMessage(ColorUtil.miniMessage(deathsMsg));

        String kdrMsg = messages.getString("stats.kdr", "")
                .replace("{KDR}", String.format("%.2f", stats.getKDR()));
        sender.sendMessage(ColorUtil.miniMessage(kdrMsg));

        String blocksMsg = messages.getString("stats.blocks-broken", "")
                .replace("{BLOCKS}", String.valueOf(stats.getBlocksBroken()));
        sender.sendMessage(ColorUtil.miniMessage(blocksMsg));

        String footer = messages.getString("stats.footer", "");
        sender.sendMessage(ColorUtil.miniMessage(footer));
    }
}