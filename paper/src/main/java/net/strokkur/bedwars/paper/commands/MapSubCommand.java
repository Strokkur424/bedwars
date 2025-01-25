package net.strokkur.bedwars.paper.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.strokkur.bedwars.paper.BedwarsPaper;
import net.strokkur.bedwars.paper.commands.arguments.MapArgument;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class MapSubCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("map")
            .requires(ctx -> ctx.getSender().hasPermission("bedwars.command.map"))
            .then(Commands.literal("unload")

                .then(Commands.literal("*")
                    .executes(ctx -> {
                        BedwarsPaper.worldManager().deleteBedwarsMaps();
                        return Command.SINGLE_SUCCESS;
                    })
                )

                .then(Commands.argument("map", new MapArgument())
                    .executes(ctx -> {
                        BedwarsPaper.worldManager().deleteBedwarsMaps();
                        return Command.SINGLE_SUCCESS;
                    })
                )
            )

            .then(Commands.literal("load")

                .then(Commands.argument("map", new MapArgument())
                    .executes(ctx -> {
                        BedwarsPaper.worldManager().loadMap(ctx.getArgument("map", String.class))
                            .thenAccept(world -> {
                                Bukkit.getScheduler().runTask(BedwarsPaper.instance(), () -> {
                                    if (ctx.getSource().getExecutor() instanceof Player player) {
                                        player.teleport(new Location(world, 0, 250, 0));
                                        player.setFlying(true);
                                        player.sendRichMessage("<aqua>Teleported you to " + world.getName());
                                    }
                                });
                            });
                        return Command.SINGLE_SUCCESS;
                    }))

            );
    }
}