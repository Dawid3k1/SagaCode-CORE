package org.sagamc.sagamcCore.lottery;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.sagamc.sagamcCore.util.ColorUtil;

import java.util.ArrayList;
import java.util.List;

public class LotteryGUI {

    public static void openLotteryGUI(Player player, LotteryManager lotteryManager, FileConfiguration messages) {
        String title = ColorUtil.miniToLegacy(messages.getString("lottery.gui-title", "🎰 Loteria 🎰"));
        Inventory inv = Bukkit.createInventory(null, 27, title);

        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta paneMeta = pane.getItemMeta();
        paneMeta.setDisplayName(" ");
        pane.setItemMeta(paneMeta);

        for (int i = 0; i < 27; i++) {
            if (i != 11 && i != 13 && i != 15) {
                inv.setItem(i, pane);
            }
        }

        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = info.getItemMeta();

        String infoName = messages.getString("lottery.info-item-name", "<yellow>ℹ <gold>Informacje");
        infoMeta.setDisplayName(ColorUtil.miniToLegacy(infoName));

        List<String> infoLore = new ArrayList<>();
        for (String line : messages.getStringList("lottery.info-item-lore")) {
            String formatted = line
                    .replace("{POOL}", String.format("%.2f", lotteryManager.getCurrentPot()))
                    .replace("{COUNT}", String.valueOf(lotteryManager.getTotalTickets()));
            infoLore.add(ColorUtil.miniToLegacy(formatted));
        }

        infoMeta.setLore(infoLore);
        info.setItemMeta(infoMeta);
        inv.setItem(11, info);

        ItemStack myTickets = new ItemStack(Material.PAPER);
        ItemMeta myTicketsMeta = myTickets.getItemMeta();

        String ticketsName = messages.getString("lottery.your-tickets-name", "<green>🎫 <dark_green>Twoje losy");
        myTicketsMeta.setDisplayName(ColorUtil.miniToLegacy(ticketsName));

        List<String> myTicketsLore = new ArrayList<>();
        List<Integer> playerNumbers = lotteryManager.getPlayerTickets(player.getUniqueId());

        if (playerNumbers.isEmpty()) {
            for (String line : messages.getStringList("lottery.your-tickets-empty")) {
                myTicketsLore.add(ColorUtil.miniToLegacy(line));
            }
        } else {
            String numbersStr = lotteryManager.getPlayerTicketsString(player.getUniqueId());
            for (String line : messages.getStringList("lottery.your-tickets-lore")) {
                String formatted = line
                        .replace("{COUNT}", String.valueOf(playerNumbers.size()))
                        .replace("{NUMBERS}", numbersStr);
                myTicketsLore.add(ColorUtil.miniToLegacy(formatted));
            }
        }

        myTicketsMeta.setLore(myTicketsLore);
        myTickets.setItemMeta(myTicketsMeta);
        inv.setItem(13, myTickets);

        ItemStack buyTicket = new ItemStack(Material.EMERALD);
        ItemMeta buyTicketMeta = buyTicket.getItemMeta();

        String buyName = messages.getString("lottery.buy-item-name", "<green>💵 <dark_green>Kup los");
        buyTicketMeta.setDisplayName(ColorUtil.miniToLegacy(buyName));

        List<String> buyTicketLore = new ArrayList<>();
        for (String line : messages.getStringList("lottery.buy-item-lore")) {
            buyTicketLore.add(ColorUtil.miniToLegacy(line));
        }

        buyTicketMeta.setLore(buyTicketLore);
        buyTicket.setItemMeta(buyTicketMeta);
        inv.setItem(15, buyTicket);

        player.openInventory(inv);
    }
}
