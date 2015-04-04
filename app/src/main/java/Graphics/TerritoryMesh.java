package Graphics;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Vector;

import utkseniordesign.conquestofares.R;

/**
 * Created by brb55_000 on 4/4/2015.
 */
public class TerritoryMesh {

    public final int FLOATS_PER_VERTEX = 2;
    public Vector<Float> vertexVec = new Vector<Float>();
    public Vector<Float> texVec = new Vector<Float>();
    private FloatBuffer m_vertexBuffer;
    private FloatBuffer m_texBuffer;

    private final int BYTES_PER_FLOAT = 4;

    private static int m_positionHandle;
    private static int m_texCoordHandle;
    private static int m_textureHandle;
    private static int m_mvpHandle;
    private static int m_colorHandle;
    private static int m_programHandle = 0;

    public void init(float x, float y, float w, float h, Context context) {

        Log.d("INIT: ", Float.toString(x) + " " + Float.toString(y) + " " + Float.toString(w));
        // Make the box
        vertexVec.add(x);
        vertexVec.add(y + h);
        texVec.add(0.0f);
        texVec.add(1.0f);

        vertexVec.add(x);
        vertexVec.add(y);
        texVec.add(0.0f);
        texVec.add(0.0f);

        vertexVec.add(x + w);
        vertexVec.add(y);
        texVec.add(1.0f);
        texVec.add(0.0f);

        vertexVec.add(x + w);
        vertexVec.add(y);
        texVec.add(1.0f);
        texVec.add(0.0f);

        vertexVec.add(x + w);
        vertexVec.add(y + h);
        texVec.add(1.0f);
        texVec.add(1.0f);

        vertexVec.add(x);
        vertexVec.add(y + h);
        texVec.add(0.0f);
        texVec.add(1.0f);

        if (m_programHandle == 0) {
            try {
                m_programHandle = ShaderHelper.compileShader(context, R.string.map_vert, R.string.map_frag, "territory");
                m_positionHandle = GLES20.glGetAttribLocation(m_programHandle, "vPosition");
                m_texCoordHandle = GLES20.glGetAttribLocation(m_programHandle, "vTexCoords");
                m_mvpHandle = GLES20.glGetUniformLocation(m_programHandle, "unMVPMatrix");
                m_colorHandle = GLES20.glGetUniformLocation(m_programHandle, "unColor");
                m_textureHandle = GLES20.glGetUniformLocation(m_programHandle, "unTexture");
            } catch (IOException e) {
                Log.d("Shader", "Error occurred during compilation");
            }
        }

        m_vertexBuffer = ByteBuffer.allocateDirect(vertexVec.size() * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();

        for (int i = 0; i < vertexVec.size(); i++) {
            m_vertexBuffer.put(vertexVec.get(i));
        }

        m_texBuffer = ByteBuffer.allocateDirect(texVec.size() * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();

        for (int i = 0; i < texVec.size(); i++) {
            m_texBuffer.put(texVec.get(i));
        }
    }

    public void render(float r, float g, float b, int texture, float[] vpMatrix) {

        GLES20.glUseProgram(m_programHandle);

        m_vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(m_positionHandle);
        GLES20.glVertexAttribPointer(m_positionHandle, 2, GLES20.GL_FLOAT, false, 0, m_vertexBuffer);

        m_texBuffer.position(0);
        GLES20.glEnableVertexAttribArray(m_texCoordHandle);
        GLES20.glVertexAttribPointer(m_texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, m_texBuffer);

        GLES20.glUniformMatrix4fv(m_mvpHandle, 1, false, vpMatrix, 0);
        GLES20.glUniform3f(m_colorHandle, r, g, b);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glUniform1f(m_textureHandle, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
    }
}
