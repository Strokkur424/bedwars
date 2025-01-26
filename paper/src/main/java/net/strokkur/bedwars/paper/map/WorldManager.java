package net.strokkur.bedwars.paper.map;

import net.strokkur.bedwars.paper.BedwarsPaper;
import net.strokkur.bedwars.paper.map.data.BedwarsMap;
import net.strokkur.bedwars.paper.map.data.MapData;
import org.codehaus.plexus.util.FastMap;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@NullMarked
public class WorldManager {

    private final File mapsFolder = BedwarsPaper.dataPath().resolve("maps").toFile();
    private final Map<String, BedwarsMap> maps = new FastMap<>(64);

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

    public void loadAllMaps() {
        maps.clear();

        try (Stream<Path> stream = Files.list(BedwarsPaper.dataPath().resolve("maps"))) {
            stream
                .map(path -> path.getFileName().toString().split("\\.")[0])
                .map(BedwarsMap::new)
                .forEach(map -> maps.put(map.name(), map));
        }
        catch (IOException exception) {
            BedwarsPaper.logger().warn("Failed to load maps", exception);
        }
    }

    public boolean doesMapExist(String mapName) {
        return maps.containsKey(mapName);
    }

    public Set<String> getAllMapNames() {
        return maps.keySet();
    }

    public Collection<BedwarsMap> getAllBedwarsMaps() {
        return maps.values();
    }

    public Optional<BedwarsMap> getMap(String mapName) {
        return Optional.ofNullable(maps.get(mapName));
    }
}
