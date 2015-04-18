package Game;

import android.graphics.PointF;

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

    public Unit() {}; //stub constructor for Json parsing

    public Unit(float x, float y, Type t){
        hasMoved = true;
        location = new PointF(x,y);
        destination = new PointF(x,y);
        type = t;
    }

    public Type type;
    public PointF location = null;
    public PointF destination = null;
    public Vector<Territory> path = null;
    public int frame;
    // when health reaches 0, soldier is removed from army.units
    public float health = 100.0f;
    // armor will divide the attacker's damage and then subtract that # from health
    public float armor;
    // damage will increase or decrease based on the type of units fighting
    public float damage;

    public void destinationStep(){// calculates the location
        frame = 0;
        location = destination;
        if(path != null && !path.isEmpty()) {
            path.remove(path.size()-1);
            if(!path.isEmpty()){
                destination = new PointF(path.get(path.size()-1).x, path.get(path.size()-1).y);
            }
        }
    }
}
