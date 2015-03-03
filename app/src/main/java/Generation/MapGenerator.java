package Generation;
import android.content.Context;

import java.nio.*;
import java.util.*;

import Graphics.ColorMesh;

/**
 * Created by brb55_000 on 1/21/2015.
 */
public class MapGenerator {

    private final int BYTES_PER_FLOAT = 4;
    private Vertex[] m_diagram;
    private List<Vertex> m_vertices = new LinkedList<Vertex>();
    private Random m_random = new Random();

    /// Generates a map and returns the map data
    // TODO(Ben): Finish this
    public MapData generateMap(Context c, MapGenerationParams p) throws BufferOverflowException{
        MapData mapData = new MapData();
        mapData.params = p;

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

        ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(height * width * 4 * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (heightMap[y][x] > 0.25) { // Mountains
                    pixelBuffer.put((byte)225);
                    pixelBuffer.put((byte)225);
                    pixelBuffer.put((byte)225);
                } else if (heightMap[y][x] < 0.0) { // Oceans
                    pixelBuffer.put((byte)0);
                    pixelBuffer.put((byte)0);
                    pixelBuffer.put((byte)170);
                } else if (heightMap[y][x] < 0.05) { // Beach
                    pixelBuffer.put((byte)130);
                    pixelBuffer.put((byte)110);
                    pixelBuffer.put((byte)90);
                } else { // Grass
                    pixelBuffer.put((byte)0);
                    pixelBuffer.put((byte)180);
                    pixelBuffer.put((byte)0);
                }
                // Alpha
                pixelBuffer.put((byte)255);
            }
        }

        mapData.texture = Graphics.TextureHelper.dataToTexture(pixelBuffer, "gentest", width, height);

        // random.setSeed(p.seed);
        generateTerritories(c, mapData, width, height, heightMap);

        // TODO(Ben): Segment heightmap into territories
        return mapData;
    }

    /// Generates a raw heightmap
    public void generateHeightmap(int width, int height, double[][] heightMap, int seed) {
        // Generate the height data
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                heightMap[y][x] = octaveNoise2D((double)(seed + y), (double)(x - seed),
                        0.86, 0.003, 8);
            }
        }
    }

    public void generateTerritories(Context context, MapData mapData, int width, int height, double[][] heightMap) {
        int numPoints = 10;
        Coord[] points = new Coord[numPoints];
        // I hate java
        for (int i = 0; i < numPoints; i++) {
            points[i] = new Coord();
        }

        for (Coord c : points) {
            c.x = m_random.nextDouble() * width;
            c.y = m_random.nextDouble() * height;
        }

        m_diagram = Voronoi.generate(points);

        mapData.territoryLineMesh = new ColorMesh(context);

        for (Vertex v : m_diagram) {
            for (VoronoiSegment s : v.edges) {
                mapData.territoryLineMesh.addVertex(((float)s.v1.x / (float)width) * 2.0f - 1.0f, ((float)s.v1.y / (float)height) * 2.0f - 1.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                mapData.territoryLineMesh.addVertex(((float)s.v2.x / (float)width) * 2.0f - 1.0f, ((float)s.v2.y / (float)height) * 2.0f - 1.0f, 0.0f, 1.0f, 1.0f, 1.0f);
            }
            break;
        }
        mapData.territoryLineMesh.finish();
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