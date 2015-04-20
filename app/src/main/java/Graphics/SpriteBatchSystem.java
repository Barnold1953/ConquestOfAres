package Graphics;

import android.graphics.PointF;
import android.util.Log;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Vector;

import Game.Unit;

/**
 * Created by Nathan on 1/12/2015.
 */
public class SpriteBatchSystem {
    static float dotProduct(PointF p1, PointF p2){
        return p1.x * p2.x + p1.y * p2.y;
    }

    static PointF normalize(PointF p){
        double total = Math.sqrt(p.x*p.x+p.y*p.y);
        p.x /= total;
        p.y /= total;
        return p;
    }

    public enum BufferType{
        Vertices, TextureCoordinates, Colors
    }

    public static Vector sprites = new Vector();

    public static class sprite{
        int texture;
        FloatBuffer vBuf;
        FloatBuffer tBuf;
        ByteBuffer cBuf;
    }

    public static void Initialize(int aCount, int iCount, int mCount){
        GeometryHelper.initializeMaster();
        GeometryHelper.initializeSoldier(aCount, iCount, mCount);
    }

    public static void addSprite(String name, Quadrilateral quadrilateral, int rid){
        GeometryHelper.addToBatch(quadrilateral, name);
        if(!(sprites.contains(name))){
            sprites.add(name);
        }
    }

    public static void clear() {
        sprites.clear();
        GeometryHelper.clear();
    }

    public static sprite getSprite(String name){
        sprite s = new sprite();

        s.texture = TextureHelper.getTexture(name);
        s.vBuf = GeometryHelper.getVertBuff(name);
        s.tBuf = GeometryHelper.getTextBuff(name);
        s.cBuf = GeometryHelper.getColorBuff(name);

        return s;
    }

    public static void addUnit(Unit u, float x, float y, byte[] color){
        Quadrilateral quad = new Quadrilateral();
        switch (u.type){
            case soldier_move:
                double angle;
                float yDiff, xDiff;
                if (u.destination != u.location) {
                    yDiff = u.destination.y - u.location.y;
                    xDiff = u.destination.x - u.location.x;
                    PointF p = new PointF(0.0f, 1.0f);
                    PointF p1 = new PointF(xDiff, yDiff);
                    p1 = normalize(p1);
                    float dp = dotProduct(p, p1);
                    angle = Math.acos(dp);
                    if(xDiff > 0.0f) angle = -angle;
                    u.angle = angle;
                }
                quad = Quadrilateral.getQuad(quad, x, y, 0, .1f, .1f, color, u.angle);
                addSprite("soldier_move", quad, TextureHelper.getTexture("soldier_move"));
                break;
            case soldier_idle:
                quad = Quadrilateral.getQuad(quad, x, y, 0, .1f, .1f, color, u.angle);
                addSprite("soldier_idle", quad, TextureHelper.getTexture("soldier_idle"));
                break;
            case soldier_attack:
                quad = Quadrilateral.getQuad(quad, x, y, 0, .1f, .1f, color, u.angle);
                addSprite("soldier_attack", quad, TextureHelper.getTexture("soldier_attack"));
                break;
        }
    }
}
