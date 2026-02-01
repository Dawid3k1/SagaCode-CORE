package org.sagamc.sagamcCore.warp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.sagamc.sagamcCore.util.ColorUtil;

import java.util.ArrayList;
import java.util.List;

public class WarpGUI {

    public static void openWarpsGUI(Player player, WarpManager warpManager, FileConfiguration messages) {
        String title = ColorUtil.miniToLegacy(messages.getString("warp.gui-title", "Warpy"));
        Inventory inv = Bukkit.createInventory(null, 54, title);

        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta paneMeta = pane.getItemMeta();
        paneMeta.setDisplayName(" ");
        pane.setItemMeta(paneMeta);

        for (int i = 0; i < 9; i++) inv.setItem(i, pane);
        for (int i = 45; i < 54; i++) inv.setItem(i, pane);

        List<Warp> warps = warpManager.getAllWarps();
        int slot = 10;

        for (Warp warp : warps) {
            if (slot >= 44) break;

            ItemStack item = new ItemStack(warp.getIcon());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ColorUtil.miniToLegacy("<gradient:#97CFF5:#BAE0FA:#97CFF5>" + warp.getName() + "</gradient>"));

            Location loc = warp.getLocation();
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ColorUtil.miniToLegacy("<gray>Świat: <white>" + loc.getWorld().getName()));
            lore.add(ColorUtil.miniToLegacy("<gray>Pozycja: <white>" + (int)loc.getX() + ", " + (int)loc.getY() + ", " + (int)loc.getZ()));
            lore.add("");
            lore.add(ColorUtil.miniToLegacy("<green>Kliknij aby się teleportować!"));

            meta.setLore(lore);
            item.setItemMeta(meta);

            inv.setItem(slot, item);
            slot++;
            if (slot % 9 == 8) slot += 2;
        }

        player.openInventory(inv);
    }
}