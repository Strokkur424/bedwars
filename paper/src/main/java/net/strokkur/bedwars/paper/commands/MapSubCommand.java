package net.strokkur.bedwars.paper.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.strokkur.bedwars.paper.BedwarsPaper;
import net.strokkur.bedwars.paper.commands.arguments.LoadedMapArgument;
import net.strokkur.bedwars.paper.commands.arguments.MapArgument;
import net.strokkur.bedwars.paper.map.data.BedwarsMap;
import net.strokkur.bedwars.paper.map.data.BedwarsMapInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
class MapSubCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("map")
            .requires(ctx -> ctx.getSender().hasPermission("bedwars.command.map"))

            .then(Commands.literal("unload")
                .then(Commands.literal("*")
                    .executes(MapSubCommand::multipleMapsUnloadExecutor)
                )

                .then(Commands.argument("instance", new LoadedMapArgument())
                    .executes(MapSubCommand::singleMapUnloadExecutor)
                )
            )

            .then(Commands.literal("load")
                .then(Commands.argument("map", new MapArgument())
                    .executes(MapSubCommand::mapLoadExecutor)
                )
            );
    }

    private static int multipleMapsUnloadExecutor(final CommandContext<CommandSourceStack> ctx) {
        final long startTime = System.currentTimeMillis();
        CompletableFuture.allOf(BedwarsPaper.worldManager().getAllBedwarsMaps().parallelStream()
            .map(BedwarsMap::unloadAllAsync)
            .toArray(CompletableFuture[]::new)
        ).thenRunAsync(() -> {
            ctx.getSource().getSender().sendRichMessage("<gold>Successfully unloaded all maps! (Took <time>ms)",
                Placeholder.unparsed("time", Long.toString(System.currentTimeMillis() - startTime))
            );
        });
        return Command.SINGLE_SUCCESS;
    }

    private static int singleMapUnloadExecutor(final CommandContext<CommandSourceStack> ctx) {
        final long startTime = System.currentTimeMillis();
        final BedwarsMapInstance instance = ctx.getArgument("instance", BedwarsMapInstance.class);
        instance
            .unloadMap()
            .thenRunAsync(() -> ctx.getSource().getSender().sendRichMessage("<aqua>Successfully unloaded <map> (Took <time>ms)",
                Placeholder.unparsed("map", instance.worldName()),
                Placeholder.unparsed("time", Long.toString(System.currentTimeMillis() - startTime))
            ));
        return Command.SINGLE_SUCCESS;
    }

    private static int mapLoadExecutor(final CommandContext<CommandSourceStack> ctx) {
        final long startTime = System.currentTimeMillis();
        ctx.getArgument("map", BedwarsMap.class)
            .createInstance()
            .loadMap()
            .thenAcceptAsync(
                world -> {
                    if (ctx.getSource().getExecutor() instanceof Player player) {
                        player.teleport(new Location(world, 0, 250, 0));
                        player.setFlying(true);
                    }
                    ctx.getSource().getSender().sendRichMessage("<aqua>Successfully loaded <map> (Took <time>ms)",
                        Placeholder.unparsed("map", world.getName()),
                        Placeholder.unparsed("time", Long.toString(System.currentTimeMillis() - startTime))
                    );
                },
                BedwarsPaper.mainThreadExecutor()
            );
        return Command.SINGLE_SUCCESS;
    }
}