package Game;

import java.util.Enumeration;

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
        destination[0] = 0;
        destination[0] = 0;
        type = t;
    }

    Type type;
    float[] location = new float[2];
    float[] destination = new float[2];
    // when health reaches 0, soldier is removed from army.units
    float health = 100.0f;
    // armor will divide the attacker's damage and then subtract that # from health
    float armor;
    // damage will increase or decrease based on the type of units fighting
    float damage;
}
