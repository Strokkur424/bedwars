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
import net.strokkur.bedwars.paper.map.data.BedwarsMap;
import net.strokkur.bedwars.paper.map.data.BedwarsMapInstance;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class LoadedMapArgument implements CustomArgumentType.Converted<BedwarsMapInstance, String> {

    @Override
    public BedwarsMapInstance convert(String nativeType) throws CommandSyntaxException {
        final String[] nameAndId = nativeType.split("-");
        if (nameAndId.length != 2) {
            final Message message = new LiteralMessage("Invalid name-id format: " + nativeType);
            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
        }

        final BedwarsMap map = new MapArgument().convert(nameAndId[0]);
        try {
            return map.getInstanceById(Integer.parseInt(nameAndId[1]))
                .orElseThrow(() -> {
                    final Message message = new LiteralMessage("Map " + nameAndId[0] + " does not have an instance with id " + nameAndId[1]);
                    return new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
                });
        }
        catch (NumberFormatException exception) {
            final Message message = new LiteralMessage("Invalid number: " + nameAndId[1]);
            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CompletableFuture.supplyAsync(() -> {
            for (BedwarsMap map : BedwarsPaper.worldManager().getAllBedwarsMaps()) {
                map.getAllInstances().forEach(instance -> {
                    if (instance.worldName().startsWith(builder.getRemainingLowerCase())) {
                        builder.suggest(instance.worldName());
                    }
                });
            }

            return builder.build();
        });
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }
}
