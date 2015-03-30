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
        this.neighbors = new Vector<Territory>(b.neighbors);
        if (b.army != null) {
            this.army = new Army(b.army);
        }
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
    public Army army = null; ///< Pointer to residing army
    public Player owner = null; ///< Owning player
    public int power = 0; ///< Power of the territory
    public float x; ///< x coordinate of center
    public float y; ///< y coordinate of center
    public float height; ///< Terrain height value TODO: Use this for something maybe? Or remove it?
    public TerrainType terrainType; //< Type of terrain TODO: Use this for something
    public double distance;///< used for PathFinding
    public boolean visited;///< used for PathFinding
}
