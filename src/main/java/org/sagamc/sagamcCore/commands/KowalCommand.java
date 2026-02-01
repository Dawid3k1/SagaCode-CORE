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

public class KowalCommand implements CommandExecutor {

    private final FileConfiguration messages;

    public KowalCommand(FileConfiguration messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cMusisz być graczem!");
            return true;
        }

        String title = ColorUtil.miniToLegacy(messages.getString("kowal.title", "Kowal"));
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

        ItemStack crafting = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta craftingMeta = crafting.getItemMeta();
        craftingMeta.setDisplayName(ColorUtil.miniToLegacy(messages.getString("kowal.crafting-name", "<yellow>⚒ <gold>Stół Rzemieślniczy")));
        List<String> craftingLore = new ArrayList<>();
        for (String line : messages.getStringList("kowal.crafting-lore")) {
            craftingLore.add(ColorUtil.miniToLegacy(line));
        }
        craftingMeta.setLore(craftingLore);
        crafting.setItemMeta(craftingMeta);
        inv.setItem(11, crafting);

        ItemStack grindstone = new ItemStack(Material.GRINDSTONE);
        ItemMeta grindstoneMeta = grindstone.getItemMeta();
        grindstoneMeta.setDisplayName(ColorUtil.miniToLegacy(messages.getString("kowal.grindstone-name", "<yellow>⚙ <gold>Szlifierka")));
        List<String> grindstoneLore = new ArrayList<>();
        for (String line : messages.getStringList("kowal.grindstone-lore")) {
            grindstoneLore.add(ColorUtil.miniToLegacy(line));
        }
        grindstoneMeta.setLore(grindstoneLore);
        grindstone.setItemMeta(grindstoneMeta);
        inv.setItem(13, grindstone);

        ItemStack anvil = new ItemStack(Material.ANVIL);
        ItemMeta anvilMeta = anvil.getItemMeta();
        anvilMeta.setDisplayName(ColorUtil.miniToLegacy(messages.getString("kowal.anvil-name", "<yellow>🔨 <gold>Kowadło")));
        List<String> anvilLore = new ArrayList<>();
        for (String line : messages.getStringList("kowal.anvil-lore")) {
            anvilLore.add(ColorUtil.miniToLegacy(line));
        }
        anvilMeta.setLore(anvilLore);
        anvil.setItemMeta(anvilMeta);
        inv.setItem(15, anvil);

        player.openInventory(inv);
        return true;
    }
}