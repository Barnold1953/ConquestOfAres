package Game;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import Graphics.TerritoryMesh;

import android.graphics.PointF;
import android.util.Log;

/**
 * Created by brb55_000 on 2/6/2015.
 */
public class Territory {

    public Territory() {
        setSecondaryColor(1.0f, 1.0f, 1.0f);
    }
    // Copy constructor
    public Territory(Territory b) {
        for (Territory t : b.neighbors) {
            this.neighbors.add(t);
        }
        this.units = b.units;
        for (Unit u : b.units) {
            this.units.add(u);
        }
        this.owner = b.owner;
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
    public List<Unit> units = new ArrayList<>(); ///< Pointer to residing armies
    public Vector<Unit> selectedUnits = new Vector<>(); ///< Pointer to units selected for movement/attack
    public Player owner = null; ///< Owning player
    public int power = 0; ///< Power of the territory
    public float x; ///< x coordinate of center
    public float y; ///< y coordinate of center
    public float height; ///< Terrain height value TODO: Use this for something maybe? Or remove it?
    public TerrainType terrainType; //< Type of terrain TODO: Use this for something
    public float mixWeight = 0.0f; ///< When 0, its player color. When 1, its secondaryColor
    public float[] secondaryColor = new float[3];
    public boolean isSelected = false;

    // Animation stuff
    private final float MAX_BLEND = 0.7f;
    private final float MIN_BLEND = 0.35f;
    private final float BLEND_SPEED = 0.01f;
    private boolean m_isIncreasing = true;
    private static Random m_random = new Random(new Date().getTime());

    public void select() {
        if (!isSelected) {
            isSelected = true;
            m_isIncreasing = true;
        }
    }

    public void unselect() {
        isSelected = false;
    }

    public void selectNeighbors(boolean attacking) {
        for( Territory t : neighbors ) {
            if(attacking){
                if(t.owner != owner && t.owner != null){
                    t.select();
                }
            }
            else{
                if(t.owner == owner && t.owner != null){
                    t.select();
                }
            }
        }
    }

    public void unselectNeighbors() {
        for( Territory t : neighbors ) t.unselect();
    }

    public void updateAnimation() {
        if (isSelected) {
            if (m_isIncreasing) {
                mixWeight += BLEND_SPEED;
                if (mixWeight > MAX_BLEND) {
                    mixWeight = MAX_BLEND;
                    m_isIncreasing = false;
                }
            } else {
                mixWeight -= BLEND_SPEED;
                if (mixWeight < MIN_BLEND) {
                    mixWeight = MIN_BLEND;
                    m_isIncreasing = true;
                }
            }
        } else {
            mixWeight -= BLEND_SPEED;
            if (mixWeight < 0.0f) mixWeight = 0.0f;
        }
    }

    public void setSecondaryColor(float r, float g, float b) {
        secondaryColor[0] = r;
        secondaryColor[1] = g;
        secondaryColor[2] = b;
    }

    PointF getUnitPlace() {
        final float spread = 30.0f;
        return new PointF(x + (m_random.nextFloat() * 2.0f - 1.0f) * spread,
                          y + (m_random.nextFloat() * 2.0f - 1.0f) * spread);
    }

    public boolean addUnit(Unit.Type type) {
        if(owner.placeableUnits > 0) {
            PointF placementCoords = getUnitPlace();
            Unit unit = new Unit(placementCoords.x,placementCoords.y,type);
            //unit.destinationStep();
            synchronized (units) { units.add(unit); }
            owner.placeableUnits--;
            return true;
        }
        return false;
    }

    public boolean removeUnits(Unit.Type type) {
        Unit unit = null;
        for( Unit unitOfType : units ) {
            if(type == unitOfType.type) {
                unit = unitOfType;
            }
        }
        if(unit != null) {
            synchronized (units) { units.remove(units.get(0)); }
            owner.placeableUnits++;
            return true;
        }
        return false;
    }

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
