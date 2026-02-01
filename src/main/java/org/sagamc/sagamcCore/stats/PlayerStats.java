package org.sagamc.sagamcCore.stats;

import java.util.UUID;

public class PlayerStats {
    private final UUID uuid;
    private long playTime;
    private int kills;
    private int deaths;
    private int blocksBroken;

    public PlayerStats(UUID uuid) {
        this.uuid = uuid;
        this.playTime = 0;
        this.kills = 0;
        this.deaths = 0;
        this.blocksBroken = 0;
    }

    public PlayerStats(UUID uuid, long playTime, int kills, int deaths, int blocksBroken) {
        this.uuid = uuid;
        this.playTime = playTime;
        this.kills = kills;
        this.deaths = deaths;
        this.blocksBroken = blocksBroken;
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getPlayTime() {
        return playTime;
    }

    public void setPlayTime(long playTime) {
        this.playTime = playTime;
    }

    public void addPlayTime(long seconds) {
        this.playTime += seconds;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void addKill() {
        this.kills++;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void addDeath() {
        this.deaths++;
    }

    public int getBlocksBroken() {
        return blocksBroken;
    }

    public void setBlocksBroken(int blocksBroken) {
        this.blocksBroken = blocksBroken;
    }

    public void addBlockBroken() {
        this.blocksBroken++;
    }

    public double getKDR() {
        if (deaths == 0) return kills;
        return (double) kills / deaths;
    }

    public String getFormattedPlayTime() {
        long hours = playTime / 3600;
        long minutes = (playTime % 3600) / 60;
        long seconds = playTime % 60;

        if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else if (minutes > 0) {
            return minutes + "m " + seconds + "s";
        } else {
            return seconds + "s";
        }
    }
}