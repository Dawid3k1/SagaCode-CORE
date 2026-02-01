package org.sagamc.sagamcCore.shop;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.sagamc.sagamcCore.SagamcCore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShopListener implements Listener {

    private final Economy economy;
    private final ShopConfig config;
    private final FileConfiguration messages;
    private final Map<UUID, ItemStack> pendingPurchases = new HashMap<>();
    private final Map<UUID, Integer> pendingPrices = new HashMap<>();
    private final Map<UUID, Integer> pendingAmounts = new HashMap<>();

    public ShopListener(Economy economy, ShopConfig config, FileConfiguration messages) {
        this.economy = economy;
        this.config = config;
        this.messages = messages;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();
        String shopTitle = ChatColor.translateAlternateColorCodes('&', config.getShopTitle());
        String confirmTitle = ChatColor.translateAlternateColorCodes('&', config.getConfirmationTitle());

        if (title.equals(shopTitle)) {
            event.setCancelled(true);

            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;

            if (clicked.getType() == Material.BARRIER) {
                player.closeInventory();
                return;
            }

            if (clicked.getType().toString().contains("STAINED_GLASS_PANE")) {
                return;
            }

            int price = config.getPriceForMaterial(clicked.getType());
            if (price == 0) return;

            pendingPurchases.put(player.getUniqueId(), clicked.clone());
            pendingPrices.put(player.getUniqueId(), price);
            pendingAmounts.put(player.getUniqueId(), 1);
            ShopGUI.openConfirmation(player, clicked, price, 1);
        }

        else if (title.equals(confirmTitle)) {
            event.setCancelled(true);

            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;

            UUID uuid = player.getUniqueId();
            ItemStack itemToBuy = pendingPurchases.get(uuid);
            Integer price = pendingPrices.get(uuid);
            Integer currentAmount = pendingAmounts.getOrDefault(uuid, 1);

            if (itemToBuy == null || price == null) return;

            if (clicked.getType() == Material.RED_STAINED_GLASS_PANE) {
                pendingPurchases.remove(uuid);
                pendingPrices.remove(uuid);
                pendingAmounts.remove(uuid);
                ShopGUI.openShop(player);

                String msg = messages.getString("shop.zakup-anulowany", "§cZakup anulowany.");
                player.sendMessage(SagamcCore.color(msg));
                return;
            }

            if (clicked.getType() == Material.GREEN_STAINED_GLASS_PANE) {
                int totalPrice = price * currentAmount;

                if (!economy.has(player, totalPrice)) {
                    String msg = messages.getString("shop.brak-pieniedzy", "§cBrak pieniędzy!")
                            .replace("%cena%", String.valueOf(totalPrice));
                    player.sendMessage(SagamcCore.color(msg));
                    return;
                }

                economy.withdrawPlayer(player, totalPrice);

                ItemStack itemToGive = new ItemStack(itemToBuy.getType(), currentAmount);
                player.getInventory().addItem(itemToGive);

                String msg = messages.getString("shop.zakup-sukces", "§aZakupiono!")
                        .replace("%ilosc%", String.valueOf(currentAmount))
                        .replace("%cena%", String.valueOf(totalPrice));
                player.sendMessage(SagamcCore.color(msg));

                pendingPurchases.remove(uuid);
                pendingPrices.remove(uuid);
                pendingAmounts.remove(uuid);
                player.closeInventory();
                return;
            }

            if (clicked.getType() == Material.PLAYER_HEAD && clicked.getItemMeta().getDisplayName().contains("+")) {
                int toAdd = 1;

                if (event.getClick() == ClickType.RIGHT) {
                    toAdd = 5;
                } else if (event.getClick() == ClickType.SHIFT_LEFT) {
                    toAdd = 10;
                } else if (event.getClick() == ClickType.SHIFT_RIGHT) {
                    toAdd = 64;
                }

                int newAmount = Math.min(currentAmount + toAdd, 64);
                pendingAmounts.put(uuid, newAmount);
                ShopGUI.openConfirmation(player, itemToBuy, price, newAmount);
                return;
            }

            if (clicked.getType() == Material.PLAYER_HEAD && clicked.getItemMeta().getDisplayName().contains("-")) {
                int toRemove = 1;

                if (event.getClick() == ClickType.RIGHT) {
                    toRemove = 5;
                } else if (event.getClick() == ClickType.SHIFT_LEFT) {
                    toRemove = 10;
                } else if (event.getClick() == ClickType.SHIFT_RIGHT) {
                    toRemove = 64;
                }

                int newAmount = Math.max(currentAmount - toRemove, 1);
                pendingAmounts.put(uuid, newAmount);
                ShopGUI.openConfirmation(player, itemToBuy, price, newAmount);
                return;
            }
        }
    }
}