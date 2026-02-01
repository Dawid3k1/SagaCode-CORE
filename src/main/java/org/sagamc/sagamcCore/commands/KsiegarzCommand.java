package org.sagamc.sagamcCore.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.sagamc.sagamcCore.util.ColorUtil;

import java.util.ArrayList;
import java.util.List;

public class KsiegarzCommand implements CommandExecutor {

    private final FileConfiguration messages;

    public KsiegarzCommand(FileConfiguration messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cMusisz być graczem!");
            return true;
        }

        String title = ColorUtil.miniToLegacy(messages.getString("ksiegarz.title", "Księgarz"));
        Inventory inv = Bukkit.createInventory(null, 27, title);

        ItemStack pane = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta paneMeta = pane.getItemMeta();
        paneMeta.setDisplayName(" ");
        pane.setItemMeta(paneMeta);

        for (int i = 0; i < 27; i++) {
            if (i != 13) {
                inv.setItem(i, pane);
            }
        }

        ItemStack enchant = new ItemStack(Material.ENCHANTING_TABLE);
        ItemMeta enchantMeta = enchant.getItemMeta();
        enchantMeta.setDisplayName(ColorUtil.miniToLegacy(messages.getString("ksiegarz.enchant-name", "<light_purple>✨ <dark_purple>Stół Zaklęć")));
        List<String> enchantLore = new ArrayList<>();
        for (String line : messages.getStringList("ksiegarz.enchant-lore")) {
            enchantLore.add(ColorUtil.miniToLegacy(line));
        }
        enchantMeta.setLore(enchantLore);
        enchant.setItemMeta(enchantMeta);
        inv.setItem(13, enchant);

        player.openInventory(inv);
        return true;
    }
}