package net.strokkur.bedwars.paper.map.data;

import com.google.common.base.Preconditions;
import net.strokkur.bedwars.paper.BedwarsPaper;
import net.strokkur.bedwars.paper.map.EmptyChunkGenerator;
import net.strokkur.bedwars.paper.map.MapNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.codehaus.plexus.util.FileUtils;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

    public abstract String worldName();

    public CompletableFuture<World> loadMap() {
        return copyWorldFiles()
            .thenApplyAsync(finished -> {
                world = Bukkit.createWorld(new WorldCreator(worldName()).generator(new EmptyChunkGenerator()));
                Preconditions.checkNotNull(world);

                return this.world;
            }, Bukkit.getScheduler().getMainThreadExecutor(BedwarsPaper.instance()));
    }
    
    public CompletableFuture<@Nullable Void> copyWorldFiles() {
        return CompletableFuture.supplyAsync(() -> {
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

            byte[] buffer = new byte[1024];
            try (ZipInputStream zipInput = new ZipInputStream(new FileInputStream(mapFile))) {
                ZipEntry zipEntry;
                while ((zipEntry = zipInput.getNextEntry()) != null) {
                    if (zipEntry.isDirectory()) {
                        //noinspection ResultOfMethodCallIgnored
                        new File(worldFolder, zipEntry.getName()).mkdirs();
                        continue;
                    }

                    final File outputFile = new File(worldFolder, zipEntry.getName());
                    try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                        int len;
                        while ((len = zipInput.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, len);
                        }
                    }
                }
            }
            catch (IOException zipFileError) {
                BedwarsPaper.logger().error("Failed to unzip {}", mapFile.getPath(), zipFileError);
            }

            return null;
        });
    }

    public CompletableFuture<Void> deleteMapAsync() {
        return CompletableFuture.runAsync(() -> {
            if (worldFolder == null || !worldFolder.exists() || !worldFolder.isDirectory()) {
                return;
            }

            if (world != null) {
                try {
                    CompletableFuture.runAsync(
                        () -> {
                            world.getPlayers().forEach(player -> player.teleport(BedwarsPaper.mainWorld().getSpawnLocation()));
                            Bukkit.unloadWorld(world, false);
                        },
                        BedwarsPaper.mainThreadExecutor()
                    ).get();
                }
                catch (Exception asyncException) {
                    BedwarsPaper.logger().error("Failed to unload world {}", worldName(), asyncException);
                }
            }

            try {
                FileUtils.deleteDirectory(worldFolder);
            }
            catch (IOException directoryRemoveException) {
                BedwarsPaper.logger().warn("An error occurred whilst trying to delete {}", worldName(), directoryRemoveException);
            }
        });
    }

    public void deleteMap() {
        if (worldFolder == null || !worldFolder.exists() || !worldFolder.isDirectory()) {
            return;
        }

        if (world != null) {
            Bukkit.unloadWorld(world, false);
        }

        try {
            FileUtils.deleteDirectory(worldFolder);
        }
        catch (IOException directoryRemoveException) {
            BedwarsPaper.logger().warn("An error occurred while trying to delete {}", worldName(), directoryRemoveException);
        }
    }
    
}
