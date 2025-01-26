package net.strokkur.bedwars.paper.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class BedwarsCommand {
    
    public static LiteralCommandNode<CommandSourceStack> createNode() {
        return Commands.literal("bedwars")
            .then(MapSubCommand.create())
            .then(EditorSubCommand.create())
            .build();
    }
}
