package org.sagamc.sagamcCore.stats;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.sagamc.sagamcCore.util.ColorUtil;

import java.util.ArrayList;
import java.util.List;

public class StatsGUI {

    public static void openStatsGUI(Player viewer, Player target, StatsManager statsManager, Economy economy, FileConfiguration messages) {
        String title = ColorUtil.miniToLegacy(messages.getString("stats.gui-title", "Statystyki"));
        Inventory inv = Bukkit.createInventory(null, 27, title);

        PlayerStats stats = statsManager.getStats(target.getUniqueId());
        double money = economy.getBalance(target);

        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta paneMeta = pane.getItemMeta();
        paneMeta.setDisplayName(" ");
        pane.setItemMeta(paneMeta);

        for (int i = 0; i < 27; i++) {
            if (i != 4 && i != 11 && i != 12 && i != 14 && i != 15) {
                inv.setItem(i, pane);
            }
        }

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setOwningPlayer(target);

        String headName = messages.getString("stats.player-head-name", "<gradient:#97CFF5:#BAE0FA:#97CFF5>{PLAYER}</gradient>")
                .replace("{PLAYER}", target.getName());
        headMeta.setDisplayName(ColorUtil.miniToLegacy(headName));

        List<String> headLore = new ArrayList<>();
        for (String line : messages.getStringList("stats.player-head-lore")) {
            String formatted = line
                    .replace("{TIME}", stats.getFormattedPlayTime())
                    .replace("{MONEY}", String.format("%.2f", money));
            headLore.add(ColorUtil.miniToLegacy(formatted));
        }
        headMeta.setLore(headLore);
        head.setItemMeta(headMeta);
        inv.setItem(4, head);

        ItemStack kills = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta killsMeta = kills.getItemMeta();
        killsMeta.setDisplayName(ColorUtil.miniToLegacy(messages.getString("stats.kills-item-name", "<red>⚔ <dark_red>Zabójstwa")));

        List<String> killsLore = new ArrayList<>();
        for (String line : messages.getStringList("stats.kills-item-lore")) {
            String formatted = line.replace("{KILLS}", String.valueOf(stats.getKills()));
            killsLore.add(ColorUtil.miniToLegacy(formatted));
        }
        killsMeta.setLore(killsLore);
        kills.setItemMeta(killsMeta);
        inv.setItem(11, kills);

        ItemStack deaths = new ItemStack(Material.SKELETON_SKULL);
        ItemMeta deathsMeta = deaths.getItemMeta();
        deathsMeta.setDisplayName(ColorUtil.miniToLegacy(messages.getString("stats.deaths-item-name", "<dark_red>☠ <red>Śmierci")));

        List<String> deathsLore = new ArrayList<>();
        for (String line : messages.getStringList("stats.deaths-item-lore")) {
            String formatted = line.replace("{DEATHS}", String.valueOf(stats.getDeaths()));
            deathsLore.add(ColorUtil.miniToLegacy(formatted));
        }
        deathsMeta.setLore(deathsLore);
        deaths.setItemMeta(deathsMeta);
        inv.setItem(12, deaths);

        ItemStack kdr = new ItemStack(Material.BOOK);
        ItemMeta kdrMeta = kdr.getItemMeta();
        kdrMeta.setDisplayName(ColorUtil.miniToLegacy(messages.getString("stats.kdr-item-name", "<yellow>📊 <gold>K/D Ratio")));

        List<String> kdrLore = new ArrayList<>();
        for (String line : messages.getStringList("stats.kdr-item-lore")) {
            String formatted = line.replace("{KDR}", String.format("%.2f", stats.getKDR()));
            kdrLore.add(ColorUtil.miniToLegacy(formatted));
        }
        kdrMeta.setLore(kdrLore);
        kdr.setItemMeta(kdrMeta);
        inv.setItem(14, kdr);

        ItemStack blocks = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta blocksMeta = blocks.getItemMeta();
        blocksMeta.setDisplayName(ColorUtil.miniToLegacy(messages.getString("stats.blocks-item-name", "<gray>⛏ <dark_gray>Zniszczone bloki")));

        List<String> blocksLore = new ArrayList<>();
        for (String line : messages.getStringList("stats.blocks-item-lore")) {
            String formatted = line.replace("{BLOCKS}", String.valueOf(stats.getBlocksBroken()));
            blocksLore.add(ColorUtil.miniToLegacy(formatted));
        }
        blocksMeta.setLore(blocksLore);
        blocks.setItemMeta(blocksMeta);
        inv.setItem(15, blocks);

        viewer.openInventory(inv);
    }
}