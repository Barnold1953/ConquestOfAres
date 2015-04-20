package Game;

import android.graphics.Point;
import android.graphics.PointF;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by brb55_000 on 3/6/2015.
 */
public class Unit {
    boolean hasMoved; ///< Can only move once per turn TODO: use this
    public enum Type{
        soldier_attack, soldier_idle, soldier_move
    }

    public Unit(float x, float y, Type t){
        hasMoved = true;
        location = new PointF(x,y);
        destination = new PointF(x,y);
        destination = location;
        type = t;

        wrapFrame = new Point(-1,-1);
    }

    public boolean inCombat = false;
    public Type type;
    public PointF location = null;
    public PointF destination = null;
    public Point wrapFrame;
    public Vector<Territory> path = null;
    public int speed = 100;
    public int turnRate = 5;
    public float angle = 0.0f;
    // when health reaches 0, soldier is removed from army.units
    public float health = 100.0f;
    // armor will divide the attacker's damage and then subtract that # from health
    public float armor;
    // damage will increase or decrease based on the type of units fighting
    public float damage;
}
