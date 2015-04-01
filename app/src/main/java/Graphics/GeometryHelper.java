package Graphics;

/**
 * Created by Nathan on 3/25/2015.
 */
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;

/**
 * Created by Nathan on 1/20/2015.
 */
public class GeometryHelper {
    private static HashMap<String, BatchGeometry> BatchMap = new HashMap<>();
    private static int unitCount = 0, totalUnits = 0;

    private final static float[] quad = {0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f};
    private final static float[] quadNormals = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f};
    private final static float[] quadTextCoords = {0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f};
    private final static float[] quadColors = {1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f};

    private final static int mBytesPerFloat = 4;

    private static float[] addToFloatArray(float[] destination, float[] source){
        float[] temp = new float[destination.length + source.length];
        System.arraycopy(destination, 0, temp, 0, destination.length);
        //System.arraycopy(source, 0, temp, destination.length, source.length);
        destination = new float[temp.length];
        System.arraycopy(temp, 0, destination, 0, temp.length);
        System.arraycopy(source, 0, destination, destination.length-source.length, source.length);
        temp = new float[0];
        source = new float[0];
        return destination;
        //destination = new float[temp.length];
        //System.arraycopy(temp, 0, destination, 0, temp.length);

        //return destination;
    }

    private static float[] removeFromFloatArray(float[] source, int index, int amount){
        float[] temp = new float[source.length - amount];
        System.arraycopy(source, 0, temp, 0, index);
        System.arraycopy(source, index + amount, temp, index, source.length - index - amount);
        return temp;
        //System.arraycopy(temp, 0, source, 0, temp.length);
    }

    public static void initializeMaster(){
        float[] ftmp = {255f, 255f, 255f, 255f};
        Quadrilateral quad = new Quadrilateral();
        quad = Quadrilateral.getQuad(quad, -1, -1, 0, 2, 2, ftmp);
        BatchGeometry bg = new BatchGeometry();
        bg.vertices = new float[18];
        bg.textureCoordinates = new float[12];
        bg.colors = new float[24];

        float[] newV = {quad.x,quad.y+quad.height,quad.z,quad.x,quad.y,quad.z,quad.x+quad.width,quad.y,quad.z,quad.x+quad.width,quad.y,quad.z,quad.x+quad.width,quad.y+quad.height,quad.z,quad.x,quad.y+quad.height,quad.z};

        Integer tmp = bg.vertices.length;

        float[] temp = new float[quadColors.length];
        for(int i = 0; i < quadColors.length; i++){
            temp[i] = quad.color[i % 4];
        }

        //Log.d("Geometry3", tmp.toString());
        System.arraycopy(newV, 0, bg.vertices, unitCount * 18, newV.length);
        System.arraycopy(quadTextCoords, 0, bg.textureCoordinates, unitCount*12, quadTextCoords.length);
        for(int i = 0; i < quadTextCoords.length; i+=2){
            bg.textureCoordinates[i+1] = 1.0f - bg.textureCoordinates[i+1];
        }
        System.arraycopy(temp, 0, bg.colors, unitCount*24, temp.length);

        if(bg.vBuff != null && bg.tcBuff != null && bg.cBuff != null) {
            bg.vBuff.reset();
            bg.tcBuff.reset();
            bg.cBuff.reset();
        }

        bg.vBuff = ByteBuffer.allocateDirect(bg.vertices.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        bg.tcBuff = ByteBuffer.allocateDirect(bg.textureCoordinates.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        bg.cBuff = ByteBuffer.allocateDirect(bg.colors.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();

        bg.vBuff.put(bg.vertices).position(0);
        bg.tcBuff.put(bg.textureCoordinates).position(0);
        bg.cBuff.put(bg.colors).position(0);

        BatchMap.put("master", bg);
    }

    public static void initializeSoldier(int count){
        BatchGeometry bg = new BatchGeometry();
        bg.vertices = new float[count * 18];
        bg.textureCoordinates = new float[count * 12];
        bg.colors = new float[count * 24];

        totalUnits = count;

        BatchMap.put("soldier", bg);
        //Log.d("Geometry2", ((Integer)BatchMap.get("soldier").vertices.length).toString());
    }

    public static void clear(){
        BatchMap.clear();
        unitCount = 0;
    }

    public static void addToBatch(Quadrilateral quad, String name){
        BatchGeometry bg;
        if(BatchMap.containsKey(name)) {
            bg = BatchMap.get(name);
        }
        else {
            bg = new BatchGeometry();
            bg.vertices = new float[18 * totalUnits];
            bg.textureCoordinates = new float[12 * totalUnits];
            bg.colors = new float[24 * totalUnits];
        }

        float[] newV = {quad.x,quad.y+quad.height,quad.z,quad.x,quad.y,quad.z,quad.x+quad.width,quad.y,quad.z,quad.x+quad.width,quad.y,quad.z,quad.x+quad.width,quad.y+quad.height,quad.z,quad.x,quad.y+quad.height,quad.z};

        //bg.vertices = addToFloatArray(bg.vertices, newV);
        //bg.textureCoordinates = addToFloatArray(bg.textureCoordinates, quadTextCoords);
        float[] temp = new float[quadColors.length];
        for(int i = 0; i < quadColors.length; i++){
            temp[i] = quad.color[i % 4];
        }
        //bg.colors = addToFloatArray(bg.colors, temp);

        Integer tmp = bg.vertices.length;

        //Log.d("Geometry3", tmp.toString());
        System.arraycopy(newV, 0, bg.vertices, unitCount * 18, newV.length);
        System.arraycopy(quadTextCoords, 0, bg.textureCoordinates, unitCount*12, quadTextCoords.length);
        System.arraycopy(temp, 0, bg.colors, unitCount*24, temp.length);

        unitCount++;

        BatchMap.put(name, bg);
    }

    public static void allocateBuffs(int puc){
        BatchGeometry bg = BatchMap.get("soldier");

        if(puc < unitCount || bg.vBuff == null) {
            if(bg.vBuff != null && bg.tcBuff != null && bg.cBuff != null) {
                bg.vBuff.reset();
                bg.tcBuff.reset();
                bg.cBuff.reset();
            }

            bg.vBuff = ByteBuffer.allocateDirect(bg.vertices.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
            bg.tcBuff = ByteBuffer.allocateDirect(bg.textureCoordinates.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
            bg.cBuff = ByteBuffer.allocateDirect(bg.colors.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        }

        bg.vBuff.put(bg.vertices).position(0);
        bg.tcBuff.put(bg.textureCoordinates).position(0);
        bg.cBuff.put(bg.colors).position(0);

        BatchMap.put("soldier", bg);
    }

    public static void removeFromBatch(String name, Quadrilateral quad){
        BatchGeometry bg = BatchMap.get(name);

        for(int i = 0; i < bg.vertices.length; i+=18){
            if(bg.vertices[i] == quad.x && bg.vertices[i+1] == quad.y+quad.height && bg.vertices[i+2] == quad.z){
                bg.vertices = removeFromFloatArray(bg.vertices, i, 18);
                bg.textureCoordinates = removeFromFloatArray(bg.textureCoordinates, (i / 18) * 12, 12);
                bg.colors = removeFromFloatArray(bg.colors, (i / 18) * 24, 24);
                break;
            }
            bg.vBuff.put(bg.vertices).position(0);
            bg.tcBuff.put(bg.textureCoordinates).position(0);
            bg.cBuff.put(bg.colors).position(0);
        }
    }

    public static FloatBuffer getFrameTexture(String name, float width, float height, float fWidth, float fHeight, int frameX, int frameY){
        float[] temp = new float[BatchMap.get(name).textureCoordinates.length];
        System.arraycopy(BatchMap.get(name).textureCoordinates, 0, temp, 0, temp.length);

        for(int i = 0; i < temp.length; i+=2){
            temp[i] = ((temp[i] * fWidth) + (frameX * fWidth)) / width;
            temp[i+1] = ((temp[i+1] * fHeight) + (frameY * fHeight)) / height;
        }

        BatchMap.get(name).tcBuff.put(temp).position(0);
        return BatchMap.get(name).tcBuff;
    }

    public static FloatBuffer getVertBuff(String name){
        return BatchMap.get(name).vBuff;
    }
    public static FloatBuffer getColorBuff(String name){
        return BatchMap.get(name).cBuff;
    }
    public static FloatBuffer getTextBuff(String name){
        return BatchMap.get(name).tcBuff;
    }
    public static int getVerticesCount(String name){
        return BatchMap.get(name).vertices.length;
    }
}
