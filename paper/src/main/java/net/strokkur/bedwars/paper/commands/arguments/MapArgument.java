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
import net.strokkur.bedwars.paper.BedwarsPaper;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class MapArgument implements CustomArgumentType.Converted<String, String> {

    @Override
    public String convert(String nativeType) throws CommandSyntaxException {
        if (!BedwarsPaper.worldManager().doesMapExist(nativeType)) {
            final Message message = new LiteralMessage("No map named " + nativeType + " exists.");
            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
        }

        return nativeType;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CompletableFuture.supplyAsync(() -> {
            for (String map : BedwarsPaper.worldManager().getAllMapNames()) {
                if (map.toLowerCase().startsWith(builder.getRemainingLowerCase())) {
                    builder.suggest(map);
                }
            }

            return builder.build();
        });
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }
}
