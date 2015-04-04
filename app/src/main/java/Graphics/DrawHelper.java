package Graphics;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Created by Nathan on 1/21/2015.
 */
public class DrawHelper {
    private Camera mHelper;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private int mTCoordHandle;
    private int mTextureHandle;

    public void setProgHandles(int ph, String shader){
        mMVPMatrixHandle = GLES20.glGetUniformLocation(ph, "uMVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(ph, "vPosition");
        mColorHandle = GLES20.glGetAttribLocation(ph, "vColor");
        mTCoordHandle = GLES20.glGetAttribLocation(ph, "vTextCoords");
        mTextureHandle = GLES20.glGetAttribLocation(ph, "texture");
    }

    public void draw(Camera camera, FloatBuffer mVertexBuffer, ByteBuffer mColorBuffer, FloatBuffer mTextCoordBuffer, int textureId, int vCount, String shader){
        int programHandle = ShaderHelper.getShader(shader);
        GLES20.glUseProgram(programHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(mTextureHandle, 0);

        mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        mColorBuffer.position(0);
        GLES20.glVertexAttribPointer(mColorHandle, 3, GLES20.GL_BYTE, false, 0, mColorBuffer);
        GLES20.glEnableVertexAttribArray(mColorHandle);

        mTextCoordBuffer.position(0);
        GLES20.glVertexAttribPointer(mTCoordHandle, 2, GLES20.GL_FLOAT, false, 0, mTextCoordBuffer);
        GLES20.glEnableVertexAttribArray(mTCoordHandle);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, camera.getVPMatrix(), 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount/3);
    }

    public DrawHelper(Camera mh){
        mHelper = mh;
    }
}
