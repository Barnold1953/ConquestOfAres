package Graphics;

/**
 * Created by Nathan on 3/25/2015.
 */

public class Quadrilateral {
    float x, y, z;
    float width, height;
    byte[] color;

    public static Quadrilateral getQuad(Quadrilateral quad, float xCoord, float yCoord, float zCoord, float w, float h, byte[] c){
        quad.x = xCoord;
        quad.y = yCoord;
        quad.z = zCoord;
        quad.width = w;
        quad.height = h;
        quad.color = c;

        return quad;
    }
}
