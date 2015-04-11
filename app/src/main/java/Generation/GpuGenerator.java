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
    //    if (!hasGenRequest) return;
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

      //  if (!m_renderTarget.bind()) {
      //      Log.d("RenderTarget ", "Failed to bind framebuffer.");
      //  }

        GLES20.glUseProgram(m_programHandle);

        m_vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(m_programHandle, "vPosition"));
        GLES20.glVertexAttribPointer(GLES20.glGetAttribLocation(m_programHandle, "vPosition"), 2, GLES20.GL_FLOAT, false, 0, m_vertexBuffer);

        // Uniforms
        GLES20.glUniform2f(GLES20.glGetUniformLocation(m_programHandle, "unDims"), (float)m_md.width, (float)m_md.height);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(m_programHandle, "unNumT"), m_md.territories.size());
        GLES20.glUniform1i(GLES20.glGetUniformLocation(m_programHandle, "unHorizontalWrap"), 0);
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

        byte[] bytes = new byte[3];
        bytes[0] = buffer.get(0);
        bytes[1] = buffer.get(1);
        bytes[2] = buffer.get(2);
        float[] fls = Utils.byteColorToFloat(bytes);

        Log.d("test", Float.toString(fls[0]) + " " + Float.toString(fls[1]) + " " + Float.toString(fls[2]));

    //    m_renderTarget.unBind();

        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.d("GpuGenerator", "Error " + Integer.toString(error));
        }

        hasGenRequest = false;
        isDone = true;
    }
}
