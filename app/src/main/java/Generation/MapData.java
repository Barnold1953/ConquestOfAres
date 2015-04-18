package Generation;

        import java.nio.ByteBuffer;
        import java.util.Vector;

        import Game.Territory;
        import Graphics.ColorMesh;

/**
 * Created by brb55_000 on 3/2/2015.
 */
public class MapData {
    public MapData() {};
    /// Returns
    public boolean isDoneGenerating = false;
    public Vector<Territory> territories = null; ///< Generated territories. Gets set by generator
    public int[][] territoryIndices = null;
    public ColorMesh territoryGraphMesh;
    public MapGenerationParams params;
    public float width;
    public float height;
}