package net.strokkur.bedwars.paper.util;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BrigadierUtils {
    
    public static CommandSyntaxException createSimpleException(final String str) {
        final Message message = new LiteralMessage(str);
        return new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
    }
    
}
