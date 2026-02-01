package org.sagamc.sagamcCore.lottery;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.sagamc.sagamcCore.util.ColorUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class LotteryManager {

    private final JavaPlugin plugin;
    private final Economy economy;
    private final FileConfiguration config;
    private final FileConfiguration messages;
    private File lotteryFile;
    private FileConfiguration lotteryData;

    private double currentPot = 0.0;
    private Map<Integer, UUID> tickets = new HashMap<>();
    private boolean lotteryActive = true;

    private int minNumber;
    private int maxNumber;
    private double ticketPrice;

    public LotteryManager(JavaPlugin plugin, Economy economy, FileConfiguration config, FileConfiguration messages) {
        this.plugin = plugin;
        this.economy = economy;
        this.config = config;
        this.messages = messages;

        this.minNumber = config.getInt("lottery.minNumber", 1);
        this.maxNumber = config.getInt("lottery.maxNumber", 1000);
        this.ticketPrice = config.getDouble("lottery.ticketPrice", 100.0);

        loadLottery();
    }

    private void loadLottery() {
        lotteryFile = new File(plugin.getDataFolder(), "lottery.yml");
        if (!lotteryFile.exists()) {
            try {
                lotteryFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        lotteryData = YamlConfiguration.loadConfiguration(lotteryFile);

        currentPot = lotteryData.getDouble("current-pot", 0.0);
        lotteryActive = lotteryData.getBoolean("active", true);

        tickets.clear();
        if (lotteryData.contains("tickets")) {
            ConfigurationSection section = lotteryData.getConfigurationSection("tickets");
            if (section != null) {
                for (String key : section.getKeys(false)) {
                    int number = Integer.parseInt(key);
                    UUID uuid = UUID.fromString(section.getString(key));
                    tickets.put(number, uuid);
                }
            }
        }
    }

    public void saveLottery() {
        lotteryData.set("current-pot", currentPot);
        lotteryData.set("active", lotteryActive);

        lotteryData.set("tickets", null);
        for (Map.Entry<Integer, UUID> entry : tickets.entrySet()) {
            lotteryData.set("tickets." + entry.getKey(), entry.getValue().toString());
        }

        try {
            lotteryData.save(lotteryFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean buyTicket(Player player, int number) {
        if (!lotteryActive) return false;

        if (number < minNumber || number > maxNumber) {
            return false;
        }

        if (tickets.containsKey(number)) {
            return false;
        }

        if (economy.getBalance(player) < ticketPrice) {
            return false;
        }

        economy.withdrawPlayer(player, ticketPrice);
        currentPot += ticketPrice;
        tickets.put(number, player.getUniqueId());
        saveLottery();
        return true;
    }

    public boolean isNumberTaken(int number) {
        return tickets.containsKey(number);
    }

    public void startLottery() {
        lotteryActive = true;
        currentPot = 0;
        tickets.clear();
        saveLottery();
    }

    public void drawWinner() {
        if (tickets.isEmpty()) {
            String msg = messages.getString("lottery.draw-no-tickets", "<red>Nikt nie kupił losów, <dark_red>losowanie anulowane!");
            Bukkit.broadcast(ColorUtil.miniMessage(msg));
            return;
        }

        // Losuj numer
        List<Integer> allNumbers = new ArrayList<>(tickets.keySet());
        int winningNumber = allNumbers.get(new Random().nextInt(allNumbers.size()));
        UUID winnerUUID = tickets.get(winningNumber);

        Player winner = Bukkit.getPlayer(winnerUUID);
        String winnerName = winner != null ? winner.getName() : Bukkit.getOfflinePlayer(winnerUUID).getName();

        if (winner != null) {
            economy.depositPlayer(winner, currentPot);
        } else {
            economy.depositPlayer(Bukkit.getOfflinePlayer(winnerUUID), currentPot);
        }

        String msg = messages.getString("lottery.draw-winner", "")
                .replace("{NUMBER}", String.valueOf(winningNumber))
                .replace("{WINNER}", winnerName)
                .replace("{PRIZE}", String.format("%.2f", currentPot));

        Bukkit.broadcast(ColorUtil.miniMessage(msg));

        currentPot = 0;
        tickets.clear();
        saveLottery();
    }

    public double getTicketPrice() { return ticketPrice; }
    public double getCurrentPot() { return currentPot; }
    public int getTotalTickets() { return tickets.size(); }
    public boolean isActive() { return lotteryActive; }

    public List<Integer> getPlayerTickets(UUID uuid) {
        return tickets.entrySet().stream()
                .filter(entry -> entry.getValue().equals(uuid))
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    public String getPlayerTicketsString(UUID uuid) {
        List<Integer> numbers = getPlayerTickets(uuid);
        if (numbers.isEmpty()) return "";

        return numbers.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }
}
