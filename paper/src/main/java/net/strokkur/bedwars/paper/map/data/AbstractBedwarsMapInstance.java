package net.strokkur.bedwars.paper.map.data;

import com.google.common.base.Preconditions;
import net.kyori.adventure.util.TriState;
import net.strokkur.bedwars.paper.BedwarsPaper;
import net.strokkur.bedwars.paper.map.EmptyChunkGenerator;
import net.strokkur.bedwars.paper.map.MapNotFoundException;
import org.bukkit.*;
import org.codehaus.plexus.util.FileUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@NullMarked
public abstract class AbstractBedwarsMapInstance {

    public final BedwarsMap bedwarsMap;
    protected final MapData mapData;

    @Nullable
    protected File worldFolder;

    @Nullable
    protected World world;

    public AbstractBedwarsMapInstance(BedwarsMap map) {
        this.bedwarsMap = map;
        this.mapData = bedwarsMap.data().orElseGet(MapData::new).clone();
    }

    public Optional<World> getWorld() {
        return Optional.ofNullable(world);
    }

    public abstract String worldName();

    public CompletableFuture<World> loadMap() {
        return copyWorldFiles()
            .thenApplyAsync(finished -> {
                world = Preconditions.checkNotNull(Bukkit.createWorld(new WorldCreator(worldName())
                    .generator(new EmptyChunkGenerator())
                    .keepSpawnLoaded(TriState.FALSE)
                ));
                world.setGameRule(GameRule.DISABLE_RAIDS, true);
                world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
                world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                world.setGameRule(GameRule.DO_FIRE_TICK, false);
                world.setGameRule(GameRule.DO_MOB_LOOT, false);
                world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
                world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
                return world;
            }, BedwarsPaper.mainThreadExecutor());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public CompletableFuture<Void> copyWorldFiles() {
        return CompletableFuture.runAsync(() -> {
            final long startTime = System.currentTimeMillis();

            final File mapFile = BedwarsPaper.dataPath().resolve("maps/" + bedwarsMap.name() + ".zip").toFile();
            if (!mapFile.exists()) {
                throw new MapNotFoundException(bedwarsMap.name());
            }

            this.worldFolder = new File(Bukkit.getWorldContainer(), worldName());
            if (worldFolder.exists()) {
                throw new RuntimeException("Map folder already exists. (" + worldName() + ")");
            }

            //noinspection ResultOfMethodCallIgnored
            worldFolder.mkdirs();

            try (ZipInputStream zipInput = new ZipInputStream(new FileInputStream(mapFile))) {
                ZipEntry zipEntry;
                while ((zipEntry = zipInput.getNextEntry()) != null) {
                    if (zipEntry.isDirectory()) {
                        new File(worldFolder, zipEntry.getName()).mkdirs();
                        continue;
                    }

                    final File outputFile = new File(worldFolder, zipEntry.getName());
                    try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                        zipInput.transferTo(fileOutputStream);
                    }
                }
            }
            catch (IOException zipFileError) {
                BedwarsPaper.logger().error("Failed to unzip {}", mapFile.getPath(), zipFileError);
            }

            BedwarsPaper.logger().info("Finished copying {} in {}ms", worldName(), System.currentTimeMillis() - startTime);
        });
    }

    private CompletableFuture<Void> teleportOutPlayers() {
        if (world == null) {
            return CompletableFuture.completedFuture(null);
        }
    
        final Location targetLocation = BedwarsPaper.mainWorld().getSpawnLocation();
        return BedwarsPaper.mainWorld().getChunkAtAsync(targetLocation)
            .thenRun(() -> world.getPlayers().forEach(player -> player.teleport(targetLocation)));
    }

    public CompletableFuture<Void> deleteMapAsync() {
        return teleportOutPlayers().thenRunAsync(() -> {
            if (world == null) {
                return;
            }

            Bukkit.unloadWorld(world, false);
        }, BedwarsPaper.mainThreadExecutor()).thenRunAsync(() -> {
            if (worldFolder == null || !worldFolder.exists() || !worldFolder.isDirectory()) {
                return;
            }

            try {
                FileUtils.deleteDirectory(worldFolder);
            }
            catch (IOException directoryRemoveException) {
                BedwarsPaper.logger().warn("An error occurred whilst trying to delete {}", worldName(), directoryRemoveException);
            }
        });
    }

}
