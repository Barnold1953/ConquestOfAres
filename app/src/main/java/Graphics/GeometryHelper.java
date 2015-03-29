package Graphics;

/**
 * Created by Nathan on 3/25/2015.
 */
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
    //private final static float[] quadTextCoords = {0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
    private final static float[] quadTextCoords = {0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f};
    private final static float[] quadColors = {1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f};

    private final static int mBytesPerFloat = 4;

    private static float[] addToFloatArray(float[] destination, float[] source){
        float[] temp = new float[destination.length + source.length];
        System.arraycopy(destination, 0, temp, 0, destination.length);
        System.arraycopy(source, 0, temp, destination.length, source.length);
        return temp;
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

    public static void addToBatch(Quadrilateral quad, String name){
        BatchGeometry bg;
        if(BatchMap.containsKey(name)) {
            bg = BatchMap.get(name);
        }
        else {
            bg = new BatchGeometry();
            //BatchMap.put(name, bg);
            bg.vertices = new float[0];
            bg.textureCoordinates = new float[0];
            bg.colors = new float[0];
        }

        float[] newV = {quad.x,quad.y+quad.height,quad.z,quad.x,quad.y,quad.z,quad.x+quad.width,quad.y,quad.z,quad.x+quad.width,quad.y,quad.z,quad.x+quad.width,quad.y+quad.height,quad.z,quad.x,quad.y+quad.height,quad.z};

        bg.vertices = addToFloatArray(bg.vertices, newV);
        bg.textureCoordinates = addToFloatArray(bg.textureCoordinates, quadTextCoords);
        float[] temp = new float[quadColors.length];
        for(int i = 0; i < quadColors.length; i++){
            temp[i] = quad.color[i % 4];
        }
        bg.colors = addToFloatArray(bg.colors, temp);

        bg.vBuff = ByteBuffer.allocateDirect(bg.vertices.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        bg.tcBuff = ByteBuffer.allocateDirect(bg.textureCoordinates.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        bg.cBuff = ByteBuffer.allocateDirect(bg.colors.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        bg.vBuff.put(bg.vertices).position(0);
        bg.tcBuff.put(bg.textureCoordinates).position(0);
        bg.cBuff.put(bg.colors).position(0);

        BatchMap.put(name, bg);
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
