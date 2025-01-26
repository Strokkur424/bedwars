package net.strokkur.bedwars.paper.map.data;

import com.google.common.base.Preconditions;
import net.strokkur.bedwars.paper.util.PureLocation;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class MapData implements Cloneable {

    public int teamAmount = 0;

    public int playersPerTeam = 0;

    public List<TeamData> teamData = new ArrayList<>();

    public List<PureLocation> diamondGenerators = new ArrayList<>();

    public List<PureLocation> emeraldGenerators = new ArrayList<>();

    public PureLocation boundCornerOne = PureLocation.empty();

    public PureLocation boundCornerTwo = PureLocation.empty();
    
    public double ironSpawnRate = 1.0d;

    @Override
    public MapData clone() {
        try {
            final MapData cloned = (MapData) super.clone();
            cloned.teamData = new ArrayList<>(this.teamData.stream().map(TeamData::clone).toList());
            cloned.diamondGenerators = new ArrayList<>(this.diamondGenerators.stream().map(PureLocation::clone).toList());
            cloned.emeraldGenerators = new ArrayList<>(this.emeraldGenerators.stream().map(PureLocation::clone).toList());
            cloned.boundCornerOne = this.boundCornerOne.clone();
            cloned.boundCornerTwo = this.boundCornerTwo.clone();
            return cloned;
        }
        catch (CloneNotSupportedException failedClone) {
            throw new Error(failedClone);
        }
    }

    @ConfigSerializable
    public static class TeamData implements Cloneable {

        private String color = null;
        
        public PureLocation spawnLocation = PureLocation.empty();

        public PureLocation generatorLocation = PureLocation.empty();

        public PureLocation playerShopLocation = PureLocation.empty();

        public PureLocation teamShopLocation = PureLocation.empty();

        public PureLocation bedBottomLocation = PureLocation.empty();

        public PureLocation bedTopLocation = PureLocation.empty();

        public int bedFacing = 0;

        public PureLocation innerBoundBoxOne = PureLocation.empty();

        public PureLocation innerBoundBoxTwo = PureLocation.empty();

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

        @Override
        public TeamData clone() {
            try {
                final TeamData cloned = (TeamData) super.clone();
                cloned.spawnLocation = this.spawnLocation.clone();
                cloned.generatorLocation = this.generatorLocation.clone();
                cloned.playerShopLocation = this.playerShopLocation.clone();
                cloned.teamShopLocation = this.teamShopLocation.clone();
                cloned.bedBottomLocation = this.bedBottomLocation.clone();
                cloned.bedTopLocation = this.bedTopLocation.clone();
                cloned.innerBoundBoxOne = this.innerBoundBoxOne.clone();
                cloned.innerBoundBoxTwo = this.innerBoundBoxTwo.clone();
                return cloned;
            }
            catch (CloneNotSupportedException failedClone) {
                throw new Error(failedClone);
            }
        }
    }
}
