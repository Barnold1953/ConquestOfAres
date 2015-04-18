package Generation;

import android.content.Context;
import android.opengl.GLES20;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Vector;

import Game.Territory;
import Graphics.GLRenderTarget;
import Graphics.ShaderHelper;
import Utils.Utils;
import utkseniordesign.conquestofares.R;

/**
 * Created by brb55_000 on 4/8/2015.
 */
public class GpuGenerator {

    public static boolean isDone = false;
    public static boolean hasGenRequest = false;
    private static MapData m_md;

    private static int m_positionHandle;
    private static int m_programHandle = 0;
    private static FloatBuffer m_vertexBuffer;

    private static GLRenderTarget m_renderTarget = new GLRenderTarget();

    public static boolean generateMap(MapData md) {
        if (hasGenRequest) return false;
        isDone = false;
        m_md = md;

        hasGenRequest = true;
        return true;
    }

    // Call from render thread
    public static void updateGen(Context context) {
        if (m_programHandle == 0) {
            try {
                m_programHandle = ShaderHelper.compileShader(context, R.string.gen_vert, R.string.gen_frag, "gen");
                m_positionHandle = GLES20.glGetAttribLocation(m_programHandle, "vPosition");
            } catch (IOException e) {
                Log.d("Shader", "Error occurred during compilation");
            }

            m_vertexBuffer = ByteBuffer.allocateDirect(12 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

            m_vertexBuffer.put(-1.0f);
            m_vertexBuffer.put(1.0f);

            m_vertexBuffer.put(-1.0f);
            m_vertexBuffer.put(-1.0f);

            m_vertexBuffer.put(1.0f);
            m_vertexBuffer.put(-1.0f);

            m_vertexBuffer.put(1.0f);
            m_vertexBuffer.put(-1.0f);

            m_vertexBuffer.put(1.0f);
            m_vertexBuffer.put(1.0f);

            m_vertexBuffer.put(-1.0f);
            m_vertexBuffer.put(1.0f);

            m_renderTarget.init((int)m_md.width, (int)m_md.height);
        }

        m_renderTarget.bind();

        GLES20.glUseProgram(m_programHandle);

        m_vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(m_programHandle, "vPosition"));
        GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(m_programHandle, "vPosition"), 2, GLES20.GL_FLOAT, false, 0, m_vertexBuffer);

        // Uniforms
        GLES20.glUniform2f(GLES20.glGetUniformLocation(m_programHandle, "unDims"), (float)m_md.width, (float)m_md.height);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(m_programHandle, "unNumT"), m_md.territories.size());
        GLES20.glUniform1i(GLES20.glGetUniformLocation(m_programHandle, "unHorizontalWrap"), m_md.params.horizontalWrap ? 0 : 1);
        Log.d("t ", Integer.toString(m_md.territories.size()));
        for (int i = 0; i < m_md.territories.size(); i++) {
            Territory t = m_md.territories.get(i);
            GLES20.glUniform2f(GLES20.glGetUniformLocation(m_programHandle, "unTPositions[" + Integer.toString(i) + "]"), t.x, t.y);
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        GLES20.glUseProgram(0);
        GLES20.glFinish();

        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * (int)m_md.width * (int)m_md.height).order(ByteOrder.nativeOrder());
        GLES20.glReadPixels(0, 0, (int)m_md.width, (int)m_md.height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);

        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.d("GpuGenerator", "Error " + Integer.toString(error));
        }

        for (int y = 0; y < m_md.height; y++) {
            for (int x = 0; x < m_md.width; x++) {
                m_md.territoryIndices[(int)m_md.height - y - 1][x] = Math.round(Utils.byteColorToFloat(buffer.get((((int)m_md.height - y - 1) * (int)m_md.width + x) * 4)) * 255.0f);
                if (m_md.territoryIndices[(int)m_md.height - y - 1][x] >= m_md.territories.size()) {
                    m_md.territoryIndices[(int)m_md.height - y - 1][x] = m_md.territories.size() - 1;
                }
            }
        }

        hasGenRequest = false;
        isDone = true;

        m_renderTarget.unBind();

       // GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
      //  GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
    }
}
