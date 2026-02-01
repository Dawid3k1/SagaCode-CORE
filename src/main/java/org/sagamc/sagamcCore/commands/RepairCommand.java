package org.sagamc.sagamcCore.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.sagamc.sagamcCore.SagamcCore;

public class RepairCommand implements CommandExecutor {

    private final FileConfiguration messages;

    public RepairCommand(FileConfiguration messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(SagamcCore.color(messages.getString("tylko-gracz", "§cMusisz być graczem!")));
            return true;
        }

        if (!player.hasPermission("sagamc.repair")) {
            sender.sendMessage(SagamcCore.color(messages.getString("brak-permisji", "§cBrak permisji!").replace("{PERM}", "sagamc.repair")));
            return true;
        }

        if (label.equalsIgnoreCase("repairall")) {
            int repaired = 0;

            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() != Material.AIR) {
                    if (repairItem(item)) {
                        repaired++;
                    }
                }
            }

            if (repaired > 0) {
                String msg = messages.getString("commands.repairall-success", "&aPrzedmioty zostały &2naprawione!");
                player.sendMessage(SagamcCore.color(msg));
            } else {
                String msg = messages.getString("commands.repair-error", "&cNie możesz tego &4naprawić!");
                player.sendMessage(SagamcCore.color(msg));
            }

            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType() == Material.AIR) {
            String msg = messages.getString("commands.repair-error", "&cNie możesz tego &4naprawić!");
            player.sendMessage(SagamcCore.color(msg));
            return true;
        }

        if (repairItem(item)) {
            String msg = messages.getString("commands.repair-success", "&aPrzedmiot został &2naprawiony!");
            player.sendMessage(SagamcCore.color(msg));
        } else {
            String msg = messages.getString("commands.repair-error", "&cNie możesz tego &4naprawić!");
            player.sendMessage(SagamcCore.color(msg));
        }

        return true;
    }

    private boolean repairItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;

        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable damageable)) return false;

        if (damageable.getDamage() > 0) {
            damageable.setDamage(0);
            item.setItemMeta(meta);
            return true;
        }

        return false;
    }
}