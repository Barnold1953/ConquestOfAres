package Graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nathan on 2/5/2015.
 */
public class TextureHelper {
    private Map textureHandles= new HashMap();
    private HashMap<String, int[]> textures = new HashMap<String, int[]>();

    Context context;

    public TextureHelper(Context c){
        context = c;
    }

    public void DataToTexture(ByteBuffer data, String label, int width, int height){
        int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if(textureHandle[0] != 0) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_BYTE, data);


            if (textureHandles.containsKey(label)) {
                Log.d("Texture", "Texture with that label already exists");
                return;
            } else {
                textureHandles.put(label, textureHandle);
            }
        }
        else{
            Log.d("Texture", "Error loading texture");
        }
    }

    public int ImageToTexture(final Context context, final int resourceId){
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;	// No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);



        textures.put("texture1", textureHandle);
        return textureHandle[0];
    }

    public int getTexture(String label){
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);



        return textures.get(label)[0];
    }
}
