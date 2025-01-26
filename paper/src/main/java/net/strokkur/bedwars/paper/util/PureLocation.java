package net.strokkur.bedwars.paper.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@NullMarked
@ConfigSerializable
public class PureLocation {

    private final int x;
    private final int y;
    private final int z;

    public PureLocation(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Location apply(World world) {
        return new Location(world, x + 0.5d, y + 0.5d, z + 0.5d);
    }
}