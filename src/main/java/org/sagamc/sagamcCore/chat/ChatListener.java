package org.sagamc.sagamcCore.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.sagamc.sagamcCore.SagamcCore;

public class ChatListener implements Listener {

    private final ChatManager chatManager;
    private final FileConfiguration messages;

    public ChatListener(ChatManager chatManager, FileConfiguration messages) {
        this.chatManager = chatManager;
        this.messages = messages;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        if (!chatManager.isChatEnabled() && !player.hasPermission("sagamc.chat.bypass")) {
            event.setCancelled(true);
            String msg = messages.getString("chat.wylaczony", "§cChat jest wyłączony!");
            player.sendMessage(SagamcCore.color(msg));
            return;
        }

        if (!chatManager.canSendMessage(player)) {
            event.setCancelled(true);
            int remaining = chatManager.getRemainingCooldown(player);
            String msg = messages.getString("chat.cooldown", "§cZwolnij! Kolejną wiadomość możesz wysłać za %czas%s")
                    .replace("%czas%", String.valueOf(remaining));
            player.sendMessage(SagamcCore.color(msg));
            return;
        }

        chatManager.updateLastMessageTime(player);
    }
}