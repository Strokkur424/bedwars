package net.strokkur.bedwars.velocity;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import java.nio.file.Path;

@Plugin(
    id = "bedwars",
    name = "Bedwars (Velocity)",
    version = "1.0-SNAPSHOT",
    description = "Remake of Hypixel Bedwars",
    authors = {"Strokkur24"}
)
public class BedwarsVelocity {

    private static BedwarsVelocity instance;

    private final ProxyServer server;
    private final ComponentLogger logger;
    private final Path dataPath;

    @Inject
    public BedwarsVelocity(ProxyServer server, ComponentLogger logger, @DataDirectory Path dataPath) {
        if (instance != null) {
            throw new UnsupportedOperationException("Cannot create new instance of already created plugin main class.");
        }

        this.server = server;
        this.logger = logger;
        this.dataPath = dataPath;

        instance = this;
    }

    public static BedwarsVelocity instance() {
        return Preconditions.checkNotNull(instance);
    }

    public static ProxyServer server() {
        return instance().server;
    }

    public static ComponentLogger logger() {
        return instance().logger;
    }

    public static Path dataPath() {
        return instance().dataPath;
    }
}