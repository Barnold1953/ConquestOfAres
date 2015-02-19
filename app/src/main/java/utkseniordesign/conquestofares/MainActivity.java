package utkseniordesign.conquestofares;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLUtils;
import android.support.v4.view.PagerTabStrip;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuInflater;
import android.view.ViewDebug;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import Graphics.CoARenderer;
import Graphics.TextureHelper;

public class MainActivity extends ActionBarActivity {
    private GLSurfaceView mGLSurfaceView;

    private HashMap<String, int[]> getTextures(){
        HashMap<String, int[]> textures = new HashMap<String, int[]>();
        int[] texture = new int[1];

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.texture1);

        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        bitmap.recycle();

        textures.put("texture1", texture);

        return textures;
    }

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        Log.d("Begin", "Before");
        super.onCreate( savedInstanceState );

        Log.d("Create", "Before new surface call\n");

        mGLSurfaceView = new GLSurfaceView(this);

        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        Log.d("Main", "Before texture loading");
        HashMap<String, int[]> textures = getTextures();
        Log.d("Main", "After texture loading");

        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setEGLConfigChooser(8 , 8, 8, 8, 16, 0);
        mGLSurfaceView.setRenderer(new CoARenderer(this, textures));

        setContentView(mGLSurfaceView);
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
        //Clean up everything here
    }
}
