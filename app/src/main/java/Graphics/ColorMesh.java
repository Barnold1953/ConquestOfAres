package Graphics;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.util.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import utkseniordesign.conquestofares.R;

/**
 * Created by brb55_000 on 2/20/2015.
 */
public class ColorMesh {

    public final int FLOATS_PER_VERTEX = 3;
    public Vector<Float> vertexVec = new Vector<Float>();
    public Vector<Float> colorVec = new Vector<Float>();
    public Vector<Short> indexVec = new Vector<Short>();
    private FloatBuffer m_vertexBuffer;
    private FloatBuffer m_colorBuffer;
    private short[] m_indexBuffer = null;

    private final int BYTES_PER_FLOAT = 4;

    private static int m_positionHandle;
    private static int m_colorHandle;
    private static int m_mvpHandle;
    private static int m_programHandle = 0;

    /// Adds a vertex to the mesh. Make sure capacity is set
    public void addVertex(float x, float y, float z, float r, float g, float b) {
        vertexVec.add(x);
        vertexVec.add(y);
        vertexVec.add(z);
        colorVec.add(r);
        colorVec.add(g);
        colorVec.add(b);
    }

    /// Adds an index to the mesh
    public void addIndex(short index) {
        indexVec.add(index);
    }

    public void finish(Context context) {

        if (m_programHandle == 0) {
            try {
                m_programHandle = ShaderHelper.compileShader(context, R.string.color_vert, R.string.color_frag, "color");
                m_positionHandle = GLES20.glGetAttribLocation(m_programHandle, "vPosition");
                m_colorHandle = GLES20.glGetAttribLocation(m_programHandle, "vColor");
                m_mvpHandle = GLES20.glGetUniformLocation(m_programHandle, "uMVPMatrix");
            } catch (IOException e) {
                Log.d("Shader", "Error occurred during compilation");
            }
        }

        m_vertexBuffer = ByteBuffer.allocateDirect(vertexVec.size() * FLOATS_PER_VERTEX * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();

        for (int i = 0; i < vertexVec.size(); i++) {
            m_vertexBuffer.put(vertexVec.get(i));
        }

        m_colorBuffer = ByteBuffer.allocateDirect(colorVec.size() * FLOATS_PER_VERTEX * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();

        for (int i = 0; i < colorVec.size(); i++) {
            m_colorBuffer.put(colorVec.get(i));
        }
    }

    public void renderLines(float[] vpMatrix) {
        GLES20.glUseProgram(m_programHandle);

        m_vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(m_positionHandle);
        GLES20.glVertexAttribPointer(m_positionHandle, 3, GLES20.GL_FLOAT, false, 0, m_vertexBuffer);

        m_colorBuffer.position(0);
        GLES20.glEnableVertexAttribArray(m_colorHandle);
        GLES20.glVertexAttribPointer(m_colorHandle, 3, GLES20.GL_FLOAT, false, 0, m_colorBuffer);

        GLES20.glUniformMatrix4fv(m_mvpHandle, 1, false, vpMatrix, 0);

        GLES20.glLineWidth(10.0f);
        //if (m_numIndices > 0) {
           // GLES20.glDrawElements(GLES20.GL_LINES, m_numIndices, GLES20.GL_UNSIGNED_SHORT, 0);
        //} else {
            GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertexVec.size() / 3);
        //}
    }

    public void renderTriangles(float[] vpMatrix) {

        GLES20.glUseProgram(m_programHandle);

        m_vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(m_positionHandle);
        GLES20.glVertexAttribPointer(m_positionHandle, 3, GLES20.GL_FLOAT, false, 0, m_vertexBuffer);

        m_colorBuffer.position(0);
        GLES20.glEnableVertexAttribArray(m_colorHandle);
        GLES20.glVertexAttribPointer(m_colorHandle, 3, GLES20.GL_FLOAT, false, 0, m_colorBuffer);

        GLES20.glUniformMatrix4fv(m_mvpHandle, 1, false, vpMatrix, 0);

        GLES20.glLineWidth(10.0f);
        //if (m_numIndices > 0) {
        // GLES20.glDrawElements(GLES20.GL_LINES, m_numIndices, GLES20.GL_UNSIGNED_SHORT, 0);
        //} else {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexVec.size() / 3);
        //}
    }
}