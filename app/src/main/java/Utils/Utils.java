package Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import java.lang.reflect.Method;

import Generation.MapGenerationParams;

/**
 * Created by lasth_000 on 4/4/2015.
 */
public class Utils {

    public static float[] byteColorToFloat(byte[] color) {
        float[] newC = new float[3];
        newC[0] = (((int)(color[0]) + 256) % 256) / 255.0f;
        newC[1] = (((int)(color[1]) + 256) % 256) / 255.0f;
        newC[2] = (((int)(color[2]) + 256) % 256) / 255.0f;
        return newC;
    }

    public static float[] translateCoordinatePair(float x, float y, MapGenerationParams.MapSize size) {
        x = x/Device.screenWidth;
        x = x*size.getWidth();
        y = (Device.screenHeight - y);
        y = y/Device.screenHeight;
        y = y*size.getHeight();
        Log.d("ben is a bully", Float.toString(x) + " " + Float.toString(y));
        return new float[] {x,y};
        //Log.d("Coordinates:", Float.toString(x) + " " + Float.toString(y));
    }

    public static Point getScreenDimensions(Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        int realWidth;
        int realHeight;

        if (Build.VERSION.SDK_INT >= 17){
            //new pleasant way to get real metrics
            DisplayMetrics realMetrics = new DisplayMetrics();
            display.getRealMetrics(realMetrics);
            realWidth = realMetrics.widthPixels;
            realHeight = realMetrics.heightPixels;

        } else if (Build.VERSION.SDK_INT >= 14) {
            //reflection for this weird in-between time
            try {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                realWidth = (Integer) mGetRawW.invoke(display);
                realHeight = (Integer) mGetRawH.invoke(display);
            } catch (Exception e) {
                //this may not be 100% accurate, but it's all we've got
                realWidth = display.getWidth();
                realHeight = display.getHeight();
                Log.e("Display Info", "Couldn't use reflection to get the real display metrics.");
            }

        } else {
            //This should be close, as lower API devices should not have window navigation bars
            realWidth = display.getWidth();
            realHeight = display.getHeight();
        }
        return new Point(realWidth, realHeight);
    }

    public static int fastFloor(float num) { return (int)(num+.001);}
}
