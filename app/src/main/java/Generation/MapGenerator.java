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

        /*ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(height * width * 4 * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder());

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

        mapData.texture = Graphics.TextureHelper.dataToTexture(pixelBuffer, "gentest", width, height); */

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
        int numPoints = 100;
        ArrayList<Float> points = new ArrayList<Float>();
        ArrayList<Byte> colors = new ArrayList<Byte>();
        // I hate java

        for (int i = 0; i < numPoints; i++) {
            points.add(m_random.nextFloat() * width);
            points.add(m_random.nextFloat() * height);
            colors.add((byte)(m_random.nextFloat() * 255.0f));
            colors.add((byte)(m_random.nextFloat() * 255.0f));
            colors.add((byte)(m_random.nextFloat() * 255.0f));
        }

        ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(height * width * 4 * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float closestDist = 9999999999.0f;
                int closestIndex = 0;
                for (int i = 0; i < numPoints; i++) {
                    float dx = (float)x - points.get(i * 2);
                    float dy = (float)y - points.get(i * 2 + 1);
                    float dist = (float)Math.sqrt(dx * dx + dy * dy);
                    if (dist < closestDist) {
                        closestDist = dist;
                        closestIndex = i;
                    }
                }
                int cIndex = closestIndex * 3;
                pixelBuffer.put(colors.get(cIndex));
                pixelBuffer.put(colors.get(cIndex + 1));
                pixelBuffer.put(colors.get(cIndex + 2));
                // Alpha
                pixelBuffer.put((byte)255);
            }
        }

        mapData.texture = Graphics.TextureHelper.dataToTexture(pixelBuffer, "vortest", width, height);

       /* DelaunayTriangulator triangulator = new DelaunayTriangulator();
        ArrayList<Integer> indices = triangulator.computeTriangles(points, false);

        mapData.territoryLineMesh = new ColorMesh(context);

        for (int i = 0; i < indices.size(); i+=3) {
            float r = m_random.nextFloat();
            float g = m_random.nextFloat();
            float b = m_random.nextFloat();
            for (int j = 0; j < 3; j++) {
                int index = indices.get(i + j);
                mapData.territoryLineMesh.addVertex(points.get(index) * 2.0f - 1.0f, points.get(index + 1) * 2.0f - 1.0f, 0.0f, r, g, b);
            }
        }

        mapData.territoryLineMesh.finish(); */
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