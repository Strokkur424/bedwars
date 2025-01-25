package net.strokkur.bedwars.paper.map;

import net.strokkur.bedwars.paper.BedwarsPaper;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class WorldManager {

    private final File mapsFolder = BedwarsPaper.dataPath().resolve("maps").toFile();

    private static final String bedwarsLoadedMapIndicator = ".bedwars-loaded-map";

    public void deleteBedwarsMaps() {
        final List<File> bedwarsMapFolders = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Bukkit.getWorldContainer().toPath())) {
            for (Path path : directoryStream) {
                final File potentialMapFolder = path.toFile();
                if (!potentialMapFolder.isDirectory()) {
                    continue;
                }

                if (new File(potentialMapFolder, bedwarsLoadedMapIndicator).exists()) {
                    bedwarsMapFolders.add(potentialMapFolder);
                }
            }
        }
        catch (IOException e) {
            BedwarsPaper.logger().warn("Failed to list folders from world folder", e);
        }

        bedwarsMapFolders.forEach(file -> {
            try {
                FileUtils.deleteDirectory(file);
            }
            catch (IOException ex) {
                BedwarsPaper.logger().warn("Failed to delete directory {}", file.getPath());
            }
        });
    }

    public boolean createPluginMapsFolder() {
        if (mapsFolder.exists() && mapsFolder.isDirectory()) {
            return false;
        }

        return mapsFolder.mkdirs();
    }

    public void copyDefaultMaps() throws IOException {
        try (ZipInputStream zipStream = new ZipInputStream(new FileInputStream(BedwarsPaper.getZipFile()))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipStream.getNextEntry()) != null) {
                if (!zipEntry.getName().matches("maps/.*\\.zip")) {
                    continue;
                }

                BedwarsPaper.logger().info("Copying default map: {}", zipEntry.getName());
                BedwarsPaper.instance().saveResource(zipEntry.getName(), false);
            }
        }
    }

    public CompletableFuture<World> loadMap(String worldName) {
        return copyMap(worldName)
            .thenApplyAsync(
                loaded -> {
                    World world = Bukkit.createWorld(new WorldCreator(worldName).generator(new EmptyChunkGenerator()));
                    if (world != null) {
                        world.setGameRule(GameRule.SPAWN_CHUNK_RADIUS, 0);
                    }
                    return world;
                },
                Bukkit.getScheduler().getMainThreadExecutor(BedwarsPaper.instance())
            );
    }

    public CompletableFuture<Void> copyMap(String worldName) {
        return CompletableFuture.supplyAsync(() -> {
            final File mapFile = BedwarsPaper.dataPath().resolve("maps/" + worldName + ".zip").toFile();
            if (!mapFile.exists()) {
                throw new MapNotFoundException(worldName);
            }

            final File outputFolder = new File(Bukkit.getWorldContainer(), worldName);
            if (outputFolder.exists()) {
                if (Bukkit.getWorld(worldName) != null) {
                    Bukkit.unloadWorld(worldName, false);
                }
                try {
                    FileUtils.deleteDirectory(outputFolder);
                }
                catch (IOException e) {
                    throw new RuntimeException("Failed to delete previously loaded map. (" + worldName + ")");
                }
            }

            //noinspection ResultOfMethodCallIgnored
            outputFolder.mkdirs();
            try {
                Files.write(outputFolder.toPath().resolve(bedwarsLoadedMapIndicator), new byte[]{1});
            }
            catch (IOException bedwarsMapIndicatorException) {
                BedwarsPaper.logger().warn("Failed to create bedwars map indicator... map might not unload automatically", bedwarsMapIndicatorException);
            }

            byte[] buffer = new byte[1024];
            try (ZipInputStream zipInput = new ZipInputStream(new FileInputStream(mapFile))) {
                ZipEntry zipEntry;
                while ((zipEntry = zipInput.getNextEntry()) != null) {
                    if (zipEntry.isDirectory()) {
                        //noinspection ResultOfMethodCallIgnored
                        new File(outputFolder, zipEntry.getName()).mkdirs();
                        continue;
                    }

                    final File outputFile = new File(outputFolder, zipEntry.getName());
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

    public boolean doesMapExist(String map) {
        return BedwarsPaper.dataPath().resolve("maps/" + map + ".zip").toFile().exists();
    }

    public List<String> getAllMaps() {
        try (Stream<Path> stream = Files.list(BedwarsPaper.dataPath().resolve("maps"))) {
            return stream.map(path -> path.getFileName().toString().split("\\.")[0]).toList();
        }
        catch (IOException exception) {
            BedwarsPaper.logger().warn("Failed to retrieve all maps", exception);
        }

        return List.of();
    }
}
