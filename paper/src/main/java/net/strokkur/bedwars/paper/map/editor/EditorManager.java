package net.strokkur.bedwars.paper.map.editor;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.strokkur.bedwars.paper.map.data.BedwarsMap;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.codehaus.plexus.util.FastMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NullMarked
public class EditorManager implements Listener {

    private final Map<Player, BedwarsDevMapInstance> editorSessions = new FastMap<>(20);
    private final Map<BedwarsMap, BedwarsDevMapInstance> editors = new FastMap<>(8);

    @SuppressWarnings("UnstableApiUsage")
    public BedwarsDevMapInstance getActiveDevInstance(final CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        if (!(ctx.getSource().getSender() instanceof Player player)) {
            final Message message = new LiteralMessage("This operation requires a player");
            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
        }

        if (!editorSessions.containsKey(player)) {
            final Message message = new LiteralMessage("You are not in an active editing session!");
            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
        }

        return editorSessions.get(player);
    }

    public BedwarsDevMapInstance getOrCreate(final BedwarsMap bedwarsMap) {
        if (editors.containsKey(bedwarsMap)) {
            return editors.get(bedwarsMap);
        }
        
        final BedwarsDevMapInstance instance = new BedwarsDevMapInstance(bedwarsMap);
        editors.put(bedwarsMap, instance);
        return instance;
    }

    public void closeSession(final BedwarsDevMapInstance editor) {
        final List<Player> playersToRemove = new ArrayList<>();
        for (Map.Entry<Player, BedwarsDevMapInstance> entry : editorSessions.entrySet()) {
            if (entry.getValue() != editor) {
                continue;
            }

            playersToRemove.add(entry.getKey());
        }

        playersToRemove.forEach(editorSessions::remove);
        editors.remove(editor.bedwarsMap);

        editor.deleteMapAsync();
    }

    public void joinSession(final Player player, final BedwarsDevMapInstance editor) {
        editorSessions.put(player, editor);
        editor.getWorld().ifPresent(world -> {
            player.teleportAsync(new Location(world, 0, 255, 0));
            player.setGameMode(GameMode.CREATIVE);
            player.setFlying(true);
        });
    }

    /* * * * * * *
     * Listeners *
     * * * * * * */

    @EventHandler
    public void onPlayerDisconnect(final PlayerQuitEvent event) {
        editorSessions.remove(event.getPlayer());
    }

    @EventHandler
    public void onPlayerSwitchWorld(final PlayerTeleportEvent event) {
        if (!event.getTo().getWorld().equals(event.getFrom())) {
            editorSessions.remove(event.getPlayer());
        }
    }

    /* * * * * * * * *
     * Static Access *
     * * * * * * * * */

    @Nullable
    private static EditorManager instance = null;

    public static EditorManager instance() {
        if (instance == null) {
            instance = new EditorManager();
        }

        return instance;
    }
}
