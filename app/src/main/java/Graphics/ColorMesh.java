package Graphics;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import utkseniordesign.conquestofares.R;

/**
 * Created by brb55_000 on 2/20/2015.
 */
public class ColorMesh {

    public final int FLOATS_PER_VERTEX = 6;
    public FloatBuffer m_vertexBuffer;
    public short[] m_indexBuffer = null;

    private int m_capacity;
    private int m_numIndices = 0;
    private final int BYTES_PER_FLOAT = 4;
    private int m_position = 0;
    private int m_indexPosition = 0;

    private static int m_positionHandle;
    private static int m_colorHandle;
    private static int m_mvpHandle;
    private static int m_programHandle = 0;

    /// Gets capacity in vertices
    public int getCapacity() { return m_capacity; }
    public int getNumIndices() { return m_numIndices; }


    ColorMesh(Context context, int capacity, int numIndices) {
        init(capacity, numIndices);

        // Init shader
        if (m_programHandle == 0) {
            try {
                m_programHandle = ShaderHelper.compileShader(context, R.string.color_vert, R.string.color_frag, "color");
                m_positionHandle = GLES20.glGetAttribLocation(m_programHandle, "vPosition");
                m_colorHandle = GLES20.glGetAttribLocation(m_programHandle, "vColor");
                m_mvpHandle = GLES20.glGetUniformLocation(m_programHandle, "unMVP");
            } catch (IOException e) {
                Log.d("Shader", "Error occurred during compilation");
            }
        }
    }
    /// Creates and sets the capacity of the mesh in vertices
    /// Creates and sets the number of indices
    /// Will clear any existing data
    public void init(int capacity, int numIndices) {
        m_capacity = capacity;
        m_numIndices = numIndices;
        m_vertexBuffer = ByteBuffer.allocateDirect(capacity * FLOATS_PER_VERTEX * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        m_indexBuffer = new short[numIndices];
        m_position = 0;
    }

    /// Adds a vertex to the mesh. Make sure capacity is set
    public void addVertex(float x, float y, float z, float r, float g, float b) {
        m_vertexBuffer.put(x);
        m_vertexBuffer.put(y);
        m_vertexBuffer.put(z);
        m_vertexBuffer.put(r);
        m_vertexBuffer.put(g);
        m_vertexBuffer.put(b);
        m_position += FLOATS_PER_VERTEX;
    }

    /// Adds an index to the mesh
    public void addIndex(short index) {
        m_indexBuffer[m_indexPosition++] = index;
    }

    public void renderLines(float[] vpMatrix) {
        m_vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(m_position);
        GLES20.glVertexAttribPointer(m_position, 3, GLES20.GL_FLOAT, false, FLOATS_PER_VERTEX * 4, m_vertexBuffer);

        m_vertexBuffer.position(3);
        GLES20.glEnableVertexAttribArray(m_colorHandle);
        GLES20.glVertexAttribPointer(m_colorHandle, 3, GLES20.GL_FLOAT, false, FLOATS_PER_VERTEX * 4, m_vertexBuffer);
        m_vertexBuffer.position(0);

        GLES20.glUniformMatrix4fv(m_mvpHandle, 1, false, vpMatrix, 0);

        //GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
        //GLES20.glDrawElements(GLES20.GL_TRIANGLES, 36, GLES20.GL_FLOAT, 0);
        //GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        if (m_numIndices > 0) {
            GLES20.glDrawElements(GLES20.GL_LINES, m_numIndices, GLES20.GL_UNSIGNED_SHORT, 0);
        } else {
            GLES20.glDrawArrays(GLES20.GL_LINES, 0, m_position / 2);
        }
    }

}
