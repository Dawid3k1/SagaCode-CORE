package org.sagamc.sagamcCore.lottery;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.sagamc.sagamcCore.util.ColorUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LotteryCommand implements CommandExecutor {

    private final LotteryManager lotteryManager;
    private final FileConfiguration messages;
    private final Map<UUID, Boolean> awaitingNumber = new HashMap<>();

    public LotteryCommand(LotteryManager lotteryManager, FileConfiguration messages) {
        this.lotteryManager = lotteryManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cMusisz być graczem!");
            return true;
        }

        if (args.length == 0) {
            LotteryGUI.openLotteryGUI(player, lotteryManager, messages);
            return true;
        }

        String subCmd = args[0].toLowerCase();

        switch (subCmd) {
            case "kup":
                if (args.length < 2) {
                    String msg = messages.getString("lottery.usage", "<red>Użycie: <dark_red>/lotto <kup/info> [numer]");
                    player.sendMessage(ColorUtil.miniMessage(msg));
                    return true;
                }

                try {
                    int number = Integer.parseInt(args[1]);
                    handleBuyTicket(player, number);
                } catch (NumberFormatException e) {
                    String msg = messages.getString("lottery.buy-invalid-number", "<red>Numer musi być w zakresie <dark_red>1-1000!");
                    player.sendMessage(ColorUtil.miniMessage(msg));
                }
                break;

            case "info":
                showInfo(player);
                break;

            default:
                String msg = messages.getString("lottery.usage", "<red>Użycie: <dark_red>/lotto <kup/info> [numer]");
                player.sendMessage(ColorUtil.miniMessage(msg));
                break;
        }

        return true;
    }

    private void handleBuyTicket(Player player, int number) {
        if (lotteryManager.isNumberTaken(number)) {
            String msg = messages.getString("lottery.buy-already-owned", "<red>Ten numer jest już <dark_red>zajęty!");
            player.sendMessage(ColorUtil.miniMessage(msg));
            return;
        }

        if (lotteryManager.buyTicket(player, number)) {
            String msg = messages.getString("lottery.buy-success", "<green>Kupiono los <dark_green>#{NUMBER} <green>za <dark_green>$100<green>!")
                    .replace("{NUMBER}", String.valueOf(number));
            player.sendMessage(ColorUtil.miniMessage(msg));
        } else {
            String msg = messages.getString("lottery.buy-no-money", "<red>Nie masz <dark_red>$100 <red>na zakup losu!");
            player.sendMessage(ColorUtil.miniMessage(msg));
        }
    }

    private void showInfo(Player player) {
        String poolMsg = messages.getString("lottery.info-pool", "<green>Aktualna pula nagród: <dark_green>${POOL}")
                .replace("{POOL}", String.format("%.2f", lotteryManager.getCurrentPot()));
        player.sendMessage(ColorUtil.miniMessage(poolMsg));

        String ticketsMsg = messages.getString("lottery.info-tickets", "<green>Ilość kupionych losów: <dark_green>{COUNT}")
                .replace("{COUNT}", String.valueOf(lotteryManager.getTotalTickets()));
        player.sendMessage(ColorUtil.miniMessage(ticketsMsg));

        String myNumbers = lotteryManager.getPlayerTicketsString(player.getUniqueId());
        if (!myNumbers.isEmpty()) {
            String myTicketsMsg = messages.getString("lottery.info-your-tickets", "<green>Twoje losy: <dark_green>{NUMBERS}")
                    .replace("{NUMBERS}", myNumbers);
            player.sendMessage(ColorUtil.miniMessage(myTicketsMsg));
        } else {
            String msg = messages.getString("lottery.info-no-tickets", "<red>Nie masz żadnych <dark_red>losów!");
            player.sendMessage(ColorUtil.miniMessage(msg));
        }
    }

    public boolean isAwaitingNumber(UUID uuid) {
        return awaitingNumber.getOrDefault(uuid, false);
    }

    public void setAwaitingNumber(UUID uuid, boolean waiting) {
        if (waiting) {
            awaitingNumber.put(uuid, true);
        } else {
            awaitingNumber.remove(uuid);
        }
    }
}
