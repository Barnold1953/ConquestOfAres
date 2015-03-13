package utkseniordesign.conquestofares;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;

import java.util.HashMap;

import Game.GameSettings;
import Graphics.CoARenderer;
import UI.UserInterfaceHelper;

public class GameActivity extends Activity {

    private GLSurfaceView mGLSurfaceView;

    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_gamescreen );

        // Get Game Settings, everything except MapGenParams

        /* COMMENT THIS OUT IF GAME ACTIVITY IS YOUR STARTUP ACTIVITY */
        Intent intent = getIntent();
        if( intent != null ) {
            GameSettings settings = (GameSettings) intent.getParcelableExtra("Settings");
        }

        // Get GL Resources
        HashMap<String, int[]> textures = getTextures();

        // Initialize the glSurfaceView
        mGLSurfaceView = ( GLSurfaceView ) findViewById( R.id.glRenderArea );
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setEGLConfigChooser(8 , 8, 8, 8, 16, 0);
        mGLSurfaceView.setRenderer( new CoARenderer( this, textures ) );

    }

    @Override
    protected void onResume()
    {
        // The activity must call the GL surface view's onResume() on activity onResume().
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause()
    {
        // The activity must call the GL surface view's onPause() on activity onPause().
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private HashMap<String, int[]> getTextures(){
        HashMap<String, int[]> textures = new HashMap<String, int[]>();
        int[] texture = new int[1];

        Bitmap bitmap = BitmapFactory.decodeResource( getResources(), R.drawable.texture1 );

        GLES20.glGenTextures( 1, texture, 0 );
        GLES20.glBindTexture( GLES20.GL_TEXTURE_2D, texture[0] );

        GLES20.glTexParameterf( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST );
        GLES20.glTexParameterf( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR );

        GLUtils.texImage2D( GLES20.GL_TEXTURE_2D, 0, bitmap, 0 );

        bitmap.recycle();

        textures.put( "texture1", texture );

        return textures;
    }
}
