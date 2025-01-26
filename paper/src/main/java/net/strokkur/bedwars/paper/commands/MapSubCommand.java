package net.strokkur.bedwars.paper.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.strokkur.bedwars.paper.BedwarsPaper;
import net.strokkur.bedwars.paper.commands.arguments.MapArgument;
import net.strokkur.bedwars.paper.map.data.BedwarsMap;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class MapSubCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("map")
            .requires(ctx -> ctx.getSender().hasPermission("bedwars.command.map"))
            .then(Commands.literal("unload")

                .then(Commands.literal("*")
                    .executes(ctx -> {
                        CompletableFuture.allOf(BedwarsPaper.worldManager().getAllBedwarsMaps().parallelStream()
                            .map(BedwarsMap::unloadAllAsync)
                            .toArray(CompletableFuture[]::new)
                        ).thenRunAsync(() -> {
                            ctx.getSource().getSender().sendRichMessage("<gold>Successfully unloaded all maps!");
                        });
                        return Command.SINGLE_SUCCESS;
                    })
                )

                .then(Commands.argument("map", new MapArgument())
                    .executes(ctx -> {
                        BedwarsPaper.worldManager().getMap(ctx.getArgument("map", String.class))
                            .ifPresent(map -> map.unloadAllAsync()
                                .thenRunAsync(() -> ctx.getSource().getSender().sendRichMessage("<aqua>Successfully unloaded " + ctx.getArgument("map", String.class)))
                            );
                        return Command.SINGLE_SUCCESS;
                    })
                )
            )

            .then(Commands.literal("load")

                .then(Commands.argument("map", new MapArgument())
                    .executes(ctx -> {
                        BedwarsPaper.worldManager().getMap(ctx.getArgument("map", String.class))
                            .ifPresent(map -> map.createInstance().loadMap()

                                .thenAcceptAsync(
                                    world -> {
                                        if (ctx.getSource().getExecutor() instanceof Player player) {
                                            player.teleport(new Location(world, 0, 250, 0));
                                            player.setFlying(true);
                                        }
                                        ctx.getSource().getSender().sendRichMessage("<aqua>Successfully loaded " + map.name());
                                    },
                                    BedwarsPaper.mainThreadExecutor()
                                )

                            );
                        return Command.SINGLE_SUCCESS;
                    }))

            );
    }
}