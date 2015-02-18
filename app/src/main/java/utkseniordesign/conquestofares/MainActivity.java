package utkseniordesign.conquestofares;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.content.pm.ConfigurationInfo;
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
    FileIO mFileIO;

    private HashMap<String,String> getShaders(){
        HashMap<String, String> shaders = new HashMap<String, String>();

        shaders.put("simple.vert", getString(R.string.simple_vert));
        shaders.put("simple.frag", getString(R.string.simple_frag));

        return shaders;
    }

    /*private HashMap<String, FloatBuffer> getTextures(){
        HashMap<String, FloatBuffer> textures = new HashMap<String, FloatBuffer>();


    }*/

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        Log.d("Begin", "Before");
        super.onCreate( savedInstanceState );

        Log.d("Create", "Before new surface call\n");

        mFileIO = new FileIO(this);
        mGLSurfaceView = new GLSurfaceView(this);

        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        HashMap<String,String> shaders = getShaders();

        TextureHelper th = new TextureHelper();
        int handle = th.ImageToTexture(this, R.drawable.texture1);
        Log.d("Texture", "Done");

        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setEGLConfigChooser(8 , 8, 8, 8, 16, 0);
        mGLSurfaceView.setRenderer(new CoARenderer(this, shaders));

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
