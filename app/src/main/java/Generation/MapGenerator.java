package Generation;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

import java.nio.*;
import java.util.*;

import Game.Territory;
import Graphics.ColorMesh;
import Utils.Utils;

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

        mapData.width = p.mapSize.getWidth();
        mapData.height = p.mapSize.getHeight();

        // random.setSeed(p.seed);
        generateTerritories(Utils.fastFloor(mapData.width), Utils.fastFloor(mapData.height), 100);

        // TODO(Ben): Segment heightmap into territories
        mapData.isDoneGenerating = true;
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

        ArrayList<Byte> colors = new ArrayList<Byte>();
        // Generate random territories
        // TODO(Ben): Generate more uniform points

        float xStep = 64.0f;
        float yStep = 64.0f;
        final float minDist = 96.0f;
        final float minDist2 = minDist * minDist;
        int maxIteration = 100;
        int pindex = 0;
        float dx, dy;

        mapData.territoryIndices = new int[Utils.fastFloor(height)][Utils.fastFloor(width)];

        // Determine bounds for placing territories based on wrap mode
        float startX = 0.0f, startY = 0.0f, genWidth = width, genHeight = height;
        switch(mapData.params.mapSymmetry) {
            case HORIZONTAL:
                startX = minDist / 2.0f;
                genWidth = width / 2.0f - startX * 2.0f;
                break;
            case VERTICAL:
                startY = minDist / 2.0f;
                genHeight = height / 2.0f - startY * 2.0f;
                break;
            case RADIAL:
                startX = minDist / 2.0f;
                genWidth = width / 2.0f - startX * 2.0f;
                startY = minDist / 2.0f;
                genHeight = height / 2.0f - startY * 2.0f;
                break;
        }

        // Generate territories
        mapData.territories = new Vector<Territory>();
        while (true) {
            Territory territory = new Territory();
            boolean stop = false;
            int iter = 0;
            do {
                territory.x = m_random.nextFloat() * genWidth + startX;
                territory.y = m_random.nextFloat() * genHeight + startY;
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
                    if (dx * dx + dy * dy < minDist2) {
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
            mapData.territories.add(territory);
        }

        // Clone territories for symmetry
        int size = mapData.territories.size();
        switch(mapData.params.mapSymmetry) {
            case HORIZONTAL:
                for (int i = 0; i < size; i++) {
                    Territory newTerritory = new Territory(mapData.territories.get(i));
                    newTerritory.x = width - newTerritory.x;
                    mapData.territories.add(newTerritory);
                }
                break;
            case RADIAL:
                for (int i = 0; i < size; i++) {
                    Territory newTerritory = new Territory(mapData.territories.get(i));
                    newTerritory.x = width - newTerritory.x;
                    mapData.territories.add(newTerritory);
                }
                size = mapData.territories.size();
                // Purposely omitting break!
            case VERTICAL:
                for (int i = 0; i < size; i++) {
                    Territory newTerritory = new Territory(mapData.territories.get(i));
                    newTerritory.y = height - newTerritory.y;
                    mapData.territories.add(newTerritory);
                }
                break;
        }

        // Set color values
        for (Territory t : mapData.territories) {
            if (t.height < 0.0f) {
                t.terrainType = Territory.TerrainType.Ocean;
                colors.add((byte)0);
                colors.add((byte)0);
                colors.add((byte)170);
            } else if (t.height < 0.4f) {
                t.terrainType = Territory.TerrainType.Grassland;
                colors.add((byte) (m_random.nextFloat() * 255.0f));
                colors.add((byte) (m_random.nextFloat() * 255.0f));
                colors.add((byte) (m_random.nextFloat() * 255.0f));
            } else {
                t.terrainType = Territory.TerrainType.Mountain;
                colors.add((byte) (m_random.nextFloat() * 255.0f));
                colors.add((byte) (m_random.nextFloat() * 255.0f));
                colors.add((byte) (m_random.nextFloat() * 255.0f));
            }
        }

        float c;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int tIndex = getClosestTerritoryIndex((float)x, (float)y, mapData.territories);
                Territory t = mapData.territories.get(tIndex);
                if (x < t.textureX) {
                    t.textureX = x;
                } else if (x > t.maxX) {
                    t.maxX = x;
                }
                if (y < t.textureY) {
                    t.textureY = y;
                } else if (y > t.maxY) {
                    t.maxY = y;
                }
                mapData.territoryIndices[y][x] = tIndex;
            }
        }

        // Generate all the textures for the territories
        for (int i = 0; i < mapData.territories.size(); i++) {
            Territory t = mapData.territories.get(i);
            t.index = i;
            t.textureWidth = t.maxX - t.textureX;
            t.textureHeight = t.maxY - t.textureY;
            t.pixelBuffer = ByteBuffer.allocateDirect(t.textureHeight * t.textureWidth * 4).order(ByteOrder.nativeOrder());
            for (int y = t.textureY; y < t.maxY; y++) {
                for (int x = t.textureX; x < t.maxX; x++) {
                    if (mapData.territoryIndices[y][x] != i) {
                        t.pixelBuffer.put((byte) 0);
                        t.pixelBuffer.put((byte) 0);
                        t.pixelBuffer.put((byte) 0);
                        t.pixelBuffer.put((byte) 0);
                    } else {
                        int newIndex;
                        boolean isEdge = false;
                        Territory nextTerritory;
                        if (x < width - 1) {
                            newIndex = mapData.territoryIndices[y][x + 1];
                            if (newIndex != i) {
                                // Add neighbor territory if needed
                                nextTerritory = mapData.territories.get(newIndex);
                                if (!t.neighbors.contains(nextTerritory)) {
                                    t.neighbors.add(nextTerritory);
                                }
                                isEdge = true;
                            }
                        }
                        if (x > 0) {
                            newIndex = mapData.territoryIndices[y][x - 1];
                            if (newIndex != i) {
                                nextTerritory = mapData.territories.get(newIndex);
                                if (!t.neighbors.contains(nextTerritory)) {
                                    t.neighbors.add(nextTerritory);
                                }
                                isEdge = true;
                            }
                        }
                        if (y < height - 1) {
                            newIndex = mapData.territoryIndices[y + 1][x];
                            if (newIndex != i) {
                                nextTerritory = mapData.territories.get(newIndex);
                                if (!t.neighbors.contains(nextTerritory)) {
                                    t.neighbors.add(nextTerritory);
                                }
                                isEdge = true;
                            }
                        }
                        if (y > 0) {
                            newIndex = mapData.territoryIndices[y - 1][x];
                            if (newIndex != i) {
                                nextTerritory = mapData.territories.get(newIndex);
                                if (!t.neighbors.contains(nextTerritory)) {
                                    t.neighbors.add(nextTerritory);
                                }
                                isEdge = true;
                            }
                        }
                        if (isEdge) {
                            t.pixelBuffer.put((byte) 0);
                            t.pixelBuffer.put((byte) 0);
                            t.pixelBuffer.put((byte) 0);
                        } else {
                            t.pixelBuffer.put((byte) 255);
                            t.pixelBuffer.put((byte) 255);
                            t.pixelBuffer.put((byte) 255);
                        }
                        // Alpha
                        t.pixelBuffer.put((byte) 255);
                    }
                }
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
