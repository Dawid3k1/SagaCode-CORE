package org.sagamc.sagamcCore.lottery;

import java.util.UUID;

public class LotteryTicket {
    private final UUID owner;
    private final int number;

    public LotteryTicket(UUID owner, int number) {
        this.owner = owner;
        this.number = number;
    }

    public UUID getOwner() {
        return owner;
    }

    public int getNumber() {
        return number;
    }
}