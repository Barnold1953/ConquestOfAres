package Graphics;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * Created by Nathan on 1/20/2015.
 */
public class GeometryHelper {
    final float[] cube = {
            0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,        // v1-v2-v3-v0 (front)

            1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.000f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f,        // v0-v3-v4-v5 (right)

            0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,       // v6-v1-v0-v5 (top)

            0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f,               // v6-v7-v2-v1 (left)

            0.0f, 0.0f, 0.0f, 1.000f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,               // v7-v4-v3-v2 (bottom)

            1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f,                  // v5-v4-v7-v6 (back)
    };

    final float[] cubeNormals = {
                 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,  // v1-v2-v3-v0 (front)

                1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // v0-v3-v4-v5 (right)

                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,       // v6-v1-v0-v5 (top)

                -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, // v6-v7-v2-v1 (left)

                0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f,  // v7-v4-v3-v2 (bottom)

                0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f,   // v5-v4-v7-v6 (back)
    };

    final float[] cubeTextCoords = {
                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,

                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,

                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,

                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,

                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,

                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f
    };

    final float[] cubeColors = {
                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,

                0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f,

                0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,

                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f,

                1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,

                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f
    };

    final float[] quad = {0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f};
    final float[] quadNormals = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f};
    final float[] quadTextCoords = {0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
    final float[] quadColors = {1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f};

    //final float[] cube = {0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f};
    //final float[] cubeNormals = {1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
    //final float[] cubeTextCoords = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
    //final float[] cubeColors = {1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f};

    float[] indices;

    private final int mBytesPerFloat = 4;
    private final int mStrideBytes = 7 * mBytesPerFloat;
    private final int mPositionOffset = 0;
    private final int mPositionDataSize = 3;
    private final int mColorOffset = 3;
    private final int mColorDataSize = 4;
    public FloatBuffer mVertexBuffer;
    public FloatBuffer mColorBuffer;
    public FloatBuffer mTextCoordBuffer;
    public FloatBuffer mIndicesBuffer;
    public int[]ibo = new int[1];

    public float[] vertices;
    public float[] normals;
    public float[] textureCoordinates;
    public float[] colors;

    public void createBuffers() {
        indices = new float[36];

        for(int i = 0, j = 0; i < 36; i+= 6, j += 4) {
            indices[i] = j;
            indices[i + 1] = j + 1;
            indices[i + 2] = j + 2;
            indices[i + 3] = j + 2;
            indices[i + 4] = j + 3;
            indices[i + 5] = j;
        }

        /*
        mVertexBuffer = ByteBuffer.allocateDirect(cube.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mColorBuffer = ByteBuffer.allocateDirect(cubeColors.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextCoordBuffer = ByteBuffer.allocateDirect(cubeTextCoords.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mIndicesBuffer = ByteBuffer.allocateDirect(indices.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        */

        vertices = new float[0];
        //System.arraycopy(quad, 0, vertices, 0, quad.length);
        normals = new float[0];
        //System.arraycopy(quadNormals, 0, normals, 0, quadNormals.length);
        textureCoordinates = new float[0];
        //System.arraycopy(quadTextCoords, 0, textureCoordinates, 0, quadTextCoords.length);
        colors = new float[0];
        //System.arraycopy(quadColors, 0, colors, 0, quadColors.length);

        mVertexBuffer = ByteBuffer.allocateDirect(vertices.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mColorBuffer = ByteBuffer.allocateDirect(colors.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextCoordBuffer = ByteBuffer.allocateDirect(textureCoordinates.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mIndicesBuffer = ByteBuffer.allocateDirect(indices.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();

        //mVertexBuffer.put(cube).position(0);
        //mColorBuffer.put(cubeColors).position(0);
        //mTextCoordBuffer.put(cubeTextCoords).position(0);
        //mIndicesBuffer.put(indices).position(0);

        //mVertexBuffer.put(quad).position(0);
        //mColorBuffer.put(quadColors).position(0);
        //mTextCoordBuffer.put(quadTextCoords).position(0);
        //mIndicesBuffer.put(indices).position(0);

        mVertexBuffer.put(vertices).position(0);
        mColorBuffer.put(colors).position(0);
        mTextCoordBuffer.put(textureCoordinates).position(0);
        mIndicesBuffer.put(indices).position(0);
    }

    public void createQuad(int x, int y, int z, int width, int height) {
        float[] newV = new float[18];

        for(int i = 0; i < quad.length; i+=3) {
            newV[i] = quad[i] * width + x;
            newV[i+1] = quad[i+1] * height + y;
            newV[i+2] = quad[i+2] + z;
        }

        float[] temp = new float[vertices.length + newV.length];
        System.arraycopy(vertices, 0, temp, 0, vertices.length);
        vertices = new float[temp.length];
        System.arraycopy(temp, 0, vertices, 0, temp.length);
        System.arraycopy(newV, 0, vertices, vertices.length - newV.length, newV.length);

        temp = new float[colors.length + quadColors.length];
        System.arraycopy(colors, 0, temp, 0, colors.length);
        colors = new float[temp.length];
        System.arraycopy(temp, 0, colors, 0, temp.length);
        System.arraycopy(quadColors, 0, colors, colors.length - quadColors.length, quadColors.length);

        temp = new float[textureCoordinates.length + quadTextCoords.length];
        System.arraycopy(textureCoordinates, 0, temp, 0, textureCoordinates.length);
        textureCoordinates = new float[temp.length];
        System.arraycopy(temp, 0, textureCoordinates, 0, temp.length);
        System.arraycopy(quadTextCoords, 0, textureCoordinates, textureCoordinates.length - quadTextCoords.length, quadTextCoords.length);

        temp = new float[normals.length + quadNormals.length];
        System.arraycopy(normals, 0, temp, 0, normals.length);
        normals = new float[temp.length];
        System.arraycopy(temp, 0, normals, 0, temp.length);
        System.arraycopy(quadNormals, 0, normals, normals.length - quadNormals.length, quadNormals.length);

        mVertexBuffer = ByteBuffer.allocateDirect(vertices.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mColorBuffer = ByteBuffer.allocateDirect(colors.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextCoordBuffer = ByteBuffer.allocateDirect(textureCoordinates.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();

        mVertexBuffer.put(vertices).position(0);
        mColorBuffer.put(colors).position(0);
        mTextCoordBuffer.put(textureCoordinates).position(0);
        mIndicesBuffer.put(indices).position(0);
    }

    public float[] getVertices() {
        return vertices;
    }
    public float[] getNormals() {
        return normals;
    }
    public float[] getTextureCoordinates() {
        return textureCoordinates;
    }
    public float[] getIndices() {
        return indices;
    }
}
