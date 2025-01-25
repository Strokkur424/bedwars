package net.strokkur.bedwars.paper.map;

public class MapNotFoundException extends RuntimeException {
    public MapNotFoundException(String map) {
        super("Map named '" + map + "' was not found inside plugin's map folder");
    }
}
