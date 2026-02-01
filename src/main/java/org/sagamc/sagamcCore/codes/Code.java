package org.sagamc.sagamcCore.codes;

import java.util.List;

public class Code {
    private final String name;
    private final long requiredTimeSeconds;
    private final List<String> commands;
    private final List<String> broadcast;

    public Code(String name, long requiredTimeSeconds, List<String> commands, List<String> broadcast) {
        this.name = name;
        this.requiredTimeSeconds = requiredTimeSeconds;
        this.commands = commands;
        this.broadcast = broadcast;
    }

    public String getName() {
        return name;
    }

    public long getRequiredTimeSeconds() {
        return requiredTimeSeconds;
    }

    public List<String> getCommands() {
        return commands;
    }

    public List<String> getBroadcast() {
        return broadcast;
    }

    public static long parseTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) return 0;

        long seconds = 0;
        String[] parts = timeStr.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

        for (int i = 0; i < parts.length - 1; i += 2) {
            try {
                int value = Integer.parseInt(parts[i]);
                String unit = parts[i + 1].toLowerCase();

                seconds += switch (unit) {
                    case "s" -> value;
                    case "m" -> value * 60L;
                    case "h" -> value * 3600L;
                    case "d" -> value * 86400L;
                    default -> 0;
                };
            } catch (NumberFormatException ignored) {}
        }

        return seconds;
    }

    public static String formatTime(long seconds) {
        if (seconds < 60) return seconds + "s";
        if (seconds < 3600) return (seconds / 60) + "m " + (seconds % 60) + "s";
        if (seconds < 86400) return (seconds / 3600) + "h " + ((seconds % 3600) / 60) + "m";
        return (seconds / 86400) + "d " + ((seconds % 86400) / 3600) + "h";
    }
}