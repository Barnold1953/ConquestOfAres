package Game;
import java.nio.ByteBuffer;
import java.util.*;

import Graphics.TerritoryMesh;

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
        units = new Vector<Unit>(b.units);
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
    public Vector<Unit> units = new Vector<Unit>(); ///< Pointer to residing army
    public Player owner = null; ///< Owning player
    public int power = 0; ///< Power of the territory
    public float x; ///< x coordinate of center
    public float y; ///< y coordinate of center
    public float height; ///< Terrain height value TODO: Use this for something maybe? Or remove it?
    public TerrainType terrainType; //< Type of terrain TODO: Use this for something
    public double distance;///< used for PathFinding
    public boolean visited;///< used for PathFinding
    public int texture = 0;
    public int textureWidth = 1;
    public int textureHeight = 1;
    public int textureX = 999999999;
    public int textureY = 999999999;
    public int maxX = 0; ///< Used in map generation only
    public int maxY = 0; ///< Used in map generation only
    public int index = -1;
    public ByteBuffer pixelBuffer = null;
    public TerritoryMesh mesh = null;
}
