package net.strokkur.bedwars.paper.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.strokkur.bedwars.paper.commands.arguments.MapArgument;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class EditorSubCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("editor")

            .then(Commands.literal("join")

                .then(Commands.argument("map", new MapArgument())
                    .executes(EditorSubCommand::executeJoin)
                )

            )

            .then(Commands.literal("save")

                .then(Commands.literal("config")

                )

                .then(Commands.literal("world")

                )
            )

            .then(EditorGlobalSubCommand.create())

            .then(EditorTeamsSubCommand.create());
    }

    private static int executeJoin(final CommandContext<CommandSourceStack> ctx) {
        return 1;
    }

    private static int executeSaveConfig(final CommandContext<CommandSourceStack> ctx) {
        return 1;
    }

    private static int executeSaveWorld(final CommandContext<CommandSourceStack> ctx) {
        return 1;
    }

}
