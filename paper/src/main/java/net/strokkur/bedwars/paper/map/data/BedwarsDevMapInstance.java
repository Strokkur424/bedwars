package net.strokkur.bedwars.paper.map.data;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import net.strokkur.bedwars.paper.BedwarsPaper;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@NullMarked
public class BedwarsDevMapInstance extends AbstractBedwarsMapInstance {

    public MapData devMapData;

    public BedwarsDevMapInstance(BedwarsMap map) {
        super(map);
        this.devMapData = bedwarsMap.data().orElseGet(MapData::new).clone();
    }

    public String worldName() {
        return super.bedwarsMap.name() + "-dev";
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public CompletableFuture<Void> saveConfig() {
        return CompletableFuture.runAsync(() -> {
            final String json = new Gson().toJson(devMapData);

            final Path folder = super.bedwarsMap.dataFolderPath();
            if (!folder.toFile().exists()) {
                folder.toFile().mkdirs();
            }

            try {
                final Path file = super.bedwarsMap.dataPath();
                if (file.toFile().exists()) {
                    Files.copy(file, super.bedwarsMap.bakDataPath());
                }

                Files.writeString(file, json, Charsets.UTF_8);
            }
            catch (IOException fileException) {
                BedwarsPaper.logger().error("Failed to save config for {}", worldName(), fileException);
            }
        });
    }

    public void saveWorldSync() throws IOException {
        if (super.worldFolder == null) {
            throw new RuntimeException("No world folder found for world " + worldName());
        }

        final File zipTargetFile = BedwarsPaper.dataPath().resolve("maps/temp-" + worldName()).toFile();
        final FileOutputStream fileOutput = new FileOutputStream(zipTargetFile);
        final ZipOutputStream zipOutput = new ZipOutputStream(fileOutput, Charsets.UTF_8);

        DirectoryStream<Path> pathStream = Files.newDirectoryStream(super.worldFolder.toPath());
        for (Path path : pathStream) {
            if (path.startsWith("region") || path.endsWith("level.dat")) {
                ZipEntry entry = new ZipEntry(path.toString());
                zipOutput.putNextEntry(entry);
                
                final byte[] bytes = Files.readAllBytes(path);
                zipOutput.write(bytes, 0, bytes.length);
                zipOutput.closeEntry();
            }
        }
        
        pathStream.close();
        zipOutput.close();
        fileOutput.close();
        
        final Path finalTargetFile = BedwarsPaper.dataPath().resolve("maps/" + super.bedwarsMap.name() + ".zip");
        if (finalTargetFile.toFile().exists()) {
            Files.move(finalTargetFile, BedwarsPaper.dataPath().resolve("maps/" + super.bedwarsMap.name() + ".zip.bak"), StandardCopyOption.REPLACE_EXISTING);
        }
        
        Files.move(zipTargetFile.toPath(), finalTargetFile, StandardCopyOption.REPLACE_EXISTING);
    }

    public CompletableFuture<Void> saveWorldAsync() {
        return CompletableFuture.runAsync(() -> {
            try {
                saveWorldSync();
            }
            catch (Exception ex) {
                BedwarsPaper.logger().error("Failed to save world {}", worldName(), ex);
            }
        });
    }
}