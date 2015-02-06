package Generation;
import java.util.*;
import Game.Territory;

/**
 * Created by brb55_000 on 2/6/2015.
 */
/// Parameters for generating a map
public class MapGenerationParams {
    /// Parameters
    public int width; ///< Width of the map. Set by caller.
    public int height; ///< Height of the map. Set by caller.
    public int seed; ///< Generation seed. Set by caller
    /// Returns
    public int texture; ///< ID for the openGL texture that is generated. Gets set by generator
    Vector<Territory> territories; ///< Generated territories. Gets set by generator
}
