package net.strokkur.bedwars.paper;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.strokkur.bedwars.paper.commands.BedwarsCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class BedwarsBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(BedwarsCommand.createNode());
        });
    }

    @Override
    public JavaPlugin createPlugin(PluginProviderContext context) {
        final BedwarsPaper plugin = new BedwarsPaper();
        BedwarsPaper.instance = plugin;
        return plugin;
    }
}
