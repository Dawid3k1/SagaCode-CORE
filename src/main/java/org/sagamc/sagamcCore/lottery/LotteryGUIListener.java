package org.sagamc.sagamcCore.lottery;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.sagamc.sagamcCore.util.ColorUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LotteryGUIListener implements Listener {

    private final LotteryManager lotteryManager;
    private final FileConfiguration messages;
    private final Map<UUID, Boolean> awaitingNumber = new HashMap<>();

    public LotteryGUIListener(LotteryManager lotteryManager, FileConfiguration messages) {
        this.lotteryManager = lotteryManager;
        this.messages = messages;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();
        String expectedTitle = ColorUtil.miniToLegacy(messages.getString("lottery.gui-title", "🎰 Loteria 🎰"));

        if (!title.equals(expectedTitle)) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        if (clicked.getType() == Material.PAPER && event.getSlot() == 11) {
            player.closeInventory();
            LotteryGUI.openLotteryGUI(player, lotteryManager, messages);
            return;
        }

        if (clicked.getType() == Material.EMERALD) {
            player.closeInventory();

            String msg = messages.getString("lottery.buy-prompt", "<green>Wpisz numer losu na chacie <dark_green>(1-1000)<green>:");
            player.sendMessage(ColorUtil.miniMessage(msg));

            awaitingNumber.put(player.getUniqueId(), true);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!awaitingNumber.getOrDefault(uuid, false)) return;

        event.setCancelled(true);
        awaitingNumber.remove(uuid);

        String message = event.getMessage();

        try {
            int number = Integer.parseInt(message);

            if (number < 1 || number > 1000) {
                String msg = messages.getString("lottery.buy-invalid-number", "<red>Numer musi być w zakresie <dark_red>1-1000!");
                player.sendMessage(ColorUtil.miniMessage(msg));
                return;
            }

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

        } catch (NumberFormatException e) {
            String msg = messages.getString("lottery.buy-cancelled", "<red>Anulowano zakup losu.");
            player.sendMessage(ColorUtil.miniMessage(msg));
        }
    }
}
