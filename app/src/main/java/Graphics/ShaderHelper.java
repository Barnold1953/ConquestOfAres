package Graphics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Shader;
import android.opengl.GLES20;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import utkseniordesign.conquestofares.R;

/**
 * Created by Nathan on 1/17/2015.
 */
public class ShaderHelper {
    HashMap<String, Integer> shaders;
    public int mPositionHandle;
    public int mColorHandle;
    public int mMVPMatrixHandle;
    public int mTCoordHandle;

    public ShaderHelper(){
        shaders = new HashMap<>();
    }

    public static int loadShader(int type, String shaderCode) throws IOException{
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public int compileShader(Context context, int vertID, int fragID, String shader) throws IOException{

        String filePath = "@raw/";
        int vertexShaderHandle, fragmentShaderHandle;

        Log.d("1", "Before loading shader");

        String vert, frag;

        int programHandle;

        if(shaders.get(shader) == null) {
            vert = context.getResources().getString(vertID);
            frag = context.getResources().getString(fragID);
            vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);

            if (vertexShaderHandle != 0) {
                Log.d("Shader", "Creating Vertex Shader");
                GLES20.glShaderSource(vertexShaderHandle, vert);

                GLES20.glCompileShader(vertexShaderHandle);

                int[] compileStatus = new int[1];
                GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

                if (compileStatus[0] == 0) {
                    Log.d("Shader", GLES20.glGetShaderInfoLog(vertexShaderHandle));
                    GLES20.glDeleteShader(vertexShaderHandle);
                    vertexShaderHandle = 0;
                }
            }
            if (vertexShaderHandle == 0) {
                Log.d("Shader", "Error creating vertex shader");
                throw new RuntimeException("Error creating vertex shader");
            }

            fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

            if (fragmentShaderHandle != 0) {
                Log.d("Shader", "Creating Fragment Shader");
                GLES20.glShaderSource(fragmentShaderHandle, frag);

                GLES20.glCompileShader(fragmentShaderHandle);

                int[] compileStatus = new int[1];
                GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

                if (compileStatus[0] == 0) {
                    GLES20.glDeleteShader(fragmentShaderHandle);
                    fragmentShaderHandle = 0;
                }
            }
            if (fragmentShaderHandle == 0) {
                Log.d("String", "Error creating fragment shader");
                throw new RuntimeException("Error creating fragment shader");
            }

            programHandle = GLES20.glCreateProgram();

            if (programHandle != 0) {
                GLES20.glAttachShader(programHandle, vertexShaderHandle);
                GLES20.glAttachShader(programHandle, fragmentShaderHandle);

                GLES20.glBindAttribLocation(programHandle, 0, "vPosition");
                GLES20.glBindAttribLocation(programHandle, 1, "vColor");
                GLES20.glBindAttribLocation(programHandle, 2, "vTextCoords");

                GLES20.glLinkProgram(programHandle);

                int[] linkStatus = new int[1];
                GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

                if (linkStatus[0] == 0) {
                    GLES20.glDeleteProgram(programHandle);
                    programHandle = 0;
                }
            }
            if (programHandle == 0) {
                throw new RuntimeException("Error creating program.");
            }

            shaders.put(shader, programHandle);
        }
        else{
            programHandle = shaders.get(shader);
        }
        Log.d("shader", "Shader successfully compiled");

        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "uMVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "vPosition");
        mColorHandle = GLES20.glGetAttribLocation(programHandle, "vColor");
        mTCoordHandle = GLES20.glGetAttribLocation(programHandle, "vTextCoords");

        GLES20.glUseProgram(programHandle);

        return programHandle;
    }
}
