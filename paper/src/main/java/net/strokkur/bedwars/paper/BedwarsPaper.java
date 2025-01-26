package net.strokkur.bedwars.paper;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.strokkur.bedwars.paper.map.WorldManager;
import net.strokkur.bedwars.paper.map.data.BedwarsMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.Executor;

@NullMarked
public class BedwarsPaper extends JavaPlugin {

    /* * * * * * * * *
     * Static access *
     * * * * * * * * */

    @Nullable
    static BedwarsPaper instance = null;

    public static BedwarsPaper instance() {
        return Preconditions.checkNotNull(instance);
    }

    public static Path dataPath() {
        return instance().getDataPath();
    }

    public static ComponentLogger logger() {
        return instance().getComponentLogger();
    }

    public static File getZipFile() {
        return instance().getFile();
    }

    public static Executor mainThreadExecutor() {
        return Bukkit.getScheduler().getMainThreadExecutor(instance());
    }

    /* * * * * * * * *
     * Plugin fields *
     * * * * * * * * */

    @Nullable
    private WorldManager worldManager = null;

    @Nullable
    private World mainWorld = null;

    public static WorldManager worldManager() {
        return Preconditions.checkNotNull(instance().worldManager);
    }
    
    public static World mainWorld() {
        return Preconditions.checkNotNull(instance().mainWorld);
    }

    /* * * * * * * * * * * *
     * Plugin initialising *
     * * * * * * * * * * * */

    @Override
    public void onLoad() {
        worldManager = new WorldManager();

        if (worldManager().createPluginMapsFolder()) {
            try {
                worldManager().copyDefaultMaps();
            }
            catch (IOException ex) {
                logger().error("Failed to copy default maps.", ex);
            }
        }

        worldManager().loadAllMaps();
    }

    @Override
    public void onEnable() {
        mainWorld = Bukkit.getWorlds().getFirst();
    }

    @Override
    public void onDisable() {
        if (mainWorld != null) {
            Bukkit.getOnlinePlayers().forEach(player -> player.teleport(mainWorld.getSpawnLocation()));
        }
        worldManager().getAllBedwarsMaps().forEach(BedwarsMap::unloadAllSync);
    }
}
