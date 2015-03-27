package Generation;

        import java.nio.ByteBuffer;
        import java.util.Vector;

        import Game.Territory;
        import Graphics.ColorMesh;

/**
 * Created by brb55_000 on 3/2/2015.
 */
public class MapData {
    /// Returns
    public ByteBuffer pixelBuffer; ///< Pixels for the texture
    public ByteBuffer terrainPixelBuffer;
    public int texture = 0; ///< ID for the openGL texture that is generated. Gets set by generator
    public int terrainTexture = 0;
    public Vector<Territory> territories = null; ///< Generated territories. Gets set by generator

    public ColorMesh territoryGraphMesh;
    public MapGenerationParams params;
    public int width;
    public int height;
}
