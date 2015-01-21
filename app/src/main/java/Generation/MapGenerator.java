package Generation;

/**
 * Created by brb55_000 on 1/21/2015.
 */
public class MapGenerator {
    public MapGenerator() {
        osNoise = new OpenSimplexNoise();
    }

    public void generateMap(int width, int height, int seed) {
        double[][] heightMap = new double[height][width];

        generateHeightmap(width, height, heightMap, seed);

        // TODO(Ben): Render the heightmap
        // TODO(Ben): Segment heightmap into territories
    }

    public void generateHeightmap(int width, int height, double[][] heightMap, int seed) {
        // Generate the height data
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                heightMap[y][x] = octaveNoise2D((double)(seed + y), (double)(x - seed),
                        0.9, 0.001, 8);
            }
        }
    }

    private double octaveNoise2D(double x, double y, double persistence, double frequency, int octaves) {
        double total = 0.0;
        double amplitude = 1.0;
        double maxAmplitude = 0.0;

        for (int i = 0; i < octaves; i++) {
            total += osNoise.eval(x * frequency, y * frequency) * amplitude;
            frequency *= 2.0;
            maxAmplitude += amplitude;
            amplitude *= persistence;
        }
        return total / maxAmplitude;
    }

    private OpenSimplexNoise osNoise;
}
