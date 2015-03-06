package Generation;
import android.content.Context;

import java.nio.*;
import java.util.*;

import Game.Territory;
import Graphics.ColorMesh;

/**
 * Created by brb55_000 on 1/21/2015.
 */
public class MapGenerator {

    private static final int BYTES_PER_FLOAT = 4;
    private static Random m_random = new Random();

    // Change these to affect the border modulation
    final static float MODULATE_SCALE = 32.0f; ///< Size of variation
    final static double MODULATE_PERSISTENCE = 0.8; ///< How much weight is passed down to each consecutive octave
    final static double MODULATE_FREQUENCY = 0.01; ///< Frequency of the modulation
    final static int MODULATE_OCTAVES = 5; ///< Number of fractal iterations
    private static OpenSimplexNoise osNoise = new OpenSimplexNoise();

    /// Generates a map and returns the map data
    // TODO(Ben): Finish this
    public MapData generateMap(MapGenerationParams p) throws BufferOverflowException{
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
            case MEDIUM:
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

        mapData.width = width;
        mapData.height = height;

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
        generateTerritories(mapData, width, height, 100);

        // TODO(Ben): Segment heightmap into territories
        return mapData;
    }

    /// Generates a raw heightmap
    public void generateHeightmap(int width, int height, double[][] heightMap, int seed) {
        // Generate the height data
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                heightMap[y][x] = getHeightValue(x, y, seed);
            }
        }
    }

    public double getHeightValue(int x, int y, int seed) {
        return octaveNoise2D((double)(seed + y), (double)(x - seed), 0.86, 0.003, 8);
    }

    public void generateTerritories(MapData mapData, int width, int height, int numTerritories) {

        ArrayList<Byte> colors = new ArrayList<Byte>();
        // Generate random territories
        // TODO(Ben): Generate more uniform points

        float xStep = 64.0f;
        float yStep = 64.0f;
        float minDist = 64.0f;
        int maxIteration = 1000;

        mapData.territories = new Vector<Territory>();
        for (float i = yStep / 2; i < height; i += yStep) {
            for (float j = xStep / 2; j < width; j += xStep) {

                Territory territory = new Territory();
                boolean stop = false;
                int iter = 0;
                do {
                    territory.x = m_random.nextFloat() * width;
                    territory.y = m_random.nextFloat() * height;
                    stop = true;
                    for (Territory t : mapData.territories) {
                        float dx = t.x - territory.x;
                        float dy = t.y - territory.y;
                        float dist = (float)Math.sqrt(dx * dx + dy * dy);
                        if (dist < minDist) {
                            stop = false;
                            break;
                        }
                    }
                    iter++;
                } while (stop == false && iter != maxIteration);

                if (iter == maxIteration) {
                    i = height;
                    break;
                }

                territory.height = (float) getHeightValue((int) territory.x, (int) territory.y, 0);
                // TODO(Ben): More terrain types, humidity / temperature distribution
                if (territory.height < 0.0f) {
                    territory.terrainType = Territory.TerrainType.Ocean;
                } else if (territory.height < 0.4f) {
                    territory.terrainType = Territory.TerrainType.Grassland;
                } else {
                    territory.terrainType = Territory.TerrainType.Mountain;
                }
                mapData.territories.add(territory);

                colors.add((byte) (m_random.nextFloat() * 255.0f));
                colors.add((byte) (m_random.nextFloat() * 255.0f));
                colors.add((byte) (m_random.nextFloat() * 255.0f));
            }
        }

        ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(height * width * 4 * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder());

        // Generate texture for showing the territories
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int closestIndex = getClosestTerritoryIndex(x, y, mapData.territories);
                Territory territory = mapData.territories.get(closestIndex);
                Territory nextTerritory;

                // Check if we are on an edge
                boolean isEdge = false;
                int newIndex;
                newIndex = getClosestTerritoryIndex((float)x + 1, (float)y, mapData.territories);
                if (newIndex != closestIndex) {
                    // Add neighbor territory if needed
                    nextTerritory = mapData.territories.get(newIndex);
                    if (!territory.neighbors.contains(nextTerritory)) {
                        territory.neighbors.add(nextTerritory);
                    }
                    isEdge = true;
                }
                newIndex = getClosestTerritoryIndex((float)x - 1, (float)y, mapData.territories);
                if (newIndex != closestIndex) {
                    nextTerritory = mapData.territories.get(newIndex);
                    if (!territory.neighbors.contains(nextTerritory)) {
                        territory.neighbors.add(nextTerritory);
                    }
                    isEdge = true;
                }
                newIndex = getClosestTerritoryIndex((float)x, (float)y + 1, mapData.territories);
                if (newIndex != closestIndex) {
                    nextTerritory = mapData.territories.get(newIndex);
                    if (!territory.neighbors.contains(nextTerritory)) {
                        territory.neighbors.add(nextTerritory);
                    }
                    isEdge = true;
                }
                newIndex = getClosestTerritoryIndex((float)x, (float)y - 1, mapData.territories);
                if (newIndex != closestIndex) {
                    nextTerritory = mapData.territories.get(newIndex);
                    if (!territory.neighbors.contains(nextTerritory)) {
                        territory.neighbors.add(nextTerritory);
                    }
                    isEdge = true;
                }
                if (isEdge) {
                    pixelBuffer.put((byte)0);
                    pixelBuffer.put((byte)0);
                    pixelBuffer.put((byte)0);
                } else {
                    int cIndex = closestIndex * 3;
                    pixelBuffer.put(colors.get(cIndex));
                    pixelBuffer.put(colors.get(cIndex + 1));
                    pixelBuffer.put(colors.get(cIndex + 2));
                }
                // Alpha
                pixelBuffer.put((byte)255);
            }
        }

        // Add graph lines
        mapData.territoryGraphMesh = new ColorMesh();
        for (Territory t1 : mapData.territories) {
            for (Territory t2 : t1.neighbors) {
                mapData.territoryGraphMesh.addVertex((t1.x / width) * 2.0f - 1.0f, (t1.y / height) * 2.0f - 1.0f, 0.0f, 1.0f, 0.0f, 0.0f);
                mapData.territoryGraphMesh.addVertex((t2.x / width) * 2.0f - 1.0f, (t2.y / height) * 2.0f - 1.0f, 0.0f, 1.0f, 0.0f, 0.0f);
            }
        }
        mapData.pixelBuffer = pixelBuffer;
    }

    public static Territory getClosestTerritory(float x, float y, Vector<Territory> territories) {
        return territories.get(getClosestTerritoryIndex(x, y, territories));
    }

    private static int getClosestTerritoryIndex(float x, float y, Vector<Territory> territories) {
        int closestIndex = 0;
        float closestDist = 99999999999.9f;

        x += (float)octaveNoise2D(x, y, MODULATE_PERSISTENCE, MODULATE_FREQUENCY, MODULATE_OCTAVES) * MODULATE_SCALE;
        y += (float)octaveNoise2D(x + 2048.0, y + 2048.0, MODULATE_PERSISTENCE, MODULATE_FREQUENCY, MODULATE_OCTAVES) * MODULATE_SCALE;
        for (int i = 0; i < territories.size(); i++) {
            float dx = (float)x - territories.get(i).x;
            float dy = (float)y - territories.get(i).y;
            float dist = dx * dx + dy * dy;
            if (dist < closestDist) {
                closestDist = dist;
                closestIndex = i;
            }
        }
        return closestIndex;
    }

    private static double octaveNoise2D(double x, double y, double persistence, double frequency, int octaves) {
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

    private static double ridgedOctaveNoise2D(double x, double y, double persistence, double frequency, int octaves) {
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
}
