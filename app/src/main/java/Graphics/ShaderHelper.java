package Graphics;

import android.opengl.GLES20;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import Utility.FileIO;

/**
 * Created by Nathan on 1/17/2015.
 */
public class ShaderHelper {
    public int mPositionHandle;
    public int mColorHandle;
    public int mMVPMatrixHandle;
    public int mTCoordHandle;

    public static int loadShader(int type, String shaderCode) throws IOException{
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public int compileShader(String shader) throws IOException{
        //String frag = "", vert = "";
        String filePath = "@raw/";
        int vertexShaderHandle, fragmentShaderHandle;
        File fragFile, vertFile;

        Log.d("1", "Before loading shader");

        vertFile = new File(filePath + shader + ".vert");
        fragFile = new File(filePath + shader + ".frag");

        //vert = FileIO.readFileToString(vertFile);
        //frag = FileIO.readFileToString(fragFile);
        //vert = FileIO.readFileToString("@raw/simple.vert");
        //frag = FileIO.readFileToString("@raw/simple.frag");
        final String vert =
                "uniform mat4 uMVPMatrix;\n" +
                "\n" +
                "attribute vec4 vPosition;\n" +
                "attribute vec4 vColor;\n" +
                "attribute vec2 vTextCoords;\n" +
                "\n" +
                "varying vec4 color;\n" +
                "varying vec2 tCoords;\n" +
                "\n" +
                "void main() {\n" +
                "  color = vColor;\n" +
                "  tCoords = vTextCoords;\n" +
                "  gl_Position =  uMVPMatrix * vPosition;\n" +
                "}";
        final String frag =
                "precision mediump float;\n" +
                "\n" +
                "varying vec4 color;\n" +
                "varying vec2 tCoords;\n" +
                "\n" +
                "void main() {\n" +
                "  gl_FragColor = color;\n" +
                "}\n";

        Log.d("Shader", vert);
        Log.d("Shader", frag);

        vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);

        if(vertexShaderHandle != 0) {
            Log.d("Shader", "Creating Vertex Shader");
            GLES20.glShaderSource(vertexShaderHandle, vert);

            GLES20.glCompileShader(vertexShaderHandle);

            int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            if(compileStatus[0] == 0){
                Log.d("Shader", GLES20.glGetShaderInfoLog(vertexShaderHandle));
                GLES20.glDeleteShader(vertexShaderHandle);
                vertexShaderHandle = 0;
            }
        }
        if(vertexShaderHandle == 0) {
            Log.d("Shader", "Error creating vertex shader");
            throw new RuntimeException("Error creating vertex shader");
        }

        fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

        if(fragmentShaderHandle != 0){
            Log.d("Shader", "Creating Fragment Shader");
            GLES20.glShaderSource(fragmentShaderHandle, frag);

            GLES20.glCompileShader(fragmentShaderHandle);

            int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            if(compileStatus[0] == 0){
                GLES20.glDeleteShader(fragmentShaderHandle);
                fragmentShaderHandle = 0;
            }
        }
        if(fragmentShaderHandle == 0) {
            Log.d("String", "Error creating fragment shader");
            throw new RuntimeException("Error creating fragment shader");
        }

        int programHandle = GLES20.glCreateProgram();

        if(programHandle != 0){
            GLES20.glAttachShader(programHandle, vertexShaderHandle);
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            GLES20.glBindAttribLocation(programHandle, 0, "vPosition");
            GLES20.glBindAttribLocation(programHandle, 1, "vColor");
            GLES20.glBindAttribLocation(programHandle, 2, "vTextCoords");

            GLES20.glLinkProgram(programHandle);

            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            if(linkStatus[0] == 0){
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }
        if(programHandle == 0){
            throw new RuntimeException("Error creating program.");
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
