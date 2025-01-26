package net.strokkur.bedwars.paper.map.data;

import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public class BedwarsMapInstance extends AbstractBedwarsMapInstance {

    public final int id;

    public BedwarsMapInstance(BedwarsMap map, int id) {
        super(map);
        this.id = id;
    }

    @Override
    public String worldName() {
        return bedwarsMap.name() + "-" + id;
    }

    public CompletableFuture<Void> unloadMap() {
        return this.bedwarsMap.unloadInstance(this);
    }
}