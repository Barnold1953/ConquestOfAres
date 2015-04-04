package Game;

import java.nio.ByteBuffer;
import java.util.*;

import Graphics.TerritoryMesh;

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
        this.neighbors = b.neighbors;
        this.units = b.units;
        this.owner = b.owner;
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
    public float mixWeight = 0.0f; ///< When 0, its player color. When 1, its secondaryColor
    public float[] secondaryColor = new float[3];
    public boolean isSelected = false;

    // Animation stuff
    private final float MAX_BLEND = 0.7f;
    private final float MIN_BLEND = 0.35f;
    private final float BLEND_SPEED = 0.01f;
    private boolean m_isIncreasing = true;

    public void select() {
        if (!isSelected) {
            isSelected = true;
            m_isIncreasing = true;
        }
    }

    public void unselect() {
        isSelected = false;
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

    public boolean addUnit(float x, float y, Unit.Type type) {
        if(owner.placeableUnits > 0) {
            Random r = new Random();
            float spread = 30.0f;
            int direction = r.nextInt();
            Unit unit;
            switch (direction%4){
                case 0:
                    Log.d("addUnit", "case 0");
                    unit = new Unit(x + r.nextFloat() * spread, y + r.nextFloat() * spread, Unit.Type.soldier);
                    break;
                case 1:
                    Log.d("addUnit", "case 1");
                    unit = new Unit(x - r.nextFloat() * spread, y + r.nextFloat() * spread, Unit.Type.soldier);
                    break;
                case 2:
                    Log.d("addUnit", "case 2");
                    unit = new Unit(x + r.nextFloat() * spread, y - r.nextFloat() * spread, Unit.Type.soldier);
                    break;
                default:
                    Log.d("addUnit", "case 3");
                    unit = new Unit(x - r.nextFloat() * spread, y - r.nextFloat() * spread, Unit.Type.soldier);
                    break;
            }
            units.add(unit);
            owner.placeableUnits--;
            return true;
        }
        return false;
    }

    public boolean removeUnits(float x, float y, Unit.Type type) {
        Unit unit = null;
        for( Unit unitOfType : units ) {
            if(type == unitOfType.type) {
                unit = unitOfType;
            }
        }
        if(unit != null) {
            units.remove(units.firstElement());
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
