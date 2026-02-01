package org.sagamc.sagamcCore.teleport;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.sagamc.sagamcCore.SagamcCore;

import java.util.List;

public class TpacceptCommand implements CommandExecutor {

    private final TeleportManager teleportManager;
    private final FileConfiguration messages;

    public TpacceptCommand(TeleportManager teleportManager, FileConfiguration messages) {
        this.teleportManager = teleportManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(SagamcCore.color(messages.getString("tylko-gracz", "§cMusisz być graczem!")));
            return true;
        }

        List<TeleportRequest> requests = teleportManager.getRequests(player);

        if (requests.isEmpty()) {
            player.sendMessage(SagamcCore.color(messages.getString("teleport.brak-prosb", "§cNie masz żadnych próśb!")));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(SagamcCore.color(messages.getString("teleport.tpaccept-usage", "§cUżycie: /tpaccept <gracz/*>")));
            return true;
        }

        if (args[0].equals("*")) {
            for (TeleportRequest request : requests) {
                Player requester = request.getRequester();
                if (requester != null && requester.isOnline()) {
                    acceptRequest(player, requester);
                }
            }
            return true;
        }

        Player requester = Bukkit.getPlayer(args[0]);

        if (requester == null || !requester.isOnline()) {
            player.sendMessage(SagamcCore.color(messages.getString("teleport.gracz-offline", "§cTen gracz jest offline!")));
            return true;
        }

        TeleportRequest request = requests.stream()
                .filter(req -> req.getRequester().equals(requester))
                .findFirst()
                .orElse(null);

        if (request == null) {
            player.sendMessage(SagamcCore.color(messages.getString("teleport.prosba-wygasla", "§cProśba od tego gracza wygasła!")));
            return true;
        }

        acceptRequest(player, requester);
        return true;
    }

    private void acceptRequest(Player target, Player requester) {
        String msgTarget = messages.getString("teleport.prosba-zaakceptowana-sender", "§aZaakceptowano prośbę od %gracz%")
                .replace("%gracz%", requester.getName());
        target.sendMessage(SagamcCore.color(msgTarget));

        String msgRequester = messages.getString("teleport.prosba-zaakceptowana-target", "§a%gracz% zaakceptował Twoją prośbę!")
                .replace("%gracz%", target.getName());
        requester.sendMessage(SagamcCore.color(msgRequester));

        String successMsg = messages.getString("teleport.tp-sukces", "§aPrzeteleportowano do %gracz%")
                .replace("%gracz%", target.getName());

        teleportManager.teleportWithCountdown(requester, target.getLocation(), successMsg);
        teleportManager.removeRequest(target, requester);
    }
}