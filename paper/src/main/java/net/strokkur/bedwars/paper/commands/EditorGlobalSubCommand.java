package net.strokkur.bedwars.paper.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.math.BlockPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.strokkur.bedwars.paper.map.editor.BedwarsDevMapInstance;
import net.strokkur.bedwars.paper.map.editor.EditorManager;
import net.strokkur.bedwars.paper.util.BrigadierUtils;
import net.strokkur.bedwars.paper.util.PureLocation;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class EditorGlobalSubCommand {

    private static int ironSpawnRate(final CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final BedwarsDevMapInstance session = EditorManager.instance().getActiveDevInstance(ctx);
        final Player player = (Player) ctx.getSource().getSender();

        player.sendRichMessage("The iron spawn rate is set to <rate>x",
            Placeholder.component("rate", Component.text(session.devMapData.ironSpawnRate))
        );

        return 1;
    }

    private static int ironSpawnRateSet(final CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final BedwarsDevMapInstance session = EditorManager.instance().getActiveDevInstance(ctx);
        final Player player = (Player) ctx.getSource().getSender();

        session.devMapData.ironSpawnRate = DoubleArgumentType.getDouble(ctx, "rate");
        player.sendRichMessage("The iron spawn rate is now set to <rate>x",
            Placeholder.component("rate", Component.text(session.devMapData.ironSpawnRate))
        );

        return 1;
    }

    private static int list(final CommandContext<CommandSourceStack> ctx, final Mineral mineral) throws CommandSyntaxException {
        final BedwarsDevMapInstance session = EditorManager.instance().getActiveDevInstance(ctx);
        final Player player = (Player) ctx.getSource().getSender();

        final StringBuilder builder = new StringBuilder("The following locations are set as <mineral> spawners:\n");
        final List<PureLocation> locations = mineral == Mineral.DIAMOND ? session.devMapData.diamondGenerators : session.devMapData.emeraldGenerators;

        for (int i = 0; i < locations.size(); i++) {
            builder.append("<dark_gray>[").append(i).append("] <gray>").append(locations.get(i));
        }

        player.sendRichMessage(builder.toString(),
            Placeholder.parsed("mineral", mineral == Mineral.DIAMOND ? "<aqua>diamond</aqua>" : "<green>emerald</green>")
        );
        return 1;
    }

    private static int remove(final CommandContext<CommandSourceStack> ctx, final Mineral mineral) throws CommandSyntaxException {
        final BedwarsDevMapInstance session = EditorManager.instance().getActiveDevInstance(ctx);
        final Player player = (Player) ctx.getSource().getSender();

        final List<PureLocation> locations = mineral == Mineral.DIAMOND ? session.devMapData.diamondGenerators : session.devMapData.emeraldGenerators;
        final int indexToRemove = IntegerArgumentType.getInteger(ctx, "index");

        if (indexToRemove >= locations.size()) {
            throw BrigadierUtils.createSimpleException("Index " + indexToRemove + " out of bounds.");
        }

        final PureLocation location = locations.get(indexToRemove);
        locations.remove(indexToRemove);
        player.sendRichMessage("Successfully removed location " + location + " from the <gen> generator locations.",
            Placeholder.parsed("gen", mineral == Mineral.DIAMOND ? "<aqua>diamond</aqua>" : "<green>emerald</green>")
        );
        return 1;
    }

    private static int add(final CommandContext<CommandSourceStack> ctx, final Mineral mineral) throws CommandSyntaxException {
        final BedwarsDevMapInstance session = EditorManager.instance().getActiveDevInstance(ctx);
        final Player player = (Player) ctx.getSource().getSender();

        final List<PureLocation> locations = mineral == Mineral.DIAMOND ? session.devMapData.diamondGenerators : session.devMapData.emeraldGenerators;
        final BlockPosition position = ctx.getArgument("position", BlockPositionResolver.class).resolve(ctx.getSource());

        final PureLocation pureLocation = PureLocation.from(position);
        locations.add(pureLocation);
        player.sendRichMessage("Successfully added location " + pureLocation + " to the <gen> generator locations.",
            Placeholder.parsed("gen", mineral == Mineral.DIAMOND ? "<aqua>diamond</aqua>" : "<green>emerald</green>")
        );
        return 1;
    }

    private static int setBox(final CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final BedwarsDevMapInstance session = EditorManager.instance().getActiveDevInstance(ctx);
        final Player player = (Player) ctx.getSource().getSender();

        final PureLocation cornerOne = PureLocation.from(ctx.getArgument("corner-one", BlockPositionResolver.class).resolve(ctx.getSource()));
        final PureLocation cornerTwo = PureLocation.from(ctx.getArgument("corner-two", BlockPositionResolver.class).resolve(ctx.getSource()));

        session.devMapData.boundCornerOne = cornerOne;
        session.devMapData.boundCornerTwo = cornerTwo;

        player.sendRichMessage("Successfully set the map's bounding box to be between <corner_one> and <corner_two>",
            Placeholder.unparsed("corner_one", cornerOne.toString()),
            Placeholder.unparsed("corner_two", cornerTwo.toString())
        );
        return 1;
    }

    public static LiteralArgumentBuilder<CommandSourceStack> construct() {
        return Commands.literal("global")

            .then(Commands.literal("iron-spawn-rate")
                .executes(EditorGlobalSubCommand::ironSpawnRate)
                .then(Commands.argument("rate", DoubleArgumentType.doubleArg(0))
                    .executes(EditorGlobalSubCommand::ironSpawnRateSet)
                )
            )

            .then(Commands.literal("diamond-gens")
                .then(Commands.literal("list")
                    .executes(ctx -> list(ctx, Mineral.DIAMOND))
                )

                .then(Commands.literal("remove")
                    .then(Commands.argument("index", IntegerArgumentType.integer(0))
                        .executes(ctx -> remove(ctx, Mineral.DIAMOND))
                    )
                )

                .then(Commands.literal("add")
                    .then(Commands.argument("position", ArgumentTypes.blockPosition())
                        .executes(ctx -> add(ctx, Mineral.DIAMOND))
                    )
                )
            )

            .then(Commands.literal("emerald-gens")
                .then(Commands.literal("list")
                    .executes(ctx -> list(ctx, Mineral.EMERALD))
                )

                .then(Commands.literal("remove")
                    .then(Commands.argument("index", IntegerArgumentType.integer(0))
                        .executes(ctx -> remove(ctx, Mineral.EMERALD))
                    )
                )

                .then(Commands.literal("add")
                    .then(Commands.argument("position", ArgumentTypes.blockPosition())
                        .executes(ctx -> add(ctx, Mineral.EMERALD))
                    )
                )
            )

            .then(Commands.literal("bounding-box")
                .then(Commands.argument("corner-one", ArgumentTypes.blockPosition())
                    .then(Commands.argument("corner-two", ArgumentTypes.blockPosition())
                        .executes(EditorGlobalSubCommand::setBox)
                    )
                )
            );
    }

    private enum Mineral {
        DIAMOND, EMERALD
    }
}
