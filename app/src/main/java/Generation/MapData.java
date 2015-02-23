package Generation;

import java.util.Vector;

import Game.Territory;
import Graphics.ColorMesh;

/**
 * Created by brb55_000 on 2/23/2015.
 */
public class MapData {
    public MapGenerationParams params; ///< The params that were used to generate this data
    public int texture = 0; ///< ID for the openGL texture that is generated. Gets set by generator
    public Vector<Territory> territories = null; ///< Generated territories. Gets set by generator
    public ColorMesh territoryLineMesh; ///< Line mesh for territories
}
