package Graphics;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Created by Nathan on 3/25/2015.
 */

public class BatchGeometry{
    float[] vertices, textureCoordinates;
    byte[] colors;
    FloatBuffer vBuff, tcBuff;
    ByteBuffer cBuff;
}
