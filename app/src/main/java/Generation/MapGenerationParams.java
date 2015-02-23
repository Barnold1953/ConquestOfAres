package Generation;
import java.util.*;
import Game.Territory;
import Game.GameSettings;
import Graphics.ColorMesh;

/**
 * Created by brb55_000 on 2/6/2015.
 */

/// Parameters for generating a map
public class MapGenerationParams {

    public enum MapSize {
        CRAMPED,
        SMALL,
        AVERAGE,
        LARGE
    }

    enum MapSymmetry {
        NONE,
        HORIZONTAL,
        VERTICAL,
        RADIAL
    }

    /// Parameters
    public int seed = 0; ///< Generation seed. Set by caller
    public MapSize mapSize = MapSize.AVERAGE; ///< Size of map
    public MapSymmetry mapSymmetry = MapSymmetry.NONE; ///< Symmetry of generated map
}