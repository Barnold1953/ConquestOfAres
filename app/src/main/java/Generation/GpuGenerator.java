package Generation;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Vector;

import Game.Territory;
import Graphics.ShaderHelper;
import utkseniordesign.conquestofares.R;

/**
 * Created by brb55_000 on 4/8/2015.
 */
public class GpuGenerator {

    public boolean isDone = false;
    public boolean hasGenRequest = false;
    private MapData m_md;

    private static int m_positionHandle;
    private static int m_programHandle = 0;
    private FloatBuffer m_vertexBuffer;
    private Vector<Territory> m_territories = null;

    public boolean generateMap(MapData md, Vector<Territory> territories) {
        if (hasGenRequest) return false;
        isDone = false;
        m_md = md;
        m_territories = territories;

        hasGenRequest = true;
        return true;
    }

    // Call from render thread
    public void updateGen(Context context) {
    //    if (!hasGenRequest) return;

        if (m_programHandle == 0) {
            try {
                m_programHandle = ShaderHelper.compileShader(context, R.string.map_vert, R.string.map_frag, "territory");
                m_positionHandle = GLES20.glGetAttribLocation(m_programHandle, "vPosition");
            } catch (IOException e) {
                Log.d("Shader", "Error occurred during compilation");
            }

            m_vertexBuffer = ByteBuffer.allocateDirect(12 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

            m_vertexBuffer.put(0.0f);
            m_vertexBuffer.put(1.0f);

            m_vertexBuffer.put(0.0f);
            m_vertexBuffer.put(0.0f);

            m_vertexBuffer.put(1.0f);
            m_vertexBuffer.put(0.0f);

            m_vertexBuffer.put(1.0f);
            m_vertexBuffer.put(0.0f);

            m_vertexBuffer.put(1.0f);
            m_vertexBuffer.put(1.0f);

            m_vertexBuffer.put(0.0f);
            m_vertexBuffer.put(1.0f);
        }

        GLES20.glUseProgram(m_programHandle);

        m_vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(m_programHandle, "vPosition"));
        GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(m_programHandle, "vPosition"), 2, GLES20.GL_FLOAT, false, 0, m_vertexBuffer);

        // Uniforms
        GLES20.glUniform2f(GLES20.glGetUniformLocation(m_programHandle, "unDims"), (float)m_md.width, (float)m_md.height);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(m_programHandle, "unNumT"), m_md.territories.size());
        GLES20.glUniform1i(GLES20.glGetUniformLocation(m_programHandle, "unHorizontalWrap"), 0);
        for (int i = 0; i < m_md.territories.size(); i++) {
            Territory t = m_md.territories.get(i);
            GLES20.glUniform2f(GLES20.glGetUniformLocation(m_programHandle, "unTPositions[" + Integer.toString(i) + "]"), t.x, t.y);
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        GLES20.glUseProgram(0);
        GLES20.glFinish();

        hasGenRequest = false;
        isDone = true;
    }
}
