package org.sagamc.sagamcCore.lottery;

import org.bukkit.scheduler.BukkitRunnable;

public class LotteryDrawTask extends BukkitRunnable {

    private final LotteryManager lotteryManager;

    public LotteryDrawTask(LotteryManager lotteryManager) {
        this.lotteryManager = lotteryManager;
    }

    @Override
    public void run() {
        lotteryManager.drawWinner();
    }
}