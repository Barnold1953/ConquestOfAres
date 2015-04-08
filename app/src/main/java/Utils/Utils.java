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
        Log.d("Screen Size", Float.toString(Device.screenWidth) + " " + Float.toString(Device.screenHeight));
        x = x/Device.screenWidth;
        x = x*size.getWidth();
        y = (Device.screenHeight - y);
        y = y/Device.screenHeight;
        y = y*size.getHeight();
        return new float[] {x,y};
    }

    public static void getScreenDimensions(Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        try {
            display.getSize(size);
            Device.screenHeight = size.y;
            Device.screenWidth = size.x;
        } catch (NoSuchMethodError e) {
            Device.screenHeight = display.getHeight();
            Device.screenWidth = display.getWidth();
        }
    }

    public static int fastFloor(float num) { return (int)(num+.001);}
}
