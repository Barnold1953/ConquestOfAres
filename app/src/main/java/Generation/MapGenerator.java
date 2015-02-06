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

        // Generate pixel data for texture
        byte[][][] pixelData = new byte[height][width][3];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (heightMap[y][x] > 0.65) { // Mountains
                    pixelData[y][x][0] = 127;
                    pixelData[y][x][1] = 127;
                    pixelData[y][x][2] = 127;
                } else if (heightMap[y][x] < 0.0) { // Oceans
                    pixelData[y][x][0] = 0;
                    pixelData[y][x][1] = 12;
                    pixelData[y][x][2] = 100;
                } else if (heightMap[y][x] < 0.05) { // Beach
                    pixelData[y][x][0] = 120;
                    pixelData[y][x][1] = 100;
                    pixelData[y][x][2] = 80;
                } else { // Grass
                    pixelData[y][x][0] = 0;
                    pixelData[y][x][1] = 80;
                    pixelData[y][x][2] = 0;
                }
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

    private double ridgedOctaveNoise2D(double x, double y, double persistence, double frequency, int octaves) {
        double total = 0.0;
        double amplitude = 1.0;
        double maxAmplitude = 0.0;

        for (int i = 0; i < octaves; i++) {
            total += ((1.0 - abs(osNoise.eval(x * frequency, y * frequency))) * 2.0 - 1.0) * amplitude;
            frequency *= 2.0;
            maxAmplitude += amplitude;
            amplitude *= persistence;
        }
        return total / maxAmplitude;
    }

    private OpenSimplexNoise osNoise;
}
