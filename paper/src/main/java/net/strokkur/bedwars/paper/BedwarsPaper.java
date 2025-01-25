package net.strokkur.bedwars.paper;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.strokkur.bedwars.paper.map.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

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

    /* * * * * * * * *
     * Plugin fields *
     * * * * * * * * */

    @Nullable
    private WorldManager worldManager = null;

    public static WorldManager worldManager() {
        return Preconditions.checkNotNull(instance().worldManager);
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
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        worldManager().deleteBedwarsMaps();
    }
}
