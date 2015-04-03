package Graphics;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Vector;

import Game.Unit;

/**
 * Created by Nathan on 1/12/2015.
 */
public class SpriteBatchSystem {
    public enum BufferType{
        Vertices, TextureCoordinates, Colors
    }

    public static Vector sprites = new Vector();
    public static class sprite{
        int texture;
        FloatBuffer vBuf;
        FloatBuffer tBuf;
        FloatBuffer cBuf;
    }
    /*public class sprite{
        int texture;
        BatchGeometry geo;
    }

    private static HashMap<String, sprite> sprites = new HashMap<>();
*/
    public static void Initialize(int count){
        GeometryHelper.initializeMaster();
        GeometryHelper.initializeSoldier(count);
    }

    public static void addSprite(String name, Quadrilateral quadrilateral, int rid){
        /*sprite s;
        if(sprites.containsKey(name)){
            s = sprites.get(name);
        }
        else {
            s = new sprite();
            s.texture = TextureHelper.getTexture(name);
        }
        GeometryHelper.addToBatch(quadrilateral, name);
        sprites.put(name, s);*/

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

    public static FloatBuffer getBuffer(String name, BufferType type){
        switch (type){
            case Vertices:
                return GeometryHelper.getVertBuff(name);
            case TextureCoordinates:
                return GeometryHelper.getTextBuff(name);
            case Colors:
                return GeometryHelper.getColorBuff(name);
        }
        Log.d("Sprite", "Return failure");
        FloatBuffer tmp = ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder()).asFloatBuffer();
        return tmp;
    }

    public static void removeSprite(String name, Quadrilateral quadrilateral){
        if(!(sprites.contains(name))){
            return;
        }
        GeometryHelper.removeFromBatch(name, quadrilateral);
    }

    public static void addUnit(Unit.Type type, float x, float y){
        Quadrilateral quad = new Quadrilateral();
        float[] color = {255,255,255,255};
        switch (type){
            case soldier:
                quad = Quadrilateral.getQuad(quad, x, y, 0, .1f, .1f, color);
                addSprite("soldier", quad, TextureHelper.getTexture("soldier"));
        }
    }
}
