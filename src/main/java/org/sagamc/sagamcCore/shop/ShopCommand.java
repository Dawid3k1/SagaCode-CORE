package org.sagamc.sagamcCore.shop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§x§f§f§5§5§5§5❌ <dark_gray>&8⁎ §x§f§f§5§5§5§5Ta komenda jest dostępna tylko dla graczy!");
            return true;
        }

        ShopGUI.openShop(player);
        return true;
    }
}