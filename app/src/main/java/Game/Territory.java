package Game;
import java.util.*;

/**
 * Created by brb55_000 on 2/6/2015.
 */
public class Territory {

    public Territory() {
        // Empty
    }
    // Copy constructor
    public Territory(Territory b) {
        this.neighbors = b.neighbors;
        this.units = b.units;
        this.owner = b.owner;
        this.neighbors = new Vector<Territory>(b.neighbors);
        this.units = new Vector<Unit>(b.units);
        if (b.owner != null) {
            this.owner = new Player(b.owner);
        }
        this.power = b.power;
        this.x = b.x;
        this.y = b.y;
        this.height = b.height;
        this.terrainType = b.terrainType;
    }

    public enum TerrainType {
        Ocean,
        Grassland,
        Mountain,
        Desert,
        Forest
    }

    public Vector<Territory> neighbors = new Vector<Territory>(); ///< Pointers to neighbor territories
    public Vector<Unit> units = new Vector<Unit>(); ///< Pointer to residing armies
    public Player owner = null; ///< Owning player
    public int power = 0; ///< Power of the territory
    public int economy = 0;
    public float x; ///< x coordinate of center
    public float y; ///< y coordinate of center
    public float height; ///< Terrain height value TODO: Use this for something maybe? Or remove it?
    public TerrainType terrainType; //< Type of terrain TODO: Use this for something

    public void resolveControl() {
        /* Random randomNumberGenerator = new Random();
        int defense_bonus = 1;
        Army defender = armies.get(0);
        Army attacker = armies.get(1);
        while( !defender.units.isEmpty() ) {
            if( attacker.units.isEmpty() ) break;
            else {
                // Generates a number between 1 and 6 ( the argument is 7, because it's exclusive of the max )
                int defenderRoll = randomNumberGenerator.nextInt( 7 );
                int attackerRoll = randomNumberGenerator.nextInt( 7 );
                if( defenderRoll < attackerRoll ) defender.units.remove( defender.units.size() - 1 );
                else attacker.units.remove( defender.units.size() - 1 );
            }
        }

        // if the attacker still has units, then the attacker won, change possession
        if( !attacker.units.isEmpty() ) {
            defender.owner.territories.remove( this );
            attacker.owner.territories.add( this );
        }*/
    }
    public double distance;///< used for PathFinding
    public boolean visited;///< used for PathFinding
}
