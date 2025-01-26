package net.strokkur.bedwars.paper.map.data;

import net.strokkur.bedwars.paper.BedwarsPaper;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@NullMarked
public class BedwarsMap {

    private static final Random random = new Random();
    private static final List<BedwarsMapInstance> instances = new ArrayList<>(16);

    @Nullable
    protected MapData data;

    protected String name;

    public BedwarsMap(String name) {
        this.name = name;
        this.data = null;
    }
    
    public BedwarsDevMapInstance createDevInstance() {
        return new BedwarsDevMapInstance(this);
    }

    public BedwarsMapInstance createInstance() {
        final BedwarsMapInstance instance = new BedwarsMapInstance(this, random.nextInt(0, 10000));
        instances.add(instance);
        return instance;
    }

    public CompletableFuture<Void> unloadInstance(BedwarsMapInstance instance) {
        return CompletableFuture
            .runAsync(instance::deleteMapAsync)
            .thenRunAsync(() -> instances.remove(instance));
    }

    public CompletableFuture<Void> unloadAllAsync() {
        return CompletableFuture
            .allOf(instances.parallelStream().map(BedwarsMapInstance::deleteMapAsync).toArray(CompletableFuture[]::new))
            .thenRunAsync(instances::clear);
    }

    /**
     * Should only be used in the plugin's onDisable
     */
    public void unloadAllSync() {
        instances.forEach(BedwarsMapInstance::deleteMap);
        instances.clear();
    }

    public Optional<BedwarsMapInstance> getInstanceById(int id) {
        for (BedwarsMapInstance instance : instances) {
            if (instance.id == id) {
                return Optional.of(instance);
            }
        }

        return Optional.empty();
    }

    @Unmodifiable
    public List<BedwarsMapInstance> getAllInstances() {
        return List.copyOf(instances);
    }
    
    public Path dataFolderPath() {
        return BedwarsPaper.dataPath().resolve("data");
    }
    
    public Path dataPath() {
        return dataFolderPath().resolve(name() + ".json");
    }
    
    public Path bakDataPath() {
        return dataFolderPath().resolve(name() + ".json.bak");
    }

    public String name() {
        return this.name;
    }

    public void name(String newName) {
        this.name = newName;
    }

    public Optional<MapData> data() {
        return Optional.ofNullable(this.data);
    }

    public void data(MapData newData) {
        this.data = newData;
    }
}
