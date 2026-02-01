package org.sagamc.sagamcCore.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();
    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<gradient:((?:#[A-Fa-f0-9]{6}:?)+)>(.*?)</gradient>");
    public static String legacyColor(String text) {
        if (text == null) return "";

        Matcher matcher = GRADIENT_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String colorsStr = matcher.group(1);
            String content = matcher.group(2);

            String[] colorCodes = colorsStr.split(":");
            java.awt.Color[] colors = new java.awt.Color[colorCodes.length];
            for (int i = 0; i < colorCodes.length; i++) {
                colors[i] = java.awt.Color.decode(colorCodes[i]);
            }

            StringBuilder gradient = new StringBuilder();
            int length = content.length();

            for (int i = 0; i < length; i++) {
                float ratio = length > 1 ? (float) i / (length - 1) : 0;
                float segmentLength = 1f / (colors.length - 1);
                int segmentIndex = Math.min((int) (ratio / segmentLength), colors.length - 2);
                float localRatio = (ratio - segmentIndex * segmentLength) / segmentLength;

                int r = (int) (colors[segmentIndex].getRed() + localRatio * (colors[segmentIndex + 1].getRed() - colors[segmentIndex].getRed()));
                int g = (int) (colors[segmentIndex].getGreen() + localRatio * (colors[segmentIndex + 1].getGreen() - colors[segmentIndex].getGreen()));
                int b = (int) (colors[segmentIndex].getBlue() + localRatio * (colors[segmentIndex + 1].getBlue() - colors[segmentIndex].getBlue()));

                gradient.append(ChatColor.of(new java.awt.Color(r, g, b))).append(content.charAt(i));
            }

            matcher.appendReplacement(sb, Matcher.quoteReplacement(gradient.toString()));
        }
        matcher.appendTail(sb);

        return ChatColor.translateAlternateColorCodes('&', sb.toString());
    }

    public static Component miniMessage(String text) {
        if (text == null) return Component.empty();
        return MINI_MESSAGE.deserialize(text);
    }

    public static String miniToLegacy(String text) {
        if (text == null) return "";
        return LEGACY_SERIALIZER.serialize(miniMessage(text));
    }

    public static String stripColor(String text) {
        if (text == null) return "";
        text = text.replaceAll("<[^>]+>", "");
        text = ChatColor.stripColor(legacyColor(text));
        return text;
    }
}