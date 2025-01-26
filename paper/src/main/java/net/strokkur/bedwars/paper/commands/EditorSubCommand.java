package net.strokkur.bedwars.paper.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.strokkur.bedwars.paper.commands.arguments.MapArgument;
import net.strokkur.bedwars.paper.commands.arguments.TeamArgument;
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

            .then(EditorGlobalSubCommand.construct())

            .then(Commands.literal("teams")
                .then(Commands.literal("info")
                    .executes(EditorSubCommand::executeTeamsInfo)
                )

                .then(Commands.literal("remove")
                    .then(Commands.argument("team", new TeamArgument())
                        .executes(EditorSubCommand::executeTeamRemove)
                    )
                )

                .then(Commands.literal("add")
                    .then(Commands.argument("team", new TeamArgument())
                        .executes(EditorSubCommand::executeTeamAdd)
                    )
                )

                .then(Commands.literal("modify")
                    .then(Commands.argument("team", new TeamArgument())

                        .then(Commands.literal("spawn")
                            .then(Commands.argument("location", ArgumentTypes.blockPosition())
                                .executes(EditorSubCommand::executeModifyTeamSpawn)
                            )
                        )

                        .then(Commands.literal("generator")
                            .then(Commands.argument("location", ArgumentTypes.blockPosition())
                                .executes(EditorSubCommand::executeModifyTeamGenerator)
                            )
                        )

                        .then(Commands.literal("playershop")
                            .then(Commands.argument("location", ArgumentTypes.blockPosition())
                                .executes(EditorSubCommand::executeModifyTeamPlayershop)
                            )
                        )

                        .then(Commands.literal("teamshop")
                            .then(Commands.argument("location", ArgumentTypes.blockPosition())
                                .executes(EditorSubCommand::executeModifyTeamTeamshop)
                            )
                        )

                        .then(Commands.literal("bed")
                            .then(Commands.argument("location", ArgumentTypes.blockPosition())
                                .executes(EditorSubCommand::executeModifyTeamBed)
                            )
                        )

                        .then(Commands.literal("bounds")
                            .then(Commands.argument("corner-one", ArgumentTypes.blockPosition())
                                .then(Commands.argument("corner-two", ArgumentTypes.blockPosition())
                                    .executes(EditorSubCommand::executeModifyTeamBoundingBox)
                                )
                            )
                        )

                    )
                )
            );
    }

    private static int executeJoin(final CommandContext<CommandSourceStack> ctx) {
        return 0;
    }

    private static int executeTeamsInfo(final CommandContext<CommandSourceStack> ctx) {
        return 0;
    }

    private static int executeTeamRemove(final CommandContext<CommandSourceStack> ctx) {
        return 0;
    }

    private static int executeTeamAdd(final CommandContext<CommandSourceStack> ctx) {
        return 0;
    }

    private static int executeModifyTeamSpawn(final CommandContext<CommandSourceStack> ctx) {
        return 0;
    }

    private static int executeModifyTeamGenerator(final CommandContext<CommandSourceStack> ctx) {
        return 0;
    }

    private static int executeModifyTeamPlayershop(final CommandContext<CommandSourceStack> ctx) {
        return 0;
    }

    private static int executeModifyTeamTeamshop(final CommandContext<CommandSourceStack> ctx) {
        return 0;
    }

    private static int executeModifyTeamBed(final CommandContext<CommandSourceStack> ctx) {
        return 0;
    }


    private static int executeModifyTeamBoundingBox(final CommandContext<CommandSourceStack> ctx) {
        return 0;
    }

}
