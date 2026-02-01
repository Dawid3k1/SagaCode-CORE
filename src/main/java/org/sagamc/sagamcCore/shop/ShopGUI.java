package org.sagamc.sagamcCore.shop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.sagamc.sagamcCore.SagamcCore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShopGUI {

    private static ShopConfig config;

    public static void setConfig(ShopConfig shopConfig) {
        config = shopConfig;
    }

    public static void openShop(Player player) {
        String title = ChatColor.translateAlternateColorCodes('&', config.getShopTitle());
        Inventory inv = Bukkit.createInventory(null, 54, title);

        ItemStack bluePane = createPane(Material.BLUE_STAINED_GLASS_PANE);
        ItemStack lightBluePane = createPane(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemStack whitePane = createPane(Material.WHITE_STAINED_GLASS_PANE);
        inv.setItem(0, bluePane);
        inv.setItem(1, lightBluePane);
        inv.setItem(2, whitePane);
        inv.setItem(3, whitePane);
        inv.setItem(5, whitePane);
        inv.setItem(6, whitePane);
        inv.setItem(7, lightBluePane);
        inv.setItem(8, bluePane);
        inv.setItem(9, lightBluePane);
        inv.setItem(17, lightBluePane);
        inv.setItem(18, whitePane);
        inv.setItem(26, whitePane);
        inv.setItem(27, whitePane);
        inv.setItem(35, whitePane);
        inv.setItem(36, bluePane);
        inv.setItem(44, bluePane);
        inv.setItem(45, bluePane);
        inv.setItem(46, lightBluePane);
        inv.setItem(47, whitePane);
        inv.setItem(48, whitePane);
        inv.setItem(50, whitePane);
        inv.setItem(51, whitePane);
        inv.setItem(52, lightBluePane);
        inv.setItem(53, bluePane);

        for (ShopConfig.ShopItem shopItem : config.getItems()) {
            ItemStack item = new ItemStack(shopItem.getMaterial());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', shopItem.getName()));

            List<String> lore = new ArrayList<>(shopItem.getLore());
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Cena: &a" + shopItem.getPrice() + "$"));
            meta.setLore(lore.stream()
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                    .toList());

            item.setItemMeta(meta);
            inv.setItem(shopItem.getSlot(), item);
        }

        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta barrierMeta = barrier.getItemMeta();
        barrierMeta.setDisplayName("§cWyjdź");
        barrier.setItemMeta(barrierMeta);
        inv.setItem(49, barrier);

        player.openInventory(inv);
    }

    public static void openConfirmation(Player player, ItemStack item, int price, int amount) {
        String title = ChatColor.translateAlternateColorCodes('&', config.getConfirmationTitle());
        Inventory inv = Bukkit.createInventory(null, 54, title);

        ItemStack whitePane = createPane(Material.WHITE_STAINED_GLASS_PANE);
        ItemStack lightGrayPane = createPane(Material.LIGHT_GRAY_STAINED_GLASS_PANE);

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, lightGrayPane);
        }

        for (int i = 45; i < 54; i++) {
            inv.setItem(i, lightGrayPane);
        }

        inv.setItem(9, lightGrayPane);
        inv.setItem(17, lightGrayPane);
        inv.setItem(18, lightGrayPane);
        inv.setItem(26, lightGrayPane);
        inv.setItem(27, lightGrayPane);
        inv.setItem(35, lightGrayPane);
        inv.setItem(36, lightGrayPane);
        inv.setItem(44, lightGrayPane);

        ItemStack displayItem = item.clone();
        displayItem.setAmount(amount);
        ItemMeta displayMeta = displayItem.getItemMeta();
        displayMeta.setLore(Arrays.asList(
                "§7Cena za sztukę: §a" + price + "$",
                "§7Ilość: §e" + amount,
                "§7Suma: §a" + (price * amount) + "$"
        ));
        displayItem.setItemMeta(displayMeta);
        inv.setItem(22, displayItem);

        ItemStack addHead = new ItemStack(Material.PLAYER_HEAD);
        org.bukkit.inventory.meta.SkullMeta addMeta = (org.bukkit.inventory.meta.SkullMeta) addHead.getItemMeta();
        addMeta.setDisplayName("§a§l+ DODAJ");
        addMeta.setLore(Arrays.asList(
                "§7Lewy klik: §a+1 sztuka",
                "§7Prawy klik: §a+5 sztuk",
                "§7Shift + Lewy: §a+10 sztuk",
                "§7Shift + Prawy: §a+64 sztuki"
        ));
        addMeta.setOwner("MHF_ArrowUp");
        addHead.setItemMeta(addMeta);
        inv.setItem(20, addHead);

        ItemStack removeHead = new ItemStack(Material.PLAYER_HEAD);
        org.bukkit.inventory.meta.SkullMeta removeMeta = (org.bukkit.inventory.meta.SkullMeta) removeHead.getItemMeta();
        removeMeta.setDisplayName("§c§l- ODEJMIJ");
        removeMeta.setLore(Arrays.asList(
                "§7Lewy klik: §c-1 sztuka",
                "§7Prawy klik: §c-5 sztuk",
                "§7Shift + Lewy: §c-10 sztuk",
                "§7Shift + Prawy: §c-64 sztuki"
        ));
        removeMeta.setOwner("MHF_ArrowDown");
        removeHead.setItemMeta(removeMeta);
        inv.setItem(24, removeHead);

        ItemStack confirmButton = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        confirmMeta.setDisplayName("§a§l✔ POTWIERDŹ ZAKUP");
        confirmMeta.setLore(Arrays.asList(
                "§7Kup §e" + amount + "x §7za §a" + (price * amount) + "$"
        ));
        confirmButton.setItemMeta(confirmMeta);

        inv.setItem(10, confirmButton);
        inv.setItem(11, confirmButton);
        inv.setItem(12, confirmButton);
        inv.setItem(19, confirmButton);
        inv.setItem(28, confirmButton);
        inv.setItem(29, confirmButton);
        inv.setItem(37, confirmButton);
        inv.setItem(38, confirmButton);
        inv.setItem(39, confirmButton);

        ItemStack cancelButton = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta cancelMeta = cancelButton.getItemMeta();
        cancelMeta.setDisplayName("§c§l✘ ANULUJ");
        cancelMeta.setLore(Arrays.asList("§7Powrót do sklepu"));
        cancelButton.setItemMeta(cancelMeta);

        inv.setItem(14, cancelButton);
        inv.setItem(15, cancelButton);
        inv.setItem(16, cancelButton);
        inv.setItem(25, cancelButton);
        inv.setItem(32, cancelButton);
        inv.setItem(33, cancelButton);
        inv.setItem(41, cancelButton);
        inv.setItem(42, cancelButton);
        inv.setItem(43, cancelButton);

        player.openInventory(inv);
    }

    private static ItemStack createPane(Material material) {
        ItemStack pane = new ItemStack(material);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(" ");
        pane.setItemMeta(meta);
        return pane;
    }
}