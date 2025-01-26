package net.strokkur.bedwars.paper.commands.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.strokkur.bedwars.paper.map.data.TeamColor;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class TeamArgument implements CustomArgumentType.Converted<TeamColor, String> {

    @Override
    public TeamColor convert(String nativeType) throws CommandSyntaxException {
        try {
            return TeamColor.of(nativeType);
        }
        catch (NullPointerException invalidNumber) {
            final Message message = new LiteralMessage(nativeType + " is not a valid team color!");
            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
        }
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CompletableFuture.supplyAsync(() -> {
            for (TeamColor color : TeamColor.values()) {
                if (color.toString().startsWith(builder.getRemainingLowerCase())) {
                    builder.suggest(color.toString());
                }
            }
            return builder.build();
        });
    }
}
