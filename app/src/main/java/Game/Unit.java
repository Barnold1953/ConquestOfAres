package Game;

import java.util.Enumeration;
import java.util.Vector;

import Graphics.Quadrilateral;

/**
 * Created by brb55_000 on 3/6/2015.
 */
public class Unit {
    boolean hasMoved; ///< Can only move once per turn TODO: use this
    public enum Type{
        soldier, tank, airplane
    }

    public Unit(float x, float y, Type t){
        hasMoved = true;
        location[0] = x;
        location[1] = y;
        destination[0] = x;
        destination[0] = y;
        type = t;
    }

    public Type type;
    public float[] location = new float[2];
    public float[] destination = new float[2];
    public Vector<Territory> path;
    public int frame;
    // when health reaches 0, soldier is removed from army.units
    public float health = 100.0f;
    // armor will divide the attacker's damage and then subtract that # from health
    public float armor;
    // damage will increase or decrease based on the type of units fighting
    public float damage;

    public Quadrilateral getUnit(){
        Quadrilateral quad = new Quadrilateral();
        float[] color = {255,255,255,255};
        quad = Quadrilateral.getQuad(quad, location[0]+(((destination[0]-location[0]) / 10) * frame), location[1]+(((destination[1]-location[1]) / 10) * frame), 0, 0.1f, 0.1f, color);

        return quad;
    }
}
