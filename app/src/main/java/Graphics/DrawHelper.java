package Graphics;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

/**
 * Created by Nathan on 1/21/2015.
 */
public class DrawHelper {
    private MatrixHelper mHelper;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private int mTCoordHandle;

    private final int mBytesPerFloat = 4;
    private final int mStrideBytes = 9 * mBytesPerFloat;

    public void DrawCube(int programHandle, FloatBuffer mVertexBuffer, FloatBuffer mColorBuffer, FloatBuffer mTextCoordBuffer, int[] ibo){
        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "uMVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "vPosition");
        mColorHandle = GLES20.glGetAttribLocation(programHandle, "vColor");
        mTCoordHandle = GLES20.glGetAttribLocation(programHandle, "vTextCoords");

        mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, mStrideBytes, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        mColorBuffer.position(0);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, mStrideBytes, mColorBuffer);
        GLES20.glEnableVertexAttribArray(mColorHandle);

        mTextCoordBuffer.position(0);
        GLES20.glVertexAttribPointer(mTCoordHandle, 2, GLES20.GL_FLOAT, false, mStrideBytes, mTextCoordBuffer);
        GLES20.glEnableVertexAttribArray(mTCoordHandle);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mHelper.getmMVPMatrix(), 0);

        //GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
        //GLES20.glDrawElements(GLES20.GL_TRIANGLES, 36, GLES20.GL_FLOAT, 0);
        //GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
    }

    public DrawHelper(MatrixHelper mh){
        mHelper = mh;
    }
}
