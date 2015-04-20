package Graphics;

/**
 * Created by Nathan on 3/28/2015.
 */
public class SpriteSheetDimensions {
    //public static float soldierWidth = 576, soldierHeight = 256, soldierFrameWidth = 64, soldierFrameHeight = 64;
    //public static int soldierFrames = 9;
    float width, height, frameWidth, frameHeight;

    public SpriteSheetDimensions(String name){
        switch (name){
            case "soldier_attack":
                width = 512;
                height = 512;
                frameWidth = 128;
                frameHeight = 128;
                break;
            case "soldier_idle":
                width = 512;
                height = 512;
                frameWidth = 128;
                frameHeight = 128;
                break;
            case "soldier_move":
                width = 512;
                height = 512;
                frameWidth = 128;
                frameHeight = 128;
                break;
        }
    }
    //public static float soldierWidth = 512, soldierHeight = 512, soldierFrameWidth = 103, soldierFrameHeight = 103;
}
