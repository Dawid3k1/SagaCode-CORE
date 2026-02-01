package org.sagamc.sagamcCore.home;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class Home {
    private final String name;
    private final Location location;
    private final UUID owner;

    public Home(String name, Location location, UUID owner) {
        this.name = name;
        this.location = location;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public UUID getOwner() {
        return owner;
    }
}