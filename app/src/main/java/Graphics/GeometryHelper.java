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
    //private final static float[] quadColors = {1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f};

    private final static int mBytesPerFloat = 4;

    public static void initializeMaster(){
        byte[] ftmp = {(byte)255, (byte)255, (byte)255};
        Quadrilateral quad = new Quadrilateral();
        quad = Quadrilateral.getQuad(quad, -1, -1, 0, 2, 2, ftmp);
        BatchGeometry bg = new BatchGeometry();
        bg.vertices = new float[18];
        bg.textureCoordinates = new float[12];
        bg.colors = new byte[18];

        float[] newV = {quad.x,quad.y+quad.height,quad.z,
                quad.x,quad.y,quad.z,
                quad.x+quad.width,quad.y,quad.z,
                quad.x+quad.width,quad.y,quad.z,
                quad.x+quad.width,quad.y+quad.height,quad.z,
                quad.x,quad.y+quad.height,quad.z};

        float[] temp = new float[bg.colors.length];
        for(int i = 0; i < bg.colors.length; i++){
            temp[i] = quad.color[i % 3];
        }

        //Log.d("Geometry3", tmp.toString());
        System.arraycopy(newV, 0, bg.vertices, 0, newV.length);
        System.arraycopy(quadTextCoords, 0, bg.textureCoordinates, 0, quadTextCoords.length);
        for(int i = 0; i < quadTextCoords.length; i+=2){
            bg.textureCoordinates[i+1] = 1.0f - bg.textureCoordinates[i+1];
        }
        byte[] tempB = new byte[bg.colors.length];
        System.arraycopy(tempB, 0, bg.colors, 0, tempB.length);

        if(bg.vBuff != null && bg.tcBuff != null && bg.cBuff != null) {
            bg.vBuff.clear();
            bg.tcBuff.clear();
            bg.cBuff.clear();
        }

        bg.vBuff = ByteBuffer.allocateDirect(bg.vertices.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        bg.tcBuff = ByteBuffer.allocateDirect(bg.textureCoordinates.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        bg.cBuff = ByteBuffer.allocateDirect(bg.colors.length).order(ByteOrder.nativeOrder());

        bg.vBuff.put(bg.vertices).position(0);
        bg.tcBuff.put(bg.textureCoordinates).position(0);
        bg.cBuff.put(bg.colors).position(0);

        BatchMap.put("master", bg);
    }

    public static void initializeSoldier(int count){
        BatchGeometry bg = new BatchGeometry();
        bg.vertices = new float[count * 18];
        bg.textureCoordinates = new float[count * 12];
        bg.colors = new byte[count * 18];

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
            bg.colors = new byte[18 * totalUnits];
        }

        float[] newV = new float[] {quad.x,quad.y+quad.height,quad.z,quad.x,quad.y,quad.z,quad.x+quad.width,quad.y,quad.z,quad.x+quad.width,quad.y,quad.z,quad.x+quad.width,quad.y+quad.height,quad.z,quad.x,quad.y+quad.height,quad.z};

        byte[] temp = new byte[18];
        for(int i = 0; i < temp.length; i++){
            temp[i] = quad.color[i % 3];
        }
        //bg.colors = addToFloatArray(bg.colors, temp);

        System.arraycopy(newV, 0, bg.vertices, unitCount * 18, newV.length);
        System.arraycopy(temp, 0, bg.colors, unitCount*18, temp.length);

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
            bg.cBuff = ByteBuffer.allocateDirect(bg.colors.length).order(ByteOrder.nativeOrder());
        }

        bg.vBuff.put(bg.vertices).position(0);
        bg.tcBuff.put(bg.textureCoordinates).position(0);
        bg.cBuff.put(bg.colors).position(0);

        BatchMap.put("soldier", bg);
    }

    //public static FloatBuffer getFrameTexture(String name, float width, float height, float fWidth, float fHeight, int frameX, int frameY){
    public static void setFrameTexture(String name, float width, float height, float fWidth, float fHeight, int frame){
        int frameX = (int)width / (int)fWidth;
        int frameY = (int)height / (int)fHeight;
        frameY = (frame / frameX) % frameY;
        frameX = frame % frameX;

        float[] temp = new float[BatchMap.get(name).textureCoordinates.length];
        System.arraycopy(BatchMap.get(name).textureCoordinates, 0, temp, 0, temp.length);

        for(int i = 0; i < temp.length; i+=2){
            temp[i] = ((quadTextCoords[i % quadTextCoords.length] * fWidth) + (frameX * fWidth)) / width;
            temp[i+1] = ((quadTextCoords[(i+1) % quadTextCoords.length] * fHeight) + (frameY * fHeight)) / height;
        }

        BatchMap.get(name).tcBuff.put(temp).position(0);
    }

    public static FloatBuffer getVertBuff(String name){
        return BatchMap.get(name).vBuff;
    }
    public static ByteBuffer getColorBuff(String name){
        return BatchMap.get(name).cBuff;
    }
    public static FloatBuffer getTextBuff(String name){
        return BatchMap.get(name).tcBuff;
    }
    public static int getVerticesCount(String name){
        return BatchMap.get(name).vertices.length;
    }
}