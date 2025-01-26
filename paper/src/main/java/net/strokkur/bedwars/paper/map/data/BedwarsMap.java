package net.strokkur.bedwars.paper.map.data;

import com.google.common.base.Preconditions;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@NullMarked
public class BedwarsMap {

    private static final Random random = new Random();
    private static final List<BedwarsMapInstance> instances = new ArrayList<>(16);

    @Nullable
    private MapData data;

    private String name;

    public BedwarsMap(MapData mapData, String name) {
        this.name = name;
        this.data = mapData;
    }

    public BedwarsMapInstance createInstance() {
        final BedwarsMapInstance instance = new BedwarsMapInstance(this, random.nextInt(0, 10000));
        instances.add(instance);
        return instance;
    }

    public CompletableFuture<Void> unloadInstance(BedwarsMapInstance instance) {
        return CompletableFuture
            .runAsync(instance::deleteMap)
            .thenRunAsync(() -> instances.remove(instance));
    }

    public CompletableFuture<Void> unloadAllAsync() {
        return CompletableFuture
            .allOf(instances.parallelStream().map(BedwarsMapInstance::deleteMapAsync).toArray(CompletableFuture[]::new))
            .thenRunAsync(instances::clear);
    }
    
    public void unloadAllSync() {
        instances.forEach(BedwarsMapInstance::deleteMap);
        instances.clear();
    }


    public String name() {
        return this.name;
    }

    public void name(String newName) {
        this.name = newName;
    }

    public MapData data() {
        Preconditions.checkNotNull(data);
        return this.data;
    }

    public void data(MapData newData) {
        this.data = newData;
    }
}
