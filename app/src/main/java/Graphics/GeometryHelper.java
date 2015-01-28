package Graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Nathan on 1/20/2015.
 */
public class GeometryHelper {
    final float[] cube = {
            0.0f, 1.000f, 1.000f, 0.0f, 0.0f, 1.000f, 1.000f, 0.0f, 1.000f, 1.000f, 1.000f, 1.000f,         // v1-v2-v3-v0 (front)

            1.000f, 1.000f, 1.000f, 1.000f, 0.0f, 1.000f, 1.000f, 0.0f, 0.0f, 1.000f, 1.000f, 0.0f,         // v0-v3-v4-v5 (right)

            0.0f, 1.000f, 0.0f, 0.0f, 1.000f, 1.000f, 1.000f, 1.000f, 1.000f, 1.000f, 1.000f, 0.0f,         // v6-v1-v0-v5 (top)

            0.0f, 1.000f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.000f, 0.0f, 1.000f, 1.000f,                 // v6-v7-v2-v1 (left)

            0.0f, 0.0f, 0.0f, 1.000f, 0.0f, 0.0f, 1.000f, 0.0f, 1.000f, 0.0f, 0.0f, 1.000f,                 // v7-v4-v3-v2 (bottom)

            1.000f, 1.000f, 0.0f, 1.000f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.000f, 0.0f                  // v5-v4-v7-v6 (back)
    };

    final float[] cubeNormals = {
                 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,    // v1-v2-v3-v0 (front)

                1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,     // v0-v3-v4-v5 (right)

                0, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,        // v6-v1-v0-v5 (top)

                -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, // v6-v7-v2-v1 (left)

                0, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f,    // v7-v4-v3-v2 (bottom)

                0, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f     // v5-v4-v7-v6 (back)
    };

    final float[] cubeTextCoords = {
                0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,

                0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,

                0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,

                0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,

                0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,

                0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,
    };

    final float[] cubeColors = {
                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,

                0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f,

                0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,

                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f,

                1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f
    };

    private final int mBytesPerFloat = 4;
    private final int mStrideBytes = 7 * mBytesPerFloat;
    private final int mPositionOffset = 0;
    private final int mPositionDataSize = 3;
    private final int mColorOffset = 3;
    private final int mColorDataSize = 4;
    public FloatBuffer mVertexBuffer;
    public FloatBuffer mColorBuffer;
    public FloatBuffer mTextCoordBuffer;

    public void createBuffers(){
        mVertexBuffer = ByteBuffer.allocateDirect(cube.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mColorBuffer = ByteBuffer.allocateDirect(cubeColors.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextCoordBuffer = ByteBuffer.allocateDirect(cubeTextCoords.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();

        mVertexBuffer.put(cube).position(0);
        mColorBuffer.put(cubeColors).position(0);
        mTextCoordBuffer.put(cubeTextCoords).position(0);
    }
}
