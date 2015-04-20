package Graphics;

/**
 * Created by Nathan on 3/25/2015.
 */
import android.graphics.PointF;
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

    public static void initializeSoldier(int attackCount, int idleCount, int moveCount){
        BatchGeometry attackBatch = new BatchGeometry(), idleBatch = new BatchGeometry(), moveBatch = new BatchGeometry();
        attackBatch.vertices = new float[attackCount * 18];
        attackBatch.textureCoordinates = new float[attackCount * 18];
        attackBatch.colors = new byte[attackCount * 18];
        idleBatch.vertices = new float[idleCount * 18];
        idleBatch.textureCoordinates = new float[idleCount * 18];
        idleBatch.colors = new byte[idleCount * 18];
        moveBatch.vertices = new float[moveCount * 18];
        moveBatch.textureCoordinates = new float[moveCount * 18];
        moveBatch.colors = new byte[moveCount * 18];

        attackBatch.total = attackCount;
        attackBatch.count = 0;
        idleBatch.total = idleCount;
        idleBatch.count = 0;
        moveBatch.total = moveCount;
        moveBatch.count = 0;

        BatchMap.put("soldier_attack", attackBatch);
        BatchMap.put("soldier_idle", idleBatch);
        BatchMap.put("soldier_move", moveBatch);
        //Log.d("Geometry2", ((Integer)BatchMap.get("soldier").vertices.length).toString());
    }

    public static void clear(){
        BatchMap.clear();
    }

    public static void addToBatch(Quadrilateral quad, String name){
        BatchGeometry bg = BatchMap.get(name);

        float[] newV = new float[] {quad.x,quad.y+quad.height,quad.z,
                                    quad.x,quad.y,quad.z,
                                    quad.x+quad.width,quad.y,quad.z,
                                    quad.x+quad.width,quad.y,quad.z,
                                    quad.x+quad.width,quad.y+quad.height,quad.z,
                                    quad.x,quad.y+quad.height,quad.z};


        if(quad.angle != 0) {
            PointF center = new PointF((quad.width / 2 + quad.x), (quad.height / 2 + quad.y));
            PointF local = new PointF();
            PointF rotate = new PointF();
            for (int i = 0; i < newV.length; i += 3) {
                local.x = newV[i] - center.x;
                local.y = newV[i + 1] - center.y;
                rotate.x = (float) (local.x * Math.cos(quad.angle) - local.y * Math.sin(quad.angle));
                rotate.y = (float) (local.x * Math.sin(quad.angle) + local.y * Math.cos(quad.angle));
                newV[i] = center.x + rotate.x;
                newV[i + 1] = center.y + rotate.y;
            }
        }

        byte[] temp = new byte[18];
        for(int i = 0; i < temp.length; i++){
            temp[i] = quad.color[i % 3];
        }
        //bg.colors = addToFloatArray(bg.colors, temp);

        System.arraycopy(newV, 0, bg.vertices, bg.count * 18, newV.length);
        System.arraycopy(temp, 0, bg.colors, bg.count*18, temp.length);

        bg.count++;

        BatchMap.put(name, bg);
    }

    public static void allocateBuffs(int pac, int pic, int pmc){
        String[] buffs = {"soldier_attack", "soldier_idle", "soldier_move"};
        int[] pCounts = {pac, pic, pmc};

        for(int i = 0; i < buffs.length; i++) {
            BatchGeometry bg = BatchMap.get(buffs[i]);
            if (pCounts[i] < bg.count || bg.vBuff == null) {
                if (bg.vBuff != null && bg.tcBuff != null && bg.cBuff != null) {
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

            BatchMap.put(buffs[i], bg);
        }
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