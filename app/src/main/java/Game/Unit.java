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
        location = new float[] {x,y};
        destination = new float[] {x,y};
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

    public void destinationStep(){
        frame = 0;
        location = destination;
        if(path != null && !path.isEmpty()) {
            path.remove(path.capacity()-1);
            if(!path.isEmpty()){
                destination = new float[] {path.get(path.capacity()-1).x, path.get(path.capacity()-1).y};
            }
        }
    }
}
