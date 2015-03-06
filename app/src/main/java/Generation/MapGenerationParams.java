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
        CRAMPED,
        SMALL,
        MEDIUM,
        LARGE,
        MASSIVE
    }

    public enum MapSymmetry {
        NONE,
        HORIZONTAL,
        VERTICAL,
        RADIAL
    }

    /// Parameters
    public int seed = 0; ///< Generation seed. Set by caller
    public MapSize mapSize = MapSize.MEDIUM; ///< Size of map
    public MapSymmetry mapSymmetry = MapSymmetry.NONE; ///< Symmetry of generated map


    /// Returns
    public int texture = 0; ///< ID for the openGL texture that is generated. Gets set by generator
    public Vector<Territory> territories = null; ///< Generated territories. Gets set by generator
}