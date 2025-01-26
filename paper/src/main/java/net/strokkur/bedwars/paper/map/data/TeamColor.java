package net.strokkur.bedwars.paper.map.data;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public enum TeamColor {
    WHITE(NamedTextColor.WHITE),
    LIGHT_GRAY(NamedTextColor.GRAY),
    GRAY(NamedTextColor.GRAY),
    BLACK(NamedTextColor.BLACK),
    RED(NamedTextColor.RED),
    ORANGE(NamedTextColor.GOLD),
    YELLOW(NamedTextColor.YELLOW),
    LIME(NamedTextColor.GREEN),
    GREEN(NamedTextColor.DARK_GREEN),
    CYAN(NamedTextColor.DARK_AQUA),
    LIGHT_BLUE(NamedTextColor.AQUA),
    BLUE(NamedTextColor.BLUE),
    PURPLE(NamedTextColor.DARK_PURPLE),
    MAGENTA(NamedTextColor.LIGHT_PURPLE);

    private final NamedTextColor color;

    TeamColor(NamedTextColor color) {
        this.color = color;
    }

    public NamedTextColor color() {
        return color;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public static TeamColor of(String string) {
        TeamColor color = TeamColor.valueOf(string.toUpperCase());
        Preconditions.checkNotNull(color, "Color " + string + " is not a valid team color!");
        return color;
    }
}
