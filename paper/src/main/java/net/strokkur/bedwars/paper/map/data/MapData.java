package net.strokkur.bedwars.paper.map.data;

import com.google.common.base.Preconditions;
import net.strokkur.bedwars.paper.util.PureLocation;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class MapData {
    
    public int teamAmount;
    
    public int playersPerTeam;
    
    public List<TeamData> teamData = new ArrayList<>();
    
    public List<PureLocation> diamondGenerators = new ArrayList<>();
    
    public List<PureLocation> emeraldGenerators = new ArrayList<>();
    
    public PureLocation boundCornerOne;
    
    public PureLocation boundCornerTwo;
    
    
    @ConfigSerializable
    public static class TeamData {
        
        private String color;
        
        public PureLocation spawnLocation;
        
        public PureLocation generatorLocation;
        
        public PureLocation playerShopLocation;
        
        public PureLocation teamShopLocation;
        
        public PureLocation bedBottomLocation;
        
        public PureLocation bedTopLocation;
        
        public int bedFacing;
        
        public PureLocation innerBoundBoxOne;
        
        public PureLocation innerBoundBoxTwo;
        
        public TeamColor color() {
            Preconditions.checkNotNull(color, "Team has no color set");
            return TeamColor.of(color);
        }
        
        public void color(TeamColor color) {
            if (color == null) {
                this.color = null;
                return;
            }
            
            this.color = color.toString();
        }
    } 
}
