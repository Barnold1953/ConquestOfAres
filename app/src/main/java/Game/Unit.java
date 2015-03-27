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

    Type type;
    float[] location = new float[2];
    float[] destination = new float[2];
    // TODO: Different stats?
    // TODO: Health?
    // TODO: Upgrades?
}
