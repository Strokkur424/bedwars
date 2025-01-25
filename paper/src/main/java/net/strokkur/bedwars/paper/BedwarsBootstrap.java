package net.strokkur.bedwars.paper;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class BedwarsBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        // Commands are defined here...
    }
    
    @Override
    public JavaPlugin createPlugin(PluginProviderContext context) {
        final BedwarsPaper plugin = new BedwarsPaper();
        BedwarsPaper.instance = plugin;
        return plugin;
    }
}
