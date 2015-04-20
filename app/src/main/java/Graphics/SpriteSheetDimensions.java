package Graphics;

/**
 * Created by Nathan on 3/28/2015.
 */
public class SpriteSheetDimensions {
    float width, height, frameWidth, frameHeight;
    float u, v, uw, vw;

    public SpriteSheetDimensions(String name, int frame){

        switch (name){
            case "soldier_attack":
                width = 512.0f;
                height = 512.0f;
                frameWidth = 128.0f;
                frameHeight = 128.0f;
                break;
            case "soldier_idle":
                width = 512.0f;
                height = 512.0f;
                frameWidth = 128.0f;
                frameHeight = 128.0f;
                break;
            case "soldier_move":
                width = 512.0f;
                height = 512.0f;
                frameWidth = 128.0f;
                frameHeight = 128.0f;
                break;
        }

        int frameX = (int)width / (int)frameWidth;
        int frameY = (int)height / (int)frameHeight;
        frameY = (frame / frameX) % frameY;
        frameX = frame % frameX;

        // Texture coordinates
        uw = frameWidth / width;
        vw = -frameHeight / height;
        u = frameX * uw;
        v = frameY * vw + vw;
    }
}
