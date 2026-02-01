package org.sagamc.sagamcCore.teleport;

import org.bukkit.entity.Player;

public class TeleportRequest {
    private final Player requester;
    private final Player target;
    private final long timestamp;

    public TeleportRequest(Player requester, Player target) {
        this.requester = requester;
        this.target = target;
        this.timestamp = System.currentTimeMillis();
    }

    public Player getRequester() {
        return requester;
    }

    public Player getTarget() {
        return target;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isExpired(int expireSeconds) {
        long currentTime = System.currentTimeMillis();
        long elapsed = (currentTime - timestamp) / 1000;
        return elapsed >= expireSeconds;
    }
}