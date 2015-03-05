package Generation;

        import java.util.Vector;

        import Game.Territory;
        import Graphics.ColorMesh;

/**
 * Created by brb55_000 on 3/2/2015.
 */
public class MapData {
    /// Returns
    public int texture = 0; ///< ID for the openGL texture that is generated. Gets set by generator
    public Vector<Territory> territories = null; ///< Generated territories. Gets set by generator

    public MapGenerationParams params;
}
