package Graphics;

import android.opengl.GLES20;

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
    private int mTotalFramesW, mTotalFramesH;
    private int mCurrentFrameW, mCurrentFrameH;

    public void setProgHandles(int ph, String shader){
        mMVPMatrixHandle = GLES20.glGetUniformLocation(ph, "uMVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(ph, "vPosition");
        mColorHandle = GLES20.glGetAttribLocation(ph, "vColor");
        mTCoordHandle = GLES20.glGetAttribLocation(ph, "vTextCoords");
        mTextureHandle = GLES20.glGetAttribLocation(ph, "texture");

        if(shader.equals("animate")){
            mCurrentFrameW = GLES20.glGetUniformLocation(ph, "currentFrameW");
            mCurrentFrameH = GLES20.glGetUniformLocation(ph, "currentFrameH");
            mTotalFramesW = GLES20.glGetUniformLocation(ph, "totalFramesW");
            mTotalFramesH = GLES20.glGetUniformLocation(ph, "totalFramesH");
        }
    }

    public void draw(Camera camera, FloatBuffer mVertexBuffer, FloatBuffer mColorBuffer, FloatBuffer mTextCoordBuffer, int textureId){
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
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
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, GeometryHelper.getVerticesCount("master")/3);

     //   GLES20.glDisableVertexAttribArray(mPositionHandle);
     //   GLES20.glDisableVertexAttribArray(mColorHandle);
     //   GLES20.glDisableVertexAttribArray(mTCoordHandle);
    }

    public DrawHelper(Camera mh){
        mHelper = mh;
    }
}
