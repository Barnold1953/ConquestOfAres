package Generation;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

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

    public static MapData mapData;

    /// Generates a map and returns the map data
    // TODO(Ben): Finish this
    public MapData generateMap(MapGenerationParams p) throws BufferOverflowException{
        mapData = new MapData();
        mapData.params = p;

        int width = 1;
        int height = 1;
        // TODO: These are arbitrary. Pick better values.
        switch (p.mapSize) {
            case CRAMPED:
                width = 540;
                height = 960;
                break;
            case SMALL:
                width = 720;
                height = 1280;
                break;
            case MEDIUM:
                width = 900;
                height = 1600;
                break;
            case LARGE:
                width = 1080;
                height = 1920;
                break;
            default:
                break;
        }

        mapData.width = width;
        mapData.height = height;

        // random.setSeed(p.seed);
        generateTerritories(width, height, 100);

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
        return octaveNoise2D((double)(seed + y), (double)(x - seed), 0.86, 0.0015, 7);
    }

    public void generateTerritories(int width, int height, int numTerritories) {
        ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(height * width * 4).order(ByteOrder.nativeOrder());
        ArrayList<Byte> colors = new ArrayList<Byte>();
        pixelBuffer.position(0);
        // Generate random territories
        // TODO(Ben): Generate more uniform points

        float xStep = 64.0f;
        float yStep = 64.0f;
        float minDist = 96.0f*96.0f;
        int maxIteration = 100;
        int pindex = 0;
        float dx, dy;

        mapData.territoryIndices = new int[height][width];

        // Generate territories
        mapData.territories = new Vector<Territory>();
        while (true) {
            Territory territory = new Territory();
            boolean stop = false;
            int iter = 0;
            do {
                territory.x = m_random.nextFloat() * width;
                territory.y = m_random.nextFloat() * height;
                stop = true;
                for (Territory t : mapData.territories) {
                    if (mapData.params.horizontalWrap) {
                        dx = Math.abs(t.x - territory.x);
                        dy = Math.abs(t.y - territory.y);
                        dx = Math.min(dx, width - dx);
                        dy = Math.min(dy, height - dy);
                    } else {
                        dx = t.x - territory.x;
                        dy = t.y - territory.y;
                    }
                    if (dx * dx + dy * dy < minDist) {
                        stop = false;
                        break;
                    }
                }
                iter++;
            } while (stop == false && iter != maxIteration);

            if (iter == maxIteration) {
                break;
            }

            territory.height = (float) getHeightValue((int) territory.x, (int) territory.y, mapData.params.seed);
            // TODO(Ben): More terrain types, humidity / temperature distribution
            if (territory.height < 0.0f) {
                territory.terrainType = Territory.TerrainType.Ocean;
                colors.add((byte)0);
                colors.add((byte)0);
                colors.add((byte)170);
            } else if (territory.height < 0.4f) {
                territory.terrainType = Territory.TerrainType.Grassland;
                colors.add((byte) (m_random.nextFloat() * 255.0f));
                colors.add((byte) (m_random.nextFloat() * 255.0f));
                colors.add((byte) (m_random.nextFloat() * 255.0f));
            } else {
                territory.terrainType = Territory.TerrainType.Mountain;
                colors.add((byte) (m_random.nextFloat() * 255.0f));
                colors.add((byte) (m_random.nextFloat() * 255.0f));
                colors.add((byte) (m_random.nextFloat() * 255.0f));
            }
            mapData.territories.add(territory);
        }
        float c;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                mapData.territoryIndices[y][x] = getClosestTerritoryIndex((float)x, (float)y, mapData.territories);
            }
        }

        // Generate texture for showing the territories
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int closestIndex = mapData.territoryIndices[y][x];
                Territory territory = mapData.territories.get(closestIndex);
                Territory nextTerritory;

                // Check if we are on an edge
                boolean isEdge = false;
                int newIndex;
                if (x < width - 1) {
                    newIndex = mapData.territoryIndices[y][x + 1];
                    if (newIndex != closestIndex) {
                        // Add neighbor territory if needed
                        nextTerritory = mapData.territories.get(newIndex);
                        if (!territory.neighbors.contains(nextTerritory)) {
                            territory.neighbors.add(nextTerritory);
                        }
                        isEdge = true;
                    }
                }
                if (x > 0) {
                    newIndex = mapData.territoryIndices[y][x - 1];
                    if (newIndex != closestIndex) {
                        nextTerritory = mapData.territories.get(newIndex);
                        if (!territory.neighbors.contains(nextTerritory)) {
                            territory.neighbors.add(nextTerritory);
                        }
                        isEdge = true;
                    }
                }
                if (y < height - 1) {
                    newIndex = mapData.territoryIndices[y + 1][x];
                    if (newIndex != closestIndex) {
                        nextTerritory = mapData.territories.get(newIndex);
                        if (!territory.neighbors.contains(nextTerritory)) {
                            territory.neighbors.add(nextTerritory);
                        }
                        isEdge = true;
                    }
                }
                if (y > 0) {
                    newIndex = mapData.territoryIndices[y - 1][x];
                    if (newIndex != closestIndex) {
                        nextTerritory = mapData.territories.get(newIndex);
                        if (!territory.neighbors.contains(nextTerritory)) {
                            territory.neighbors.add(nextTerritory);
                        }
                        isEdge = true;
                    }
                }
                if (isEdge) {
                    pixelBuffer.put((byte)0);
                    pixelBuffer.put((byte)0);
                    pixelBuffer.put((byte)0);
                } else {
                    int cIndex = closestIndex * 3;
                    pixelBuffer.put((byte)(colors.get(cIndex)));
                    pixelBuffer.put((byte)(colors.get(cIndex + 1)));
                    pixelBuffer.put((byte)(colors.get(cIndex + 2)));
                }
                // Alpha
                pixelBuffer.put((byte)255);
            }
        }

        // Add graph lines
        float r, g, b;
        mapData.territoryGraphMesh = new ColorMesh();
        for (Territory t1 : mapData.territories) {
            for (Territory t2 : t1.neighbors) {
                if (t1.terrainType == Territory.TerrainType.Ocean || t2.terrainType == Territory.TerrainType.Ocean) {
                    r = 0;
                    g = 1.0f;
                    b = 1.0f;
                } else {
                    r = 1.0f;
                    g = 0.0f;
                    b = 0.0f;
                }
                mapData.territoryGraphMesh.addVertex((t1.x / width) * 2.0f - 1.0f, (t1.y / height) * 2.0f - 1.0f, 0.0f, r, g, b);
                mapData.territoryGraphMesh.addVertex((t2.x / width) * 2.0f - 1.0f, (t2.y / height) * 2.0f - 1.0f, 0.0f, r, g, b);
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
        float dx, dy;

        x += (float)octaveNoise2D(x, y, MODULATE_PERSISTENCE, MODULATE_FREQUENCY, MODULATE_OCTAVES) * MODULATE_SCALE;
        y += (float)octaveNoise2D(x + 2048.0, y + 2048.0, MODULATE_PERSISTENCE, MODULATE_FREQUENCY, MODULATE_OCTAVES) * MODULATE_SCALE;
        for (int i = 0; i < territories.size(); i++) {
            if (mapData.params.horizontalWrap) {
                dx = Math.abs(x - territories.get(i).x);
                dy = Math.abs(y - territories.get(i).y);
                dx = Math.min(dx, mapData.width - dx);
                dy = Math.min(dy, mapData.height - dy);
            } else {
                dx = x - territories.get(i).x;
                dy = y - territories.get(i).y;
            }
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
