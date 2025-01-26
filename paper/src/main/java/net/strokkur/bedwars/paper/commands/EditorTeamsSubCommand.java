package net.strokkur.bedwars.paper.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.strokkur.bedwars.paper.commands.arguments.TeamArgument;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class EditorTeamsSubCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("teams")
            .then(Commands.literal("info")
//                .executes(EditorSubCommand::executeTeamsInfo)
            )

            .then(Commands.literal("remove")
                .then(Commands.argument("team", new TeamArgument())
//                    .executes(EditorSubCommand::executeTeamRemove)
                )
            )

            .then(Commands.literal("add")
                .then(Commands.argument("team", new TeamArgument())
//                    .executes(EditorSubCommand::executeTeamAdd)
                )
            )

            .then(Commands.literal("modify")
                .then(Commands.argument("team", new TeamArgument())

                    .then(Commands.literal("spawn")
                        .then(Commands.argument("location", ArgumentTypes.blockPosition())
//                            .executes(EditorSubCommand::executeModifyTeamSpawn)
                        )
                    )

                    .then(Commands.literal("generator")
                        .then(Commands.argument("location", ArgumentTypes.blockPosition())
//                            .executes(EditorSubCommand::executeModifyTeamGenerator)
                        )
                    )

                    .then(Commands.literal("playershop")
                        .then(Commands.argument("location", ArgumentTypes.blockPosition())
//                            .executes(EditorSubCommand::executeModifyTeamPlayershop)
                        )
                    )

                    .then(Commands.literal("teamshop")
                        .then(Commands.argument("location", ArgumentTypes.blockPosition())
//                            .executes(EditorSubCommand::executeModifyTeamTeamshop)
                        )
                    )

                    .then(Commands.literal("bed")
                        .then(Commands.argument("location", ArgumentTypes.blockPosition())
//                            .executes(EditorSubCommand::executeModifyTeamBed)
                        )
                    )

                    .then(Commands.literal("bounds")
                        .then(Commands.argument("corner-one", ArgumentTypes.blockPosition())
                            .then(Commands.argument("corner-two", ArgumentTypes.blockPosition())
//                                .executes(EditorSubCommand::executeModifyTeamBoundingBox)
                            )
                        )
                    )

                )
            );

    }

}
