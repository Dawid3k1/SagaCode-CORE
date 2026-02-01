package org.sagamc.sagamcCore.warp;

import org.bukkit.Location;
import org.bukkit.Material;

public class Warp {
    private final String name;
    private final Location location;
    private final Material icon;

    public Warp(String name, Location location, Material icon) {
        this.name = name;
        this.location = location;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public Material getIcon() {
        return icon;
    }
}