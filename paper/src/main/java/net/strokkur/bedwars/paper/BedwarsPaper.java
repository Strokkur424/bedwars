package net.strokkur.bedwars.paper;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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

    /* * * * * * * * * * * *
     * Plugin initialising *
     * * * * * * * * * * * */

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
