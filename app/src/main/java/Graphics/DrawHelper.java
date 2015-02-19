package Graphics;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

/**
 * Created by Nathan on 1/21/2015.
 */
public class DrawHelper {
    private Camera mHelper;
    private GeometryHelper gHelper;
    private TextureHelper tHelper;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private int mTCoordHandle;
    private int mTextureHandle;

    private final int mBytesPerFloat = 4;
    private final int mStrideBytes = 9 * mBytesPerFloat;

    public void setProgHandles(int ph){
        mMVPMatrixHandle = GLES20.glGetUniformLocation(ph, "uMVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(ph, "vPosition");
        mColorHandle = GLES20.glGetAttribLocation(ph, "vColor");
        mTCoordHandle = GLES20.glGetAttribLocation(ph, "vTextCoords");
        mTextureHandle = GLES20.glGetAttribLocation(ph, "texture");
    }

    public void draw(Camera camera, FloatBuffer mVertexBuffer, FloatBuffer mColorBuffer, FloatBuffer mTextCoordBuffer, FloatBuffer mIndicesBuffer, String textureName){
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tHelper.getTexture(textureName));
        GLES20.glUniform1i(mTextureHandle, 0);

        mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        mColorBuffer.position(0);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, mColorBuffer);
        GLES20.glEnableVertexAttribArray(mColorHandle);

        mTextCoordBuffer.position(0);
        GLES20.glVertexAttribPointer(mTCoordHandle, 2, GLES20.GL_FLOAT, false, 0, mTextCoordBuffer);
        GLES20.glEnableVertexAttribArray(mTCoordHandle);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, camera.getVPMatrix(), 0);

        //GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
        //GLES20.glDrawElements(GLES20.GL_TRIANGLES, 36, GLES20.GL_FLOAT, 0);
        //GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, gHelper.getVertices().length/3);
    }

    public DrawHelper(Camera mh, GeometryHelper gh, TextureHelper th){
        gHelper = gh;
        mHelper = mh;
        tHelper = th;
    }
}
