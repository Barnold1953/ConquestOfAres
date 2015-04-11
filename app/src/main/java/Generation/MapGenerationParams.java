package Generation;
import java.util.*;
import Game.Territory;
import Game.GameSettings;

/**
 * Created by brb55_000 on 2/6/2015.
 */

/// Parameters for generating a map
public class MapGenerationParams {

    public enum MapSize {
        TINY(540,960),
        SMALL(720,1280),
        MEDIUM(900,1600),
        LARGE(1080,1920);

        private float width,height;

        MapSize(float x, float y) {
            width = x;
            height = y;
        }

        public float getWidth() {return width;}
        public float getHeight() {return height;}
    }

    public enum MapSymmetry {
        NONE,
        HORIZONTAL,
        VERTICAL,
        RADIAL
    }

    /// Parameters
    public int seed = 150000; ///< Generation seed. Set by caller
    public MapSize mapSize = MapSize.SMALL; ///< Size of map
    public MapSymmetry mapSymmetry = MapSymmetry.NONE; ///< Symmetry of generated map
    public boolean horizontalWrap = false;
}
