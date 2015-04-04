package Utils;

import Generation.MapGenerationParams;

/**
 * Created by lasth_000 on 4/4/2015.
 */
public class Utils {
    public static void translateCoordinatePair(float x, float y, MapGenerationParams.MapSize size) {
        x = x/Device.screenWidth;
        x = x*size.getWidth();
        y = (Device.screenHeight - y);
        y = y/Device.screenHeight;
        y = y*size.getHeight();
    }

    public static int fastFloor(float num) { return (int)(num+.001);}
}
