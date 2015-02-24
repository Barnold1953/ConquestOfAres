package Generation;
import java.lang.Math.*;
import java.nio.*;
import java.util.*;

/**
 * Created by brb55_000 on 1/21/2015.
 */
public class MapGenerator {

    private final int mBytesPerFloat = 4;
    Vertex[] diagram;
    List<Vertex> vertices = new LinkedList<Vertex>();
    Random random = new Random();

    /// Generates a map and stores the result in p
    // TODO(Ben): Finish this
    public void generateMap(MapGenerationParams p) {
        int width = 1;
        int height = 1;
        // TODO: These are arbitrary. Pick better values.
        switch (p.mapSize) {
            case CRAMPED:
                width = 256;
                height = 256;
                break;
            case SMALL:
                width = 512;
                height = 512;
                break;
            case AVERAGE:
                width = 768;
                height = 768;
                break;
            case LARGE:
                width = 1024;
                height = 1024;
                break;
            default:
                break;
        }

        double[][] heightMap = new double[height][width];
        generateHeightmap(width, height, heightMap, p.seed);

        FloatBuffer pixelBuffer = ByteBuffer.allocateDirect(height * width * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();

        // Solid red for now
        for (int i = 0; i < width * height; i++) {
            pixelBuffer.put(1.0f);
            pixelBuffer.put(0.0f);
            pixelBuffer.put(0.0f);
            pixelBuffer.put(1.0f);
        }
        random.setSeed(p.seed);
        generateTerritories(width, height, heightMap);

        // TODO(Ben): Render the heightmap
        // TODO(Ben): Segment heightmap into territories
    }

    /// Generates a raw heightmap
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

    public void generateTerritories(int width, int height, double[][] heightMap) {
        int numPoints = 10;
        Coord[] points = new Coord[numPoints];

        for (int i = 0; i < numPoints; i++) {
            points[i].x = random.nextDouble() * width;
            points[i].y = random.nextDouble() * height;
        }

        diagram = Voronoi.generate(points);
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
            total += ((1.0 - Math.abs(osNoise.eval(x * frequency, y * frequency))) * 2.0 - 1.0) * amplitude;
            frequency *= 2.0;
            maxAmplitude += amplitude;
            amplitude *= persistence;
        }
        return total / maxAmplitude;
    }

    private OpenSimplexNoise osNoise = new OpenSimplexNoise();
}
